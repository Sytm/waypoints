package de.md5lukas.waypoints.api.event

import de.md5lukas.waypoints.api.Folder
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/** This event is triggered after the folder has been removed from the database */
class FolderPostDeleteEvent(
    val isAsync: Boolean,
    /** The deleted folder */
    val folder: Folder
) : Event(isAsync) {

  private companion object {
    @JvmStatic // Automatically creates static getHandlerList()
    val handlerList = HandlerList()
  }

  override fun getHandlers(): HandlerList = handlerList
}
