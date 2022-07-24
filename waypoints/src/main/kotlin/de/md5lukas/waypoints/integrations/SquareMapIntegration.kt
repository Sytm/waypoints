package de.md5lukas.waypoints.integrations

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.event.WaypointCreateEvent
import de.md5lukas.waypoints.api.event.WaypointCustomDataChangeEvent
import de.md5lukas.waypoints.api.event.WaypointPostDeleteEvent
import de.md5lukas.waypoints.events.ConfigReloadEvent
import de.md5lukas.waypoints.util.registerEvents
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import xyz.jpenilla.squaremap.api.*
import xyz.jpenilla.squaremap.api.marker.Marker
import xyz.jpenilla.squaremap.api.marker.MarkerOptions
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.logging.Level
import javax.imageio.ImageIO

class SquareMapIntegration(
    private val plugin: WaypointsPlugin
) : Listener {

    companion object Constants {
        val CUSTOM_DATA_KEY = "squaremap-icon"
    }

    private lateinit var api: Squaremap
    private lateinit var layerKey: Key
    private val layerProviders: MutableMap<UUID, SimpleLayerProvider?> = HashMap()

    private val iconFolder = File(plugin.dataFolder, "sqm-icons")

    fun setupSquareMap(): Boolean {
        if (plugin.server.pluginManager.getPlugin("squaremap") === null) {
            return false
        }

        plugin.logger.log(Level.INFO, "Found squaremap plugin")

        extractSQMIcons()

        api = SquaremapProvider.get()

        layerKey = Key.of("waypoints")

        plugin.api.publicWaypoints.allWaypoints.forEach {
            createMarker(it)
        }

        plugin.registerEvents(this)
        return true
    }

    private fun extractSQMIcons() {
        plugin.getResource("resourceIndex")!!.bufferedReader(StandardCharsets.UTF_8).useLines { seq ->
            seq.filter {
                it.startsWith("sqm-icons/")
            }.forEach {
                if (!File(plugin.dataFolder, it).exists()) {
                    plugin.saveResource(it, false)
                }
            }
        }
    }

    @EventHandler
    private fun onDisable(e: PluginDisableEvent) {
        if (e.plugin !== plugin) {
            return
        }

        layerProviders.values.forEach {
            it?.clearMarkers()
        }
    }

    @EventHandler
    private fun onConfigReload(e: ConfigReloadEvent) {
        plugin.api.publicWaypoints.allWaypoints.let { waypoints ->
            waypoints.forEach {
                it.removeMarker()
            }
            unregisterLayerProviders()
            waypoints.forEach {
                createMarker(it)
            }
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
            e.waypoint.removeMarker()
        }
    }

    @EventHandler
    private fun onUpdate(e: WaypointCustomDataChangeEvent) {
        if (e.key != CUSTOM_DATA_KEY || e.waypoint.type !== Type.PUBLIC) {
            return
        }
        e.waypoint.removeMarker()

        createMarker(e.waypoint)
    }

    private fun createMarker(waypoint: Waypoint) {
        val worldNotNull = waypoint.location.world ?: return

        worldNotNull.layerProvider?.let { provider ->
            val marker =
                Marker.icon(BukkitAdapter.point(waypoint.location), getMarkerForWaypoint(waypoint), plugin.waypointsConfig.integrations.squaremap.iconSize)
            marker.markerOptions(MarkerOptions.builder().hoverTooltip(waypoint.name))
            provider.addMarker(Key.of(waypoint.id.toString()), marker)
        }
    }

    private fun getMarkerForWaypoint(waypoint: Waypoint): Key {
        val rawKey = waypoint.getCustomData(CUSTOM_DATA_KEY) ?: plugin.waypointsConfig.integrations.squaremap.icon
        var key = Key.of(rawKey)

        if (api.iconRegistry().hasEntry(key)) {
            return key
        }

        key = Key.of("waypoints-$rawKey")

        if (!api.iconRegistry().hasEntry(key)) {
            val image = File(iconFolder, "$rawKey.png")
            api.iconRegistry().register(key, ImageIO.read(image))
        }

        return key
    }


    private val World.layerProvider: SimpleLayerProvider?
        get() = layerProviders.computeIfAbsent(uid) {
            mapWorld?.let { mapWorld ->
                val provider = SimpleLayerProvider.builder(plugin.translations.INTEGRATIONS_MAPS_LABEL.text).build()

                mapWorld.layerRegistry().register(layerKey, provider)

                provider
            }
        }

    private fun unregisterLayerProviders() {
        api.mapWorlds().forEach {
            it.layerRegistry().let { registry ->
                if (registry.hasEntry(layerKey)) {
                    registry.unregister(layerKey)
                }
            }
        }
        layerProviders.clear()
    }

    private val World.mapWorld: MapWorld?
        get() = api.getWorldIfEnabled(BukkitAdapter.worldIdentifier(this)).orElse(null)

    private fun Waypoint.removeMarker() {
        location.world?.layerProvider?.removeMarker(Key.of(id.toString()))
    }
}