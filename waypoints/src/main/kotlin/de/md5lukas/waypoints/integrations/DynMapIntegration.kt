package de.md5lukas.waypoints.integrations

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.event.WaypointCreateEvent
import de.md5lukas.waypoints.api.event.WaypointPostDeleteEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.dynmap.DynmapAPI
import org.dynmap.markers.MarkerAPI
import org.dynmap.markers.MarkerIcon
import org.dynmap.markers.MarkerSet
import java.util.logging.Level

/**
 * For docs see:
 * https://github.com/webbukkit/dynmap/blob/v3.0/DynmapCoreAPI/src/main/java/org/dynmap/markers/MarkerAPI.java
 * https://github.com/webbukkit/dynmap/blob/v3.0/DynmapCoreAPI/src/main/java/org/dynmap/markers/MarkerSet.java
 */
class DynMapIntegration(
    private val plugin: WaypointsPlugin
) : Listener {

    private lateinit var markerApi: MarkerAPI
    private lateinit var markerSet: MarkerSet
    private lateinit var markerIcon: MarkerIcon

    fun setupDynMap() {
        val dynmapPluginInstance = plugin.server.pluginManager.getPlugin("dynmap")
        if (dynmapPluginInstance === null) {
            return
        }

        plugin.logger.log(Level.INFO, "Found DynMap plugin")
        try {
            markerApi = (dynmapPluginInstance as DynmapAPI).markerAPI
            markerIcon = markerApi.getMarkerIcon(plugin.waypointsConfig.integrations.dynmap.icon)

            markerSet = markerApi.createMarkerSet(
                "waypoints_public",
                plugin.translations.INTEGRATIONS_DYNMAP_MARKER_SET_LABEL.text,
                null,
                false
            ); // id, label, iconlimit, persistent

            plugin.api.publicWaypoints.allWaypoints.forEach {
                createMarker(it)
            }

            plugin.server.pluginManager.registerEvents(this, plugin)
        } catch (_: ClassNotFoundException) {
            plugin.logger.log(Level.WARNING, "The DynMap plugin has been found, but plugin instance class could not be found")
        }
    }

    @EventHandler
    private fun onCreate(e: WaypointCreateEvent) {
        if (e.waypoint.type === Type.PUBLIC) {
            createMarker(e.waypoint)
        }
    }

    @EventHandler
    private fun onDelete(e: WaypointPostDeleteEvent) {
        if (e.waypoint.type === Type.PUBLIC) {
            markerSet.findMarker(e.waypoint.id.toString())?.deleteMarker()
        }
    }

    private fun createMarker(waypoint: Waypoint) {
        with(waypoint.location) {
            val worldNotNull = world ?: return
            markerSet.createMarker(
                waypoint.id.toString(), // ID
                waypoint.name, // Label
                worldNotNull.name, // World ID
                x, // X
                y, // Y
                z, // z
                markerIcon, // Marker icon
                false // is persistent
            )
        }
    }
}