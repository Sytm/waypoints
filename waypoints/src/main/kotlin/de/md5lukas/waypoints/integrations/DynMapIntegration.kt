package de.md5lukas.waypoints.integrations

import com.okkero.skedule.skedule
import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.event.WaypointCreateEvent
import de.md5lukas.waypoints.api.event.WaypointCustomDataChangeEvent
import de.md5lukas.waypoints.api.event.WaypointPostDeleteEvent
import de.md5lukas.waypoints.events.ConfigReloadEvent
import de.md5lukas.waypoints.util.registerEvents
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.dynmap.DynmapAPI
import org.dynmap.markers.MarkerAPI
import org.dynmap.markers.MarkerIcon
import org.dynmap.markers.MarkerSet
import java.util.*

/**
 * For docs see:
 * https://github.com/webbukkit/dynmap/blob/v3.0/DynmapCoreAPI/src/main/java/org/dynmap/markers/MarkerAPI.java
 * https://github.com/webbukkit/dynmap/blob/v3.0/DynmapCoreAPI/src/main/java/org/dynmap/markers/MarkerSet.java
 */
class DynMapIntegration(
    private val plugin: WaypointsPlugin
) : Listener {

    companion object Constants {
        const val CUSTOM_DATA_KEY = "dynmap-icon"
    }

    private lateinit var markerApi: MarkerAPI
    private lateinit var markerSet: MarkerSet
    private lateinit var defaultMarkerIcon: MarkerIcon

    fun setupDynMap(): Boolean {
        val dynmapPluginInstance = plugin.server.pluginManager.getPlugin("dynmap")
        if (dynmapPluginInstance === null) {
            return false
        }

        plugin.slF4JLogger.info("Found DynMap plugin")
        try {
            markerApi = (dynmapPluginInstance as DynmapAPI).markerAPI
            defaultMarkerIcon = markerApi.getMarkerIcon(plugin.waypointsConfig.integrations.dynmap.icon)

            markerSet = markerApi.createMarkerSet(
                "waypoints_public",
                plugin.translations.INTEGRATIONS_MAPS_LABEL.rawText,
                null,
                false
            ) // id, label, iconlimit, persistent

            plugin.skedule {
                plugin.api.publicWaypoints.getAllWaypoints().forEach {
                    createMarker(it)
                }
            }

            plugin.registerEvents(this)
        } catch (_: ClassNotFoundException) {
            plugin.slF4JLogger.error("The DynMap plugin has been found, but plugin instance class could not be found")
            return false
        }
        return true
    }

    @EventHandler
    private fun onConfigReload(e: ConfigReloadEvent) {
        val newId = e.config.integrations.dynmap.icon
        if (newId != defaultMarkerIcon.markerIconID) {
            defaultMarkerIcon = markerApi.getMarkerIcon(newId)
        }
        plugin.skedule {
            markerSet.markers.forEach {
                it.markerIcon = getMarkerForWaypoint(plugin.api.getWaypointByID(UUID.fromString(it.markerID))!!)
            }
        }
    }

    @EventHandler
    private fun onCreate(e: WaypointCreateEvent) {
        if (e.waypoint.type === Type.PUBLIC) {
            plugin.skedule {
                createMarker(e.waypoint)
            }
        }
    }

    @EventHandler
    private fun onDelete(e: WaypointPostDeleteEvent) {
        if (e.waypoint.type === Type.PUBLIC) {
            markerSet.findMarker(e.waypoint.id.toString())?.deleteMarker()
        }
    }

    @EventHandler
    private fun onUpdate(e: WaypointCustomDataChangeEvent) {
        if (e.key != CUSTOM_DATA_KEY) {
            return
        }
        plugin.skedule {
            markerSet.findMarker(e.waypoint.id.toString())?.markerIcon = getMarkerForWaypoint(e.waypoint, e.data)
        }
    }

    private suspend fun createMarker(waypoint: Waypoint) {
        with(waypoint.location) {
            val worldNotNull = world ?: return
            markerSet.createMarker(
                waypoint.id.toString(), // ID
                waypoint.name, // Label
                worldNotNull.name, // World ID
                x, // X
                y, // Y
                z, // z
                getMarkerForWaypoint(waypoint), // Marker icon
                false // is persistent
            )
        }
    }

    private suspend fun getMarkerForWaypoint(waypoint: Waypoint, directIcon: String? = null): MarkerIcon {
        val icon = directIcon ?: waypoint.getCustomData(CUSTOM_DATA_KEY)
        return if (icon === null) {
            defaultMarkerIcon
        } else {
            val customIcon: MarkerIcon? = markerApi.getMarkerIcon(icon)

            if (customIcon === null) {
                plugin.slF4JLogger.error("The public waypoint {} has the icon with the name '{}', but that icon does not exist!", waypoint.name, icon)
                defaultMarkerIcon
            } else {
                customIcon
            }
        }
    }
}