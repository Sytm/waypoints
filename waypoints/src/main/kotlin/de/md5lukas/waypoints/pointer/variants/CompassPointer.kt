package de.md5lukas.waypoints.pointer.variants

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.config.pointers.CompassConfiguration
import de.md5lukas.waypoints.pointer.Pointer
import de.md5lukas.waypoints.util.runTaskAsync
import org.bukkit.entity.Player

class CompassPointer(
    private val config: CompassConfiguration,
    private val plugin: WaypointsPlugin,
) : Pointer(0) {

    override fun show(player: Player, waypoint: Waypoint) {
        val currentCompassTarget = player.compassTarget
        plugin.runTaskAsync {
            config.compassStorage.saveCompassLocation(player, currentCompassTarget)
        }
        player.compassTarget = waypoint.location
    }

    override fun hide(player: Player, waypoint: Waypoint) {
        plugin.runTaskAsync {
            config.compassStorage.loadCompassLocation(player)?.let {
                player.compassTarget = it
            }
        }
    }
}