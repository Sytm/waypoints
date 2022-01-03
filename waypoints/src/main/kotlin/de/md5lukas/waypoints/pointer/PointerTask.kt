package de.md5lukas.waypoints.pointer

import org.bukkit.entity.Player

class PointerTask(
    private val pointer: Pointer,
    private val activePointers: Map<Player, ActivePointer>,
) : Runnable {

    override fun run() {
        activePointers.forEach { (player, pointerData) ->
            pointer.update(player, pointerData.waypoint, pointerData.translatedTarget)
        }
    }
}