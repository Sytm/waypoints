package de.md5lukas.waypoints.pointer.variants

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.pointer.Pointer
import de.md5lukas.waypoints.util.runTaskAsync
import org.bukkit.Location
import org.bukkit.entity.Player

class CompassPointer(
    plugin: WaypointsPlugin,
) : Pointer(plugin, 0) {

    override fun show(player: Player, waypoint: Waypoint, translatedTarget: Location?) {
        val currentCompassTarget = player.compassTarget
        plugin.runTaskAsync {
            plugin.api.getWaypointPlayer(player.uniqueId).setCompassTarget(currentCompassTarget)
        }
        update(player, waypoint, translatedTarget)
    }

    override fun update(player: Player, waypoint: Waypoint, translatedTarget: Location?) {
        player.compassTarget = translatedTarget ?: waypoint.location
    }

    override fun hide(player: Player, waypoint: Waypoint, translatedTarget: Location?) {
        plugin.runTaskAsync {
            plugin.api.getWaypointPlayer(player.uniqueId).getCompassTarget()?.let {
                player.compassTarget = it
            }
        }
    }
}