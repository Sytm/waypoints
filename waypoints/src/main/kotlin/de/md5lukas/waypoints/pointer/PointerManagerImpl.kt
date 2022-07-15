package de.md5lukas.waypoints.pointer

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.PointerManager
import de.md5lukas.waypoints.api.Trackable
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.event.TrackableDeselectEvent
import de.md5lukas.waypoints.api.event.TrackableSelectEvent
import de.md5lukas.waypoints.api.event.WaypointPostDeleteEvent
import de.md5lukas.waypoints.config.pointers.PointerConfiguration
import de.md5lukas.waypoints.pointer.variants.*
import de.md5lukas.waypoints.util.callEvent
import de.md5lukas.waypoints.util.runTask
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

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
                    CompassPointer(plugin, this)
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

    override fun enable(player: Player, trackable: Trackable) {
        if (trackable.location.world === null) {
            throw IllegalStateException("The waypoint to activate the pointers to has no world available")
        }

        val newPointer = ActivePointer(plugin, player, trackable)
        activePointers.put(player, newPointer)?.let { oldPointer ->
            hide(player, oldPointer)
        }
        show(player, newPointer)
        plugin.api.getWaypointPlayer(player.uniqueId).lastSelectedWaypoint = if (trackable is Waypoint) {
            trackable
        } else {
            null
        }
    }

    override fun disable(player: Player) = disable(player, true)

    private fun disable(player: Player, save: Boolean) {
        activePointers.remove(player)?.let {
            hide(player, it)
        }
        if (save) {
            plugin.api.getWaypointPlayer(player.uniqueId).lastSelectedWaypoint = null
        }
    }

    override fun disableAll(id: UUID) {
        activePointers.filter { it.value.trackable.id == id }.forEach {
            disable(it.key)
        }
    }

    override fun trackableOf(player: Player) = PlayerTrackable(player)

    override fun trackableOf(location: Location) = StaticTrackableImpl(location)

    override fun temporaryWaypointTrackableOf(location: Location) = TemporaryWaypointTrackable(location)

    override fun getCurrentTarget(player: Player): Trackable? = activePointers[player]?.trackable

    private fun show(player: Player, pointer: ActivePointer) {
        val trackable = pointer.trackable
        plugin.callEvent(TrackableSelectEvent(player, trackable))
        enabledPointers.forEach {
            it.show(player, pointer.trackable, pointer.translatedTarget)
        }
    }

    private fun hide(player: Player, pointer: ActivePointer) {
        val trackable = pointer.trackable
        plugin.callEvent(TrackableDeselectEvent(player, trackable))
        enabledPointers.forEach {
            it.hide(player, pointer.trackable, pointer.translatedTarget)
        }
    }

    @EventHandler
    private fun onPlayerJoin(e: PlayerJoinEvent) {
        plugin.runTask { // Run this in the next tick, because otherwise the compass pointer errors because the player doesn't have a current compass target yet
            val waypoint = plugin.api.getWaypointPlayer(e.player.uniqueId).lastSelectedWaypoint
            if (waypoint != null) {
                enable(e.player, waypoint)
            }
        }
    }

    @EventHandler
    private fun onQuit(e: PlayerQuitEvent) {
        disable(e.player, false)
    }

    @EventHandler
    private fun onMove(e: PlayerMoveEvent) {
        val disableWhenReachedRadius = plugin.waypointsConfig.pointer.disableWhenReachedRadius

        if (disableWhenReachedRadius == 0) {
            return
        }

        val pointer = activePointers[e.player] ?: return

        if (e.player.world === pointer.trackable.location.world &&
            e.player.location.distanceSquared(pointer.trackable.location) <= disableWhenReachedRadius
        ) {
            disable(e.player)
        }
    }

    @EventHandler
    private fun onWaypointDelete(e: WaypointPostDeleteEvent) {
        disableAll(e.waypoint.id)
    }

    init {
        setupPointers()
    }
}