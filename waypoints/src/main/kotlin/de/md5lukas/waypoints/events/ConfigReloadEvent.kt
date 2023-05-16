package de.md5lukas.waypoints.events

import de.md5lukas.waypoints.config.WaypointsConfiguration
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class ConfigReloadEvent(val config: WaypointsConfiguration) : Event() {
  private companion object {
    @JvmStatic // Automatically creates static getHandlerList()
    val handlerList = HandlerList()
  }

  override fun getHandlers(): HandlerList = handlerList
}
