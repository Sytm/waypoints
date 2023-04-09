package de.md5lukas.waypoints.pointers

import org.bukkit.entity.Player

internal class PointerTask(
    private val pointer: Pointer,
    private val activePointers: Map<Player, ActivePointer>,
) : Runnable {

    override fun run() {
        activePointers.forEach { (player, pointerData) ->
            pointer.update(player, pointerData.trackable, pointerData.translatedTarget)
        }
    }
}