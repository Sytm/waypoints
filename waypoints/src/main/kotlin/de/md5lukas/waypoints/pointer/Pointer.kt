package de.md5lukas.waypoints.pointer

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Waypoint
import org.bukkit.Location
import org.bukkit.entity.Player

abstract class Pointer(
    protected val plugin: WaypointsPlugin,
    val interval: Int,
) {

    open fun show(player: Player, waypoint: Waypoint, translatedTarget: Location?) {
        update(player, waypoint, translatedTarget)
    }

    open fun update(player: Player, waypoint: Waypoint, translatedTarget: Location?) {}

    open fun hide(player: Player, waypoint: Waypoint, translatedTarget: Location?) {}
}