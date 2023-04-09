package de.md5lukas.waypoints.pointers

import org.bukkit.Location
import org.bukkit.entity.Player

internal abstract class Pointer(
    protected val pointerManager: PointerManager,
    val interval: Int,
) {

    open fun show(player: Player, trackable: Trackable, translatedTarget: Location?) {
        update(player, trackable, translatedTarget)
    }

    open fun update(player: Player, trackable: Trackable, translatedTarget: Location?) {}

    open fun hide(player: Player, trackable: Trackable, translatedTarget: Location?) {}
}