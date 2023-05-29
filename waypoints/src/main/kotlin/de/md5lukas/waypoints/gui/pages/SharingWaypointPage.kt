package de.md5lukas.waypoints.gui.pages

import com.okkero.skedule.SynchronizationContext
import com.okkero.skedule.switchContext
import de.md5lukas.commons.collections.PaginationList
import de.md5lukas.kinvs.GUIPattern
import de.md5lukas.kinvs.items.GUIContent
import de.md5lukas.kinvs.items.GUIItem
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.WaypointShare
import de.md5lukas.waypoints.gui.WaypointsGUI
import de.md5lukas.waypoints.util.editMeta
import de.md5lukas.waypoints.util.placeholder
import kotlinx.coroutines.future.await
import org.bukkit.Bukkit
import org.bukkit.inventory.meta.SkullMeta

class SharingWaypointPage(
    wpGUI: WaypointsGUI,
    private val waypoint: Waypoint,
) : ListingPage<WaypointShare>(wpGUI, wpGUI.extendApi { waypoint.type.getBackgroundItem() }) {

  override suspend fun getContent() =
      PaginationList<WaypointShare>(PAGINATION_LIST_PAGE_SIZE).also { list ->
        list.addAll(waypoint.getSharedWith())
      }

  override suspend fun toGUIContent(value: WaypointShare): GUIContent {
    val player = Bukkit.getOfflinePlayer(value.sharedWith)

    val profile =
        if (player.isOnline) {
          player.playerProfile
        } else {
          player.playerProfile.update().await()
        }

    return GUIItem(
        wpGUI.translations.SHARING_PLAYER_DELETE.getItem("name" placeholder profile.name!!).also {
            stack ->
          stack.editMeta<SkullMeta> { playerProfile = profile }
        }) {
          wpGUI.skedule {
            value.delete()
            updateListingContent()
          }
        }
  }

  private companion object {
    /** p = previous b = Back n = Next */
    val controlsPattern = GUIPattern("p_____b_n")
  }

  override fun update() {
    wpGUI.skedule {
      updateListingContent()
      switchContext(SynchronizationContext.SYNC)
      updateControls()
    }
  }

  private fun updateControls(update: Boolean = true) {
    applyPattern(
        controlsPattern,
        4,
        0,
        background,
        'p' to GUIItem(wpGUI.translations.GENERAL_PREVIOUS.item) { previousPage() },
        'b' to GUIItem(wpGUI.translations.GENERAL_BACK.item) { wpGUI.goBack() },
        'n' to GUIItem(wpGUI.translations.GENERAL_NEXT.item) { nextPage() },
    )

    if (update) {
      wpGUI.gui.update()
    }
  }

  override suspend fun init() {
    super.init()
    updateListingInInventory()
    updateControls(false)
  }
}
