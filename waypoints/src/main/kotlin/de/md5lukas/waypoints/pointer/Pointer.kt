package de.md5lukas.waypoints.pointer

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Trackable
import org.bukkit.Location
import org.bukkit.entity.Player

abstract class Pointer(
    protected val plugin: WaypointsPlugin,
    val interval: Int,
) {

    open fun show(player: Player, trackable: Trackable, translatedTarget: Location?) {
        update(player, trackable, translatedTarget)
    }

    open fun update(player: Player, trackable: Trackable, translatedTarget: Location?) {}

    open fun hide(player: Player, trackable: Trackable, translatedTarget: Location?) {}
}