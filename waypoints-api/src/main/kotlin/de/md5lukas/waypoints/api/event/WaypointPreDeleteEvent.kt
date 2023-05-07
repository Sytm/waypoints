package de.md5lukas.waypoints.api.event

import de.md5lukas.waypoints.api.Waypoint
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * This event is triggered before the waypoint is going to be removed from the database
 */
class WaypointPreDeleteEvent(
    /**
     * The deleted waypoint
     */
    val waypoint: Waypoint
) : Event(true) {

    private companion object {
        @JvmStatic // Automatically creates static getHandlerList()
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList = handlerList
}