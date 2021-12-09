package de.md5lukas.waypoints.integrations

import de.md5lukas.waypoints.WaypointsPlugin
import org.dynmap.DynmapAPI
import org.dynmap.bukkit.DynmapPlugin
import org.dynmap.markers.MarkerIcon
import org.dynmap.markers.MarkerSet
import java.util.logging.Level

class DynMapIntegration(
    private val plugin: WaypointsPlugin
) : Runnable {

    private var dynMapApi: DynmapAPI? = null
    private lateinit var markerSet: MarkerSet
    private lateinit var markerIcon: MarkerIcon

    fun setupDynMap() {
        if (plugin.server.pluginManager.getPlugin("dynmap") === null) {
            return
        }

        plugin.logger.log(Level.INFO, "Found DynMap plugin")
        try {
            val dynMapApi = DynmapPlugin.plugin
            this.dynMapApi = dynMapApi

            val markerApi = dynMapApi.markerAPI
            markerSet = markerApi.createMarkerSet("waypoints_public", "Public Waypoints", null, false); // id, label, iconlimit, persistent
            markerIcon = markerApi.getMarkerIcon(MarkerIcon.DEFAULT)

            plugin.server.scheduler.runTaskTimerAsynchronously(plugin, this, 0, 5 * 60 * 20);
        } catch (_: ClassNotFoundException) {
            plugin.logger.log(Level.WARNING, "The dynmap plugin has been found, but plugin instance class could not be found")
        }
    }

    /**
     * For docs see:
     * https://github.com/webbukkit/dynmap/blob/v3.0/DynmapCoreAPI/src/main/java/org/dynmap/markers/MarkerAPI.java
     * https://github.com/webbukkit/dynmap/blob/v3.0/DynmapCoreAPI/src/main/java/org/dynmap/markers/MarkerSet.java
     */
    override fun run() {
        plugin.api.publicWaypoints.allWaypoints.forEach {
            if (markerSet.findMarker(it.id.toString()) === null) {
                with(it.location) {
                    markerSet.createMarker(
                        it.id.toString(), // ID
                        it.name, // Label
                        world!!.name, // World ID
                        x, // X
                        y, // Y
                        z, // z
                        markerIcon, // Marker icon
                        false // is persistent
                    )
                }
            }
        }
    }
}