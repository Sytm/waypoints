package de.md5lukas.waypoints.integrations

import com.flowpowered.math.vector.Vector3d
import de.bluecolored.bluemap.api.BlueMapAPI
import de.bluecolored.bluemap.api.markers.MarkerSet
import de.bluecolored.bluemap.api.markers.POIMarker
import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.event.WaypointCreateEvent
import de.md5lukas.waypoints.api.event.WaypointPostDeleteEvent
import de.md5lukas.waypoints.util.registerEvents
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.logging.Level

class BlueMapIntegration(
    private val plugin: WaypointsPlugin,
) : Listener {

    private lateinit var api: BlueMapAPI
    private val markerSets = HashMap<World, MarkerSet>()

    fun setupBlueMap(): Boolean {
        if (plugin.server.pluginManager.getPlugin("BlueMap") === null)
            return false

        BlueMapAPI.onEnable {
            api = it

            plugin.api.publicWaypoints.allWaypoints.forEach { waypoint ->
                createMarker(waypoint)
            }

            plugin.registerEvents(this)
            plugin.logger.log(Level.INFO, "Delayed initialization of BlueMap integration completed")
        }

        return true
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
            getMarkerSet(e.waypoint.location.world!!).remove(e.waypoint.id.toString())
        }
    }

    private fun createMarker(waypoint: Waypoint) {
        getMarkerSet(waypoint.location.world!!).markers[waypoint.id.toString()] =
            POIMarker(waypoint.name, Vector3d(waypoint.location.x, waypoint.location.y, waypoint.location.z))
    }

    private fun getMarkerSet(world: World) = markerSets.computeIfAbsent(world) {
        val markerSet = MarkerSet(plugin.translations.INTEGRATIONS_MAPS_LABEL.text)
        api.getWorld(world).ifPresent { blueMapWorld ->
            blueMapWorld.maps.forEach {
                it.markerSets["waypoints_public"] = markerSet
            }
        }
        markerSet
    }
}