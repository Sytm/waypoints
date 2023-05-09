package de.md5lukas.waypoints.events

import com.okkero.skedule.skedule
import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.event.WaypointPostDeleteEvent
import de.md5lukas.waypoints.pointers.PlayerTrackable
import de.md5lukas.waypoints.pointers.WaypointTrackable
import de.md5lukas.waypoints.util.checkWorldAvailability
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent

class PointerEvents(private val plugin: WaypointsPlugin) : Listener {

  private val pointerManager
    get() = plugin.pointerManager

  @EventHandler(priority = EventPriority.MONITOR)
  private fun onPlayerRespawn(e: PlayerRespawnEvent) {
    plugin.skedule(e.player) {
      if (plugin.waypointsConfig.general.features.deathWaypoints &&
          plugin.waypointsConfig.general.pointToDeathWaypointOnDeath.enabled &&
          checkWorldAvailability(plugin, e.respawnLocation.world!!)) {
        plugin.api
            .getWaypointPlayer(e.player.uniqueId)
            .deathFolder
            .getWaypoints()
            .maxByOrNull { it.createdAt }
            ?.let {
              if (pointerManager.getCurrentTarget(e.player) === null ||
                  plugin.waypointsConfig.general.pointToDeathWaypointOnDeath.overwriteCurrent) {
                pointerManager.enable(e.player, WaypointTrackable(plugin, it))
              }
            }
      }
    }
  }

  @EventHandler
  private fun onMove(e: PlayerMoveEvent) {
    val pointer = plugin.pointerManager.getCurrentTarget(e.player) as? WaypointTrackable ?: return

    val visitedRadius = plugin.waypointsConfig.general.teleport.visitedRadiusSquared

    if (e.player.world === pointer.location.world) {
      if (e.player.location.distanceSquared(pointer.location) <= visitedRadius) {
        plugin.skedule(e.player) {
          pointer.waypoint.getWaypointMeta(e.player.uniqueId).setVisisted(true)
        }
      }
    }
  }

  @EventHandler
  private fun onWaypointDelete(e: WaypointPostDeleteEvent) {
    pointerManager.disableAll {
      if (it is Waypoint) {
        it.id == e.waypoint.id
      } else false
    }
  }

  @EventHandler
  private fun onPlayerQuit(e: PlayerQuitEvent) {
    pointerManager.disableAll {
      if (it is PlayerTrackable) {
        it.player == e.player
      } else false
    }
  }

  @EventHandler
  private fun onConfigReload(e: ConfigReloadEvent) {
    pointerManager.applyNewConfiguration(e.config.pointers)
  }
}
