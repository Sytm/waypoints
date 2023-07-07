package de.md5lukas.examplek

import de.md5lukas.waypoints.api.WaypointsAPI
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class ExamplePlugin : JavaPlugin() {

  private lateinit var api: WaypointsAPI

  override fun onEnable() {
    val localApi = server.servicesManager.load(WaypointsAPI::class.java)
    if (localApi === null) {
      server.pluginManager.disablePlugin(this)
      return
    }

    this.api = localApi

    CoroutineScope(EmptyCoroutineContext).launch {
      api.publicWaypoints.getAllWaypoints().forEach { waypoint ->
        println("Public waypoint ${waypoint.name} is at position ${waypoint.location}")
      }
    }
  }

  override fun onCommand(
      sender: CommandSender,
      command: Command,
      label: String,
      args: Array<out String>?
  ): Boolean {
    if (sender is Player) {
      CoroutineScope(EmptyCoroutineContext).launch {
        api.getWaypointPlayer(sender.uniqueId).getAllWaypoints().forEach { waypoint ->
          sender.sendMessage(
              Component.text(
                  "You have the waypoint ${waypoint.name} in the folder ${waypoint.getFolder()?.name ?: "none"}"))
        }
      }
    }

    return true
  }
}
