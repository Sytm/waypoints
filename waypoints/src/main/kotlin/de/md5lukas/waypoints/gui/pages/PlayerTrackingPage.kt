package de.md5lukas.waypoints.gui.pages

import com.okkero.skedule.SynchronizationContext
import com.okkero.skedule.withSynchronizationContext
import de.md5lukas.commons.MathHelper
import de.md5lukas.commons.collections.PaginationList
import de.md5lukas.kinvs.GUIPattern
import de.md5lukas.kinvs.items.GUIItem
import de.md5lukas.waypoints.WaypointsPermissions
import de.md5lukas.waypoints.gui.WaypointsGUI
import de.md5lukas.waypoints.gui.items.TrackableToggleItem
import de.md5lukas.waypoints.pointers.PlayerTrackable
import de.md5lukas.waypoints.util.placeholder
import de.md5lukas.waypoints.util.placeholderIgnoringArguments
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.SkullMeta

class PlayerTrackingPage(
    wpGUI: WaypointsGUI,
) :
    ListingPage<Player>(
        wpGUI,
        wpGUI.translations.TRACKING_BACKGROUND.item,
        {
          val list = PaginationList<Player>(PAGINATION_LIST_PAGE_SIZE)
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
          list
        },
        { player ->
          GUIItem(
              wpGUI.translations.TRACKING_PLAYER.getItem(
                      "name" placeholder player.displayName(),
                      "world" placeholder wpGUI.plugin.worldTranslations.getWorldName(player.world),
                      "x" placeholder player.location.x,
                      "y" placeholder player.location.y,
                      "z" placeholder player.location.z,
                      "block_x" placeholder player.location.blockX,
                      "block_y" placeholder player.location.blockY,
                      "block_z" placeholder player.location.blockZ,
                      if (wpGUI.viewer.world === player.world) {
                        "distance" placeholder
                            MathHelper.distance2D(wpGUI.viewer.location, player.location)
                      } else {
                        "distance" placeholderIgnoringArguments
                            wpGUI.translations.TEXT_DISTANCE_OTHER_WORLD.text
                      })
                  .also { stack ->
                    stack.itemMeta =
                        (stack.itemMeta!! as SkullMeta).also { meta -> meta.owningPlayer = player }
                  }) {
                if (!wpGUI.viewerData.canBeTracked &&
                    wpGUI.plugin.waypointsConfig.playerTracking.trackingRequiresTrackable) {
                  wpGUI.translations.MESSAGE_TRACKING_TRACKABLE_REQUIRED.send(wpGUI.viewer)
                } else if (!player.isOnline) {
                  wpGUI.translations.MESSAGE_TRACKING_PLAYER_NO_LONGER_ONLINE.send(wpGUI.viewer)
                } else {
                  wpGUI.viewer.closeInventory()
                  wpGUI.plugin.pointerManager.enable(
                      wpGUI.viewer, PlayerTrackable(wpGUI.plugin, player))
                  if (wpGUI.plugin.waypointsConfig.playerTracking.notification) {
                    wpGUI.translations.MESSAGE_TRACKING_NOTIFICATION.send(
                        player, "name" placeholder wpGUI.viewer.displayName())
                  }
                }
              }
        }) {

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
            GUIItem(wpGUI.translations.TRACKING_REFRESH_LISTING.item) {
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
