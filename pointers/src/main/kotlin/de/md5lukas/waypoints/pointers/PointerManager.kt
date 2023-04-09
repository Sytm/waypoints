package de.md5lukas.waypoints.pointers

import de.md5lukas.waypoints.pointers.config.PointerConfiguration
import de.md5lukas.waypoints.pointers.variants.*
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.*

class PointerManager(
    internal val plugin: Plugin,
    internal val hooks: Hooks,
    internal var configuration: PointerConfiguration,
) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    private val availablePointers: List<(PointerConfiguration) -> Pointer?> = listOf(
        {
            with(it.actionBar) {
                if (enabled) {
                    ActionBarPointer(this@PointerManager, this)
                } else {
                    null
                }
            }
        }, {
            with(it.beacon) {
                if (enabled) {
                    BeaconPointer(this@PointerManager, this)
                } else {
                    null
                }
            }
        }, {
            with(it.blinkingBlock) {
                if (enabled) {
                    BlinkingBlockPointer(this@PointerManager, this)
                } else {
                    null
                }
            }
        }, {
            with(it.compass) {
                if (enabled) {
                    CompassPointer(this@PointerManager, this)
                } else {
                    null
                }
            }
        }, {
            with(it.particle) {
                if (enabled) {
                    ParticlePointer(this@PointerManager, this)
                } else {
                    null
                }
            }
        }, {
            with(it.hologram) {
                if (enabled && plugin.server.pluginManager.isPluginEnabled("ProtocolLib")) {
                    HologramPointer(this@PointerManager, this)
                } else {
                    null
                }
            }
        }, {
            with(it.bossBar) {
                if (enabled) {
                    BossBarPointer(this@PointerManager, this)
                } else {
                    null
                }
            }
        }
    )

    private val enabledPointers: MutableList<Pointer> = ArrayList()
    private val enabledPointerTasks: MutableList<BukkitTask> = ArrayList()

    private val activePointers: MutableMap<Player, ActivePointer> = HashMap()


    private fun setupPointers() {
        availablePointers.forEach { supplier ->
            supplier(configuration)?.let { pointer ->
                enabledPointers.add(pointer)

                if (pointer.interval > 0) {
                    enabledPointerTasks.add(plugin.server.scheduler.runTaskTimer(plugin, PointerTask(pointer, activePointers), 0, pointer.interval.toLong()))
                }
            }
        }
    }

    fun applyNewConfiguration(newConfiguration: PointerConfiguration) {
        enabledPointerTasks.forEach(BukkitTask::cancel)
        enabledPointerTasks.clear()

        val activePointersCopy = HashMap(activePointers)
        activePointersCopy.keys.forEach {
            disable(it, false)
        }

        enabledPointers.clear()

        configuration = newConfiguration

        setupPointers()
        activePointersCopy.forEach { (player, activePointer) ->
            enable(player, activePointer.trackable)
        }
    }

    fun enable(player: Player, trackable: Trackable) {
        if (trackable.location.world === null) {
            throw IllegalStateException("The waypoint to activate the pointers to has no world available")
        }

        val newPointer = ActivePointer(this, player, trackable)
        activePointers.put(player, newPointer)?.let { oldPointer ->
            hide(player, oldPointer)
        }
        show(player, newPointer)
        hooks.saveActiveTrackable(player, trackable)
    }

    fun disable(player: Player) = disable(player, true)

    private fun disable(player: Player, save: Boolean) {
        activePointers.remove(player)?.let {
            hide(player, it)
        }
        if (save) {
            hooks.saveActiveTrackable(player, null)
        }
    }

    fun disableAll(id: UUID) {
        activePointers.filter { it.value.trackable.id == id }.forEach {
            disable(it.key)
        }
    }

    fun getCurrentTarget(player: Player): Trackable? = activePointers[player]?.trackable

    private fun show(player: Player, pointer: ActivePointer) {
        val trackable = pointer.trackable
        plugin.server.pluginManager.callEvent(TrackableSelectEvent(player, trackable))
        enabledPointers.forEach {
            it.show(player, pointer.trackable, pointer.translatedTarget)
        }
    }

    private fun hide(player: Player, pointer: ActivePointer) {
        val trackable = pointer.trackable
        plugin.server.pluginManager.callEvent(TrackableDeselectEvent(player, trackable))
        enabledPointers.forEach {
            it.hide(player, pointer.trackable, pointer.translatedTarget)
        }
    }

    @EventHandler
    private fun onPlayerJoin(e: PlayerJoinEvent) {
        plugin.server.scheduler.runTask(
            plugin,
            Runnable { // Run this in the next tick, because otherwise the compass pointer errors because the player doesn't have a current compass target yet
                hooks.loadActiveTrackable(e.player)?.let {
                    enable(e.player, it)
                }
            }
        )
    }

    @EventHandler
    private fun onQuit(e: PlayerQuitEvent) {
        disable(e.player, false)
    }

    @EventHandler
    private fun onMove(e: PlayerMoveEvent) {
        val pointer = getCurrentTarget(e.player) ?: return

        val disableWhenReachedRadius = configuration.disableWhenReachedRadiusSquared

        if (disableWhenReachedRadius == 0) {
            return
        }

        if (e.player.world === pointer.location.world) {
            val distance = e.player.location.distanceSquared(pointer.location)

            if (distance <= disableWhenReachedRadius) {
                disable(e.player)
            }
        }
    }

    @EventHandler
    private fun onPluginDisable(e: PluginDisableEvent) {
        if (e.plugin !== plugin)
            return

        plugin.server.onlinePlayers.forEach {
            disable(it, false)
        }
    }

    init {
        setupPointers()
    }

    interface Hooks {
        val actionBarHooks: ActionBar

        val hologramHooks: Hologram

        fun saveActiveTrackable(player: Player, tracked: Trackable?)

        fun loadActiveTrackable(player: Player): Trackable?

        fun saveCompassTarget(player: Player, location: Location)

        fun loadCompassTarget(player: Player): Location?

        interface ActionBar {
            fun formatDistanceMessage(player: Player, distance3D: Double, heightDifference: Double): Component

            fun formatWrongWorldMessage(player: Player, current: World, correct: World): Component
        }

        interface Hologram {
            fun formatHologramText(player: Player, trackable: Trackable): Component?

            fun getIconMaterial(trackable: Trackable): Material?
        }
    }
}