package de.md5lukas.waypoints.events

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.event.WaypointPostDeleteEvent
import de.md5lukas.waypoints.util.checkWorldAvailability
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerRespawnEvent

class PointerEvents(
    private val plugin: WaypointsPlugin
) : Listener {

    private val pointerManager
        get() = plugin.pointerManager

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onPlayerRespawn(e: PlayerRespawnEvent) {
        if (plugin.waypointsConfig.general.features.deathWaypoints && plugin.waypointsConfig.general.pointToDeathWaypointOnDeath.enabled
            && checkWorldAvailability(plugin, e.respawnLocation.world!!)
        ) {
            plugin.api.getWaypointPlayer(e.player.uniqueId).deathFolder.waypoints.maxByOrNull { it.createdAt }?.let {
                if (pointerManager.getCurrentTarget(e.player) === null || plugin.waypointsConfig.general.pointToDeathWaypointOnDeath.overwriteCurrent) {
                    pointerManager.enable(e.player, it)
                }
            }
        }
    }

    @EventHandler
    private fun onMove(e: PlayerMoveEvent) {
        val pointer = plugin.pointerManager.getCurrentTarget(e.player) as? Waypoint ?: return

        val visitedRadius = plugin.waypointsConfig.general.teleport.visitedRadius

        if (e.player.world === pointer.location.world) {
            if (e.player.location.distanceSquared(pointer.location) <= visitedRadius) {
                pointer.getWaypointMeta(e.player.uniqueId).visited = true
            }
        }
    }

    @EventHandler
    private fun onWaypointDelete(e: WaypointPostDeleteEvent) {
        pointerManager.disableAll(e.waypoint.id)
    }

    @EventHandler
    private fun onConfigReload(e: ConfigReloadEvent) {
        pointerManager.applyNewConfiguration(e.config.pointers)
    }
}