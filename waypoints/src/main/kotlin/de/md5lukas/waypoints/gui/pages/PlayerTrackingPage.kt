package de.md5lukas.waypoints.gui.pages

import com.okkero.skedule.SynchronizationContext
import com.okkero.skedule.withSynchronizationContext
import de.md5lukas.commons.collections.PaginationList
import de.md5lukas.commons.paper.distance2D
import de.md5lukas.commons.paper.editMeta
import de.md5lukas.commons.paper.placeholder
import de.md5lukas.commons.paper.placeholderIgnoringArguments
import de.md5lukas.kinvs.GUIPattern
import de.md5lukas.kinvs.items.GUIItem
import de.md5lukas.waypoints.WaypointsPermissions
import de.md5lukas.waypoints.gui.WaypointsGUI
import de.md5lukas.waypoints.gui.items.TrackableToggleItem
import de.md5lukas.waypoints.pointers.PlayerTrackable
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.SkullMeta

class PlayerTrackingPage(
    wpGUI: WaypointsGUI,
) : ListingPage<Player>(wpGUI, wpGUI.translations.TRACKING_BACKGROUND.item) {

  override suspend fun getContent(): PaginationList<Player> =
      PaginationList<Player>(PAGINATION_LIST_PAGE_SIZE).also { list ->
        val toggleable = wpGUI.plugin.waypointsConfig.playerTracking.toggleable
        val canTrackAll = wpGUI.viewer.hasPermission(WaypointsPermissions.TRACKING_TRACK_ALL)

        val api = wpGUI.plugin.api

        wpGUI.plugin.server.onlinePlayers.forEach {
          if (it === wpGUI.viewer) {
            return@forEach
          }
          if (canTrackAll || !toggleable || api.getWaypointPlayer(it.uniqueId).canBeTracked) {
            list.add(it)
          }
        }
      }

  override suspend fun toGUIContent(value: Player) =
      GUIItem(
          wpGUI.translations.TRACKING_PLAYER.getItem(
                  "name" placeholder value.displayName(),
                  "world" placeholder wpGUI.plugin.worldTranslations.getWorldName(value.world),
                  "x" placeholder value.location.x,
                  "y" placeholder value.location.y,
                  "z" placeholder value.location.z,
                  "block_x" placeholder value.location.blockX,
                  "block_y" placeholder value.location.blockY,
                  "block_z" placeholder value.location.blockZ,
                  if (wpGUI.viewer.world === value.world) {
                    "distance" placeholder wpGUI.viewer.location.distance2D(value.location)
                  } else {
                    "distance" placeholderIgnoringArguments
                        wpGUI.translations.TEXT_DISTANCE_OTHER_WORLD.text
                  })
              .also { stack -> stack.editMeta<SkullMeta> { owningPlayer = value } }) {
            if (!wpGUI.viewerData.canBeTracked &&
                wpGUI.plugin.waypointsConfig.playerTracking.trackingRequiresTrackable) {
              wpGUI.translations.MESSAGE_TRACKING_TRACKABLE_REQUIRED.send(wpGUI.viewer)
            } else if (!value.isOnline) {
              wpGUI.translations.MESSAGE_TRACKING_PLAYER_NO_LONGER_ONLINE.send(wpGUI.viewer)
            } else {
              wpGUI.viewer.closeInventory()
              wpGUI.plugin.pointerManager.enable(wpGUI.viewer, PlayerTrackable(wpGUI.plugin, value))
              if (wpGUI.plugin.waypointsConfig.playerTracking.notification) {
                wpGUI.translations.MESSAGE_TRACKING_NOTIFICATION.send(
                    value, "name" placeholder wpGUI.viewer.displayName())
              }
            }
          }

  private companion object {

    /** p = previous t = Toggle trackable r = Refresh players b = Back n = Next */
    val controlsPattern = GUIPattern("p_t_r_b_n")
  }

  override fun update() {
    wpGUI.skedule {
      updateListingContent()
      updateControls()
    }
  }

  private suspend fun updateControls(update: Boolean = true) {
    applyPattern(
        controlsPattern,
        4,
        0,
        background,
        'p' to GUIItem(wpGUI.translations.GENERAL_PREVIOUS.item) { previousPage() },
        't' to
            if (wpGUI.plugin.waypointsConfig.playerTracking.toggleable) {
              TrackableToggleItem(wpGUI)
            } else {
              background
            },
        'r' to
            GUIItem(wpGUI.translations.PLAYER_LIST_REFRESH_LISTING.item) {
              wpGUI.skedule { updateListingContent() }
            },
        'b' to GUIItem(wpGUI.translations.GENERAL_BACK.item) { wpGUI.goBack() },
        'n' to GUIItem(wpGUI.translations.GENERAL_NEXT.item) { nextPage() },
    )

    if (update) {
      withSynchronizationContext(SynchronizationContext.SYNC) { wpGUI.gui.update() }
    }
  }

  override suspend fun init() {
    super.init()
    updateListingInInventory()
    updateControls(false)
  }
}
