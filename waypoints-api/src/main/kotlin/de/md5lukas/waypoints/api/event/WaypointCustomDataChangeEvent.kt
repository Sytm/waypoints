package de.md5lukas.waypoints.api.event

import de.md5lukas.waypoints.api.Waypoint
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class WaypointCustomDataChangeEvent(
    val waypoint: Waypoint,
    val key: String,
    val data: String?
) : Event() {

    private companion object {
        @JvmStatic // Automatically creates static getHandlerList()
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList = handlerList
}