package de.md5lukas.waypoints.events

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.gui.WaypointsGUI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent

class WaypointsListener(
    private val plugin: WaypointsPlugin
) : Listener {

    @EventHandler
    private fun onPlayerDeath(e: PlayerDeathEvent) {
        plugin.api.getWaypointPlayer(e.entity.uniqueId).addDeathLocation(e.entity.location)
    }

    @EventHandler
    private fun onPlayerInteract(e: PlayerInteractEvent) {
        val config = plugin.waypointsConfig.general.openWithItem
        if (config.enabled &&
            (!config.mustSneak || e.player.isSneaking) &&
            e.action in config.validClicks &&
            e.material in config.items) {
            WaypointsGUI(plugin, e.player, e.player.uniqueId)
        }
    }
}