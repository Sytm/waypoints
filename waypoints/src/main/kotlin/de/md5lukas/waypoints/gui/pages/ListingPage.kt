package de.md5lukas.waypoints.gui.pages

import com.okkero.skedule.SynchronizationContext
import com.okkero.skedule.withSynchronizationContext
import de.md5lukas.commons.collections.PaginationList
import de.md5lukas.kinvs.items.GUIContent
import de.md5lukas.waypoints.gui.WaypointsGUI
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.inventory.ItemStack

abstract class ListingPage<T>(
    wpGUI: WaypointsGUI,
    background: ItemStack,
) : BasePage(wpGUI, background) {

  companion object Constants {
    const val PAGINATION_LIST_PAGE_SIZE = 4 * 9
  }

  abstract suspend fun getContent(): PaginationList<T>

  abstract suspend fun toGUIContent(value: T): GUIContent

  open suspend fun init() {
    listingContent = getContent()
  }

  protected lateinit var listingContent: PaginationList<T>
  protected var listingPage = 0

  protected fun checkListingPageBounds() {
    if (listingPage < 0) {
      listingPage = 0
    } else if (listingPage >= listingContent.pages()) {
      listingPage = listingContent.pages() - 1
    }
  }

  protected fun isValidListingPage(page: Int) = page >= 0 && page < listingContent.pages()

  protected suspend fun updateListingContent() {
    listingContent = getContent()

    checkListingPageBounds()

    updateListingInInventory()
  }

  private val listingUpdate = Mutex()

  protected suspend fun updateListingInInventory() {
    listingUpdate.withLock {
      val pageContent = listingContent.page(listingPage)
      for (row in 0..3) {
        for (column in 0..8) {
          val content = pageContent.getOrNull(row * 9 + column)
          if (content == null) {
            grid[row][column] = GUIContent.AIR
          } else {
            grid[row][column] = toGUIContent(content)
          }
        }
      }
      withSynchronizationContext(SynchronizationContext.SYNC) { wpGUI.gui.update() }
    }
  }

  protected fun previousPage() {
    if (!isValidListingPage(listingPage - 1)) return
    listingPage--
    wpGUI.skedule { updateListingInInventory() }
  }

  protected fun nextPage() {
    if (!isValidListingPage(listingPage + 1)) return
    listingPage++
    wpGUI.skedule { updateListingInInventory() }
  }
}
