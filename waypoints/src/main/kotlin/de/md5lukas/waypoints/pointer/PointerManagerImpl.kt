package de.md5lukas.waypoints.pointer

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.PointerManager
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.event.WaypointDeselectEvent
import de.md5lukas.waypoints.api.event.WaypointSelectEvent
import de.md5lukas.waypoints.config.pointers.PointerConfiguration
import de.md5lukas.waypoints.pointer.variants.*
import de.md5lukas.waypoints.util.callEvent
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import kotlin.math.floor

class PointerManagerImpl(
    private val plugin: WaypointsPlugin
) : PointerManager, Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    private val availablePointers: List<(PointerConfiguration) -> Pointer?> = listOf(
        {
            with(it.actionBar) {
                if (enabled) {
                    ActionBarPointer(plugin, this)
                } else {
                    null
                }
            }
        }, {
            with(it.beacon) {
                if (enabled) {
                    BeaconPointer(plugin, this)
                } else {
                    null
                }
            }
        }, {
            with(it.blinkingBlock) {
                if (enabled) {
                    BlinkingBlockPointer(plugin, this)
                } else {
                    null
                }
            }
        }, {
            with(it.compass) {
                if (enabled) {
                    CompassPointer(plugin)
                } else {
                    null
                }
            }
        }, {
            with(it.particle) {
                if (enabled) {
                    ParticlePointer(plugin, this)
                } else {
                    null
                }
            }
        }, {
            with(it.hologram) {
                if (enabled && plugin.server.pluginManager.isPluginEnabled("ProtocolLib")) {
                    HologramPointer(plugin, this)
                } else {
                    null
                }
            }
        }
    )

    private val enabledPointers: MutableList<Pointer> = ArrayList()

    private val activePointers: MutableMap<Player, ActivePointer> = HashMap()


    private fun setupPointers() {
        availablePointers.forEach { supplier ->
            supplier(plugin.waypointsConfig.pointer)?.let { pointer ->
                enabledPointers.add(pointer)

                if (pointer.interval > 0) {
                    plugin.server.scheduler.runTaskTimer(plugin, PointerTask(pointer, activePointers), 0, pointer.interval.toLong())
                }
            }
        }
    }

    override fun enable(player: Player, waypoint: Waypoint) {
        if (waypoint.location.world === null) {
            throw IllegalStateException("The waypoint to activate the pointers to has no world available")
        }

        val newPointer = ActivePointer(waypoint, translateTargetLocation(player, waypoint))
        activePointers.put(player, newPointer)?.let { oldPointer ->
            hide(player, oldPointer)
        }
        show(player, newPointer)
    }

    override fun disable(player: Player) {
        activePointers.remove(player)?.let {
            hide(player, it)
        }
    }

    private fun show(player: Player, pointer: ActivePointer) {
        plugin.callEvent(WaypointSelectEvent(player, pointer.waypoint))
        enabledPointers.forEach {
            it.show(player, pointer.waypoint, pointer.translatedTarget)
        }
    }

    private fun hide(player: Player, pointer: ActivePointer) {
        plugin.callEvent(WaypointDeselectEvent(player, pointer.waypoint))
        enabledPointers.forEach {
            it.hide(player, pointer.waypoint, pointer.translatedTarget)
        }
    }

    @EventHandler
    private fun onQuit(e: PlayerQuitEvent) {
        disable(e.player)
    }

    @EventHandler
    private fun onMove(e: PlayerMoveEvent) {
        val disableWhenReachedRadius = plugin.waypointsConfig.pointer.disableWhenReachedRadius

        if (disableWhenReachedRadius == 0) {
            return
        }

        val pointer = activePointers[e.player] ?: return

        if (e.player.world === pointer.waypoint.location.world &&
            e.player.location.distanceSquared(pointer.waypoint.location) <= disableWhenReachedRadius
        ) {
            disable(e.player)
        }
    }

    @EventHandler
    private fun onWorldChange(e: PlayerChangedWorldEvent) {
        activePointers[e.player]?.let {
            it.translatedTarget = translateTargetLocation(e.player, it.waypoint)
        }
    }

    private fun translateTargetLocation(player: Player, waypoint: Waypoint): Location? {
        if (player.world === waypoint.location.world) {
            return waypoint.location
        }

        plugin.waypointsConfig.general.connectedWorlds.worlds.forEach {
            if (it.primary == player.world.name || it.secondary == waypoint.location.world?.name
                && it.secondary == player.world.name || it.primary == waypoint.location.world?.name
            ) {
                val target = waypoint.location.clone()
                target.world = player.world

                if (player.world.name == it.primary) {
                    target.x *= 8
                    target.z *= 8
                } else {
                    target.x = floor(target.x / 8)
                    target.z = floor(target.z / 8)
                }

                return target
            }
        }

        return null
    }

    init {
        setupPointers()
    }
}