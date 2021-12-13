package de.md5lukas.waypoints.pointer.variants

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.pointer.Pointer
import de.md5lukas.waypoints.util.runTaskAsync
import org.bukkit.entity.Player

class CompassPointer(
    private val plugin: WaypointsPlugin,
) : Pointer(0) {

    override fun show(player: Player, waypoint: Waypoint) {
        val currentCompassTarget = player.compassTarget
        plugin.runTaskAsync {
            plugin.api.getWaypointPlayer(player.uniqueId).setCompassTarget(currentCompassTarget)
        }
        player.compassTarget = waypoint.location
    }

    override fun hide(player: Player, waypoint: Waypoint) {
        plugin.runTaskAsync {
            plugin.api.getWaypointPlayer(player.uniqueId).getCompassTarget()?.let {
                player.compassTarget = it
            }
        }
    }
}