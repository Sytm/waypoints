package de.md5lukas.waypoints.gui.pages

import com.okkero.skedule.SynchronizationContext
import com.okkero.skedule.switchContext
import de.md5lukas.commons.collections.PaginationList
import de.md5lukas.commons.paper.appendLore
import de.md5lukas.commons.paper.placeholder
import de.md5lukas.kinvs.GUIPattern
import de.md5lukas.kinvs.items.GUIItem
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.WaypointShare
import de.md5lukas.waypoints.gui.WaypointsGUI
import kotlinx.coroutines.future.await
import net.kyori.adventure.text.Component

class SharedWaypointsPage(
    wpGUI: WaypointsGUI,
) : ListingPage<WaypointShare>(wpGUI, wpGUI.extendApi { Type.PRIVATE.getBackgroundItem() }) {

  override suspend fun getContent() =
      PaginationList<WaypointShare>(PAGINATION_LIST_PAGE_SIZE).also { list ->
        list.addAll(wpGUI.targetData.getSharedWaypoints())
      }

  override suspend fun toGUIContent(value: WaypointShare) =
      value.getWaypoint().let { waypoint ->
        GUIItem(
            wpGUI.extendApi {
              waypoint.getItem(wpGUI.viewer).also { stack ->
                val playerName =
                    wpGUI.plugin.uuidUtils.getNameAsync(value.owner).await().let { result ->
                      result?.let { Component.text(it) }
                          ?: wpGUI.translations.SHARING_UNKNOWN_PLAYER.text
                    }
                stack.appendLore(
                    wpGUI.translations.SHARING_SHARED_BY.withReplacements(
                        "name" placeholder playerName))
              }
            }) {
              wpGUI.skedule {
                if (it.isShiftClick) {
                  value.delete()
                  updateListingContent()
                } else {
                  wpGUI.openWaypoint(waypoint)
                }
              }
            }
      }

  private companion object {
    /** p = previous s = sort mode b = Back n = Next */
    val controlsPattern = GUIPattern("p_s___b_n")
  }

  override fun update() {
    wpGUI.skedule {
      updateListingContent()
      switchContext(SynchronizationContext.SYNC)
      updateControls()
    }
  }

  private fun updateControls(update: Boolean = true) {
    // TODO sorting
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
