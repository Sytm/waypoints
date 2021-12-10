package de.md5lukas.waypoints.api.event

import de.md5lukas.waypoints.api.Folder
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class FolderCreateEvent(
    val folder: Folder
) : Event() {

    private companion object {
        @JvmStatic // Automatically creates static getHandlerList()
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList = handlerList
}