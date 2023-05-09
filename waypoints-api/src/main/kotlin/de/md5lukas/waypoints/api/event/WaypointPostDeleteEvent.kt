package de.md5lukas.waypoints.api.event

import de.md5lukas.waypoints.api.Waypoint
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/** This event is triggered after the waypoint has been removed from the database */
class WaypointPostDeleteEvent(
    val isAsync: Boolean,
    /** The deleted waypoint */
    val waypoint: Waypoint
) : Event(isAsync) {

  private companion object {
    @JvmStatic // Automatically creates static getHandlerList()
    val handlerList = HandlerList()
  }

  override fun getHandlers(): HandlerList = handlerList
}
