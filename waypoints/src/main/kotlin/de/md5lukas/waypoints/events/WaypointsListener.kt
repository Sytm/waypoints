package de.md5lukas.waypoints.events

import de.md5lukas.waypoints.WaypointsPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class WaypointsListener(
    private val plugin: WaypointsPlugin
) : Listener {

    @EventHandler
    private fun onPlayerDeath(e: PlayerDeathEvent) {
        plugin.api.getWaypointPlayer(e.entity.uniqueId).addDeathLocation(e.entity.location)
    }
}