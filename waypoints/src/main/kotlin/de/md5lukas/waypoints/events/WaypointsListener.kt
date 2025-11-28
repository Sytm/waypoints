package de.md5lukas.waypoints.events

import com.okkero.skedule.SynchronizationContext
import com.okkero.skedule.skedule
import com.okkero.skedule.switchContext
import de.md5lukas.waypoints.WaypointsPermissions
import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Icon
import de.md5lukas.waypoints.gui.WaypointsGUI
import de.md5lukas.waypoints.util.SuccessWaypoint
import de.md5lukas.waypoints.util.checkWorldAvailability
import de.md5lukas.waypoints.util.createWaypointPrivate
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.BannerPatternLayers
import java.util.UUID
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.NamespacedKey
import org.bukkit.block.Banner
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType

class WaypointsListener(private val plugin: WaypointsPlugin) : Listener {

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  fun onPlayerDeath(e: PlayerDeathEvent) {
    if (plugin.waypointsConfig.features.deathWaypoints &&
        checkWorldAvailability(plugin, e.entity.world)) {
      plugin.skedule {
        plugin.api.getWaypointPlayer(e.entity.uniqueId).addDeathLocation(e.entity.location)
      }
    }
  }

  @EventHandler
  fun onPlayerInteract(e: PlayerInteractEvent) {
    val config = plugin.waypointsConfig.openWithItem
    if (config.enabled &&
        (!config.mustSneak || e.player.isSneaking) &&
        e.action in config.click.actions &&
        e.material.asItemType() in config.items) {
      // Run in next tick to hopefully fix https://github.com/Sytm/waypoints/issues/86 (will be run
      // automatically in next tick due to context switch)
      WaypointsGUI(plugin, e.player, e.player.uniqueId)
    }
  }

  private val bannerKey = NamespacedKey(plugin, "waypoint_id")

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  fun onBlockPlace(e: BlockPlaceEvent) {
    if (!plugin.waypointsConfig.bannerWaypoints.enabled) return
    if (!e.player.hasPermission(WaypointsPermissions.MODIFY_PRIVATE)) return

    val state = e.block.state
    if (state is Banner) {
      state.customName()?.let { customName ->
        val name = PlainTextComponentSerializer.plainText().serialize(customName)
        plugin.skedule {
          // Center on block horizontally
          val location = e.block.location.add(0.5, 0.0, 0.5)
          val result = createWaypointPrivate(plugin, e.player, name, location)
          if (result is SuccessWaypoint) {
            val item = state.type.asBlockType()!!.itemType.createItemStack()
            if (state.numberOfPatterns() > 0) {
              @Suppress("UnstableApiUsage")
              item.setData(
                  DataComponentTypes.BANNER_PATTERNS,
                  BannerPatternLayers.bannerPatternLayers(state.patterns))
            }
            result.waypoint.setIcon(Icon.icon(item))

            switchContext(SynchronizationContext.SYNC)
            val freshState = e.block.state as? Banner ?: return@skedule
            freshState.persistentDataContainer.set(
                bannerKey, PersistentDataType.STRING, result.waypoint.id.toString())
            freshState.update(false, false)
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  fun onBlockBreak(e: BlockBreakEvent) {
    val config = plugin.waypointsConfig.bannerWaypoints
    if (!config.enabled || !config.bannerBreaking.removeWaypoint) return
    if (!e.player.hasPermission(WaypointsPermissions.MODIFY_PRIVATE)) return

    val state = e.block.state
    if (state is Banner) {
      state.persistentDataContainer.get(bannerKey, PersistentDataType.STRING)?.let { waypointId ->
        plugin.skedule {
          plugin.api.getWaypointByID(UUID.fromString(waypointId))?.let { waypoint ->
            if (!config.bannerBreaking.triggerOnlyForOwner || e.player.uniqueId == waypoint.owner) {
              waypoint.delete()
            }
          }
        }
      }
    }
  }

  @EventHandler
  fun onConfigReload(e: ConfigReloadEvent) {
    plugin.server.onlinePlayers.forEach(Player::updateCommands)
    plugin.initDurationFormatter()
  }
}
