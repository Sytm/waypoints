package de.md5lukas.waypoints.api.event

import de.md5lukas.waypoints.api.Folder
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * This event is triggered when a folder is created
 */
class FolderCreateEvent(
    /**
     * The just created folder
     */
    val folder: Folder
) : Event(true) {

    private companion object {
        @JvmStatic // Automatically creates static getHandlerList()
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList = handlerList
}