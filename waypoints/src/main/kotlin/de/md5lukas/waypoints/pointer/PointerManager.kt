package de.md5lukas.waypoints.pointer

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.event.WaypointDeselectEvent
import de.md5lukas.waypoints.api.event.WaypointSelectEvent
import de.md5lukas.waypoints.config.pointers.PointerConfiguration
import de.md5lukas.waypoints.pointer.variants.*
import de.md5lukas.waypoints.util.callEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

class PointerManager(
    private val plugin: WaypointsPlugin
) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    private val availablePointers: List<(PointerConfiguration) -> Pointer?> = listOf(
        {
            with(it.actionBar) {
                if (enabled) {
                    ActionBarPointer(this, plugin)
                } else {
                    null
                }
            }
        }, {
            with(it.beacon) {
                if (enabled) {
                    BeaconPointer(this)
                } else {
                    null
                }
            }
        }, {
            with(it.blinkingBlock) {
                if (enabled) {
                    BlinkingBlockPointer(this)
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
                    ParticlePointer(this)
                } else {
                    null
                }
            }
        }
    )

    private val enabledPointers: MutableList<Pointer> = ArrayList()

    private val activePointers: MutableMap<Player, Waypoint> = HashMap()


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

    fun enable(player: Player, waypoint: Waypoint) {
        activePointers.put(player, waypoint)?.let {
            hide(player, it)
        }
        show(player, waypoint)
    }

    fun disable(player: Player) {
        activePointers.remove(player)?.let {
            hide(player, it)
        }
    }

    private fun show(player: Player, target: Waypoint) {
        plugin.callEvent(WaypointSelectEvent(player, target))
        enabledPointers.forEach {
            it.show(player, target)
        }
    }

    private fun hide(player: Player, target: Waypoint) {
        plugin.callEvent(WaypointDeselectEvent(player, target))
        enabledPointers.forEach {
            it.hide(player, target)
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

        val waypoint = activePointers[e.player] ?: return

        if (e.player.location.distanceSquared(waypoint.location) <= disableWhenReachedRadius) {
            disable(e.player)
        }
    }

    init {
        setupPointers()
    }
}