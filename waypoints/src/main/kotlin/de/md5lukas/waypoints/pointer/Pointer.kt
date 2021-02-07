package de.md5lukas.waypoints.pointer

import de.md5lukas.waypoints.api.Waypoint
import org.bukkit.entity.Player

abstract class Pointer(
    val interval: Int
) {

    open fun show(player: Player, waypoint: Waypoint) {
        update(player, waypoint)
    }

    open fun update(player: Player, waypoint: Waypoint) {}

    open fun hide(player: Player, waypoint: Waypoint) {}
}