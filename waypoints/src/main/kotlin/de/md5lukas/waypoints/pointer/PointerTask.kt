package de.md5lukas.waypoints.pointer

import de.md5lukas.waypoints.api.Waypoint
import org.bukkit.entity.Player

class PointerTask(
    private val pointer: Pointer,
    private val activePointers: MutableMap<Player, Waypoint>,
) : Runnable {

    override fun run() {
        activePointers.forEach { (player, targetData) ->
            pointer.update(player, targetData)
        }
    }
}