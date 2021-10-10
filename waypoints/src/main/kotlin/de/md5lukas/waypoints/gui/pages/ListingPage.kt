package de.md5lukas.waypoints.gui.pages

import de.md5lukas.commons.collections.PaginationList
import de.md5lukas.kinvs.items.GUIContent
import de.md5lukas.waypoints.api.gui.GUIDisplayable
import de.md5lukas.waypoints.gui.WaypointsGUI

open class ListingPage<T : GUIDisplayable>(
    wpGUI: WaypointsGUI,
    guiDisplayable: GUIDisplayable,
    contentGetter: () -> PaginationList<T>,
    private val displayableConverter: (T) -> GUIContent
)
    : BasePage(wpGUI, guiDisplayable) {

    companion object Constants {
        const val PAGINATION_LIST_PAGE_SIZE = 5 * 9
    }

    protected var listingContent: PaginationList<T> = contentGetter()
    protected var listingPage = 0

    protected fun checkListingPageBounds() {
        if (listingPage < 0) {
            listingPage = 0
        } else if (listingPage >= listingContent.pages()) {
            listingPage = listingContent.pages() - 1
        }
    }

    protected fun isValidListingPage(page: Int) = page >= 0 && page < listingContent.pages()

    protected fun updateListingInInventory() {
        val pageContent = listingContent.page(listingPage)
        for (row in 0..4) {
            for (column in 0..8) {
                val content = pageContent.getOrNull(row * 8 + column)
                if (content == null) {
                    grid[row][column] = GUIContent.AIR
                } else {
                    grid[row][column] = displayableConverter(content)
                }
            }
        }
        wpGUI.gui.update()
    }

    protected fun previousPage() {
        if (!isValidListingPage(listingPage - 1))
            return
        listingPage--
        updateListingInInventory()
    }

    protected fun nextPage() {
        if (!isValidListingPage(listingPage + 1))
            return
        listingPage++
        updateListingInInventory()
    }
}