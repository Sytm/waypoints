package de.md5lukas.waypoints.events

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.gui.WaypointsGUI
import de.md5lukas.waypoints.util.checkWorldAvailability
import de.md5lukas.waypoints.util.runTask
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerRespawnEvent

class WaypointsListener(
    private val plugin: WaypointsPlugin
) : Listener {

    @EventHandler
    private fun onPlayerDeath(e: PlayerDeathEvent) {
        if (plugin.waypointsConfig.general.features.deathWaypoints && checkWorldAvailability(plugin, e.entity.world)) {
            plugin.api.getWaypointPlayer(e.entity.uniqueId).addDeathLocation(e.entity.location)
        }
    }

    @EventHandler
    private fun onPlayerInteract(e: PlayerInteractEvent) {
        val config = plugin.waypointsConfig.general.openWithItem
        if (config.enabled &&
            (!config.mustSneak || e.player.isSneaking) &&
            e.action in config.validClicks &&
            e.material in config.items
        ) {
            plugin.runTask {
                // Run in next tick to hopefully fix https://github.com/Sytm/waypoints/issues/86
                WaypointsGUI(plugin, e.player, e.player.uniqueId)
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onPlayerRespawn(e: PlayerRespawnEvent) {
        if (plugin.waypointsConfig.general.features.deathWaypoints && plugin.waypointsConfig.general.pointToDeathWaypointOnDeath.enabled
            && checkWorldAvailability(plugin, e.respawnLocation.world!!)
        ) {
            plugin.api.getWaypointPlayer(e.player.uniqueId).deathFolder.waypoints.maxByOrNull { it.createdAt }?.let {
                if (plugin.api.pointerManager.getCurrentTarget(e.player) === null || plugin.waypointsConfig.general.pointToDeathWaypointOnDeath.overwriteCurrent) {
                    plugin.api.pointerManager.enable(e.player, it)
                }
            }
        }
    }
}