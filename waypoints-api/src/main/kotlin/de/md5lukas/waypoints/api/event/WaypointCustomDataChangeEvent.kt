package de.md5lukas.waypoints.api.event

import de.md5lukas.waypoints.api.Waypoint
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * This event is triggered when the custom data for a waypoint has been changed
 */
class WaypointCustomDataChangeEvent(
    /**
     * The waypoint the custom data belongs to
     */
    val waypoint: Waypoint,
    /**
     * The key that got updated
     */
    val key: String,
    /**
     * The new data for the key
     */
    val data: String?
) : Event(true) {

    private companion object {
        @JvmStatic // Automatically creates static getHandlerList()
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList = handlerList
}