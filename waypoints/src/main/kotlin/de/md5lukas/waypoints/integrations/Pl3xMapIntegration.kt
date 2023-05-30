package de.md5lukas.waypoints.integrations

import com.okkero.skedule.skedule
import de.md5lukas.commons.paper.registerEvents
import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.event.WaypointCreateEvent
import de.md5lukas.waypoints.api.event.WaypointCustomDataChangeEvent
import de.md5lukas.waypoints.api.event.WaypointPostDeleteEvent
import de.md5lukas.waypoints.events.ConfigReloadEvent
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import net.pl3x.map.core.Pl3xMap
import net.pl3x.map.core.image.IconImage
import net.pl3x.map.core.markers.layer.SimpleLayer
import net.pl3x.map.core.markers.marker.Marker
import net.pl3x.map.core.markers.option.Options
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent

class Pl3xMapIntegration(private val plugin: WaypointsPlugin) : Listener {

  companion object Constants {
    const val CUSTOM_DATA_KEY = "pl3xmap-icon"
  }

  private lateinit var api: Pl3xMap
  private val layerKey: String = "waypoints"
  private val layerProviders: MutableMap<UUID, SimpleLayer?> = HashMap()

  private val iconFolder = File(plugin.dataFolder, "icons")

  fun setupPl3xMap(): Boolean {
    if (plugin.server.pluginManager.getPlugin("Pl3xMap") === null) {
      return false
    }

    plugin.slF4JLogger.info("Found Pl3xMap plugin")

    extractIcons(plugin)

    api = Pl3xMap.api()

    plugin.skedule { plugin.api.publicWaypoints.getAllWaypoints().forEach { createMarker(it) } }

    plugin.registerEvents(this)
    return true
  }

  @EventHandler
  fun onDisable(e: PluginDisableEvent) {
    if (e.plugin !== plugin) {
      return
    }

    layerProviders.values.forEach { it?.clearMarkers() }
  }

  @EventHandler
  @Suppress("UNUSED_PARAMETER")
  fun onConfigReload(e: ConfigReloadEvent) {
    plugin.skedule {
      plugin.api.publicWaypoints.getAllWaypoints().let { waypoints ->
        waypoints.forEach { it.removeMarker() }
        unregisterLayerProviders()
        waypoints.forEach { createMarker(it) }
      }
    }
  }

  @EventHandler
  fun onCreate(e: WaypointCreateEvent) {
    if (e.waypoint.type === Type.PUBLIC) {
      plugin.skedule { createMarker(e.waypoint) }
    }
  }

  @EventHandler
  fun onDelete(e: WaypointPostDeleteEvent) {
    if (e.waypoint.type === Type.PUBLIC) {
      e.waypoint.removeMarker()
    }
  }

  @EventHandler
  fun onUpdate(e: WaypointCustomDataChangeEvent) {
    if (e.key != CUSTOM_DATA_KEY || e.waypoint.type !== Type.PUBLIC) {
      return
    }
    e.waypoint.removeMarker()

    plugin.skedule { createMarker(e.waypoint) }
  }

  private suspend fun createMarker(waypoint: Waypoint) {
    val worldNotNull = waypoint.location.world ?: return

    worldNotNull.layerProvider?.let { provider ->
      val marker =
          Marker.icon(
              waypoint.id.toString(),
              waypoint.location.x,
              waypoint.location.z,
              getMarkerForWaypoint(waypoint),
              plugin.waypointsConfig.integrations.pl3xmap.iconSize)
      marker.setOptions(Options.builder().tooltipContent(waypoint.name))
      provider.addMarker(marker)
    }
  }

  private suspend fun getMarkerForWaypoint(waypoint: Waypoint): String {
    val key =
        waypoint.getCustomData(CUSTOM_DATA_KEY) ?: plugin.waypointsConfig.integrations.pl3xmap.icon

    if (api.iconRegistry.has(key)) {
      return key
    }

    val prefixedKey = "waypoints-$key"

    if (!api.iconRegistry.has(prefixedKey)) {
      val image = File(iconFolder, "$key.png")
      api.iconRegistry.register(IconImage(prefixedKey, ImageIO.read(image), "png"))
    }

    return prefixedKey
  }

  private val World.layerProvider: SimpleLayer?
    get() =
        layerProviders.computeIfAbsent(uid) {
          mapWorld?.let { mapWorld ->
            val layer =
                SimpleLayer(layerKey) { plugin.translations.INTEGRATIONS_MAPS_LABEL.rawText }

            mapWorld.layerRegistry.register(layer)

            layer
          }
        }

  private fun unregisterLayerProviders() {
    api.worldRegistry.forEach {
      it.layerRegistry.let { registry ->
        if (registry.has(layerKey)) {
          registry.unregister(layerKey)
        }
      }
    }
    layerProviders.clear()
  }

  private val World.mapWorld
    get() = api.worldRegistry.get(name)

  private fun Waypoint.removeMarker() {
    location.world?.layerProvider?.removeMarker(id.toString())
  }
}
