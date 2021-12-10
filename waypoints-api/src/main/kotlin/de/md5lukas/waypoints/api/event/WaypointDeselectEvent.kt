package de.md5lukas.waypoints.api.event

import de.md5lukas.waypoints.api.Waypoint
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class WaypointDeselectEvent(
    player: Player,
    val waypoint: Waypoint
) : PlayerEvent(player) {

    private companion object {
        @JvmStatic // Automatically creates static getHandlerList()
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList = handlerList
}