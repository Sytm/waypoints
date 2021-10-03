package de.md5lukas.waypoints.gui

import de.md5lukas.commons.collections.PaginationList
import de.md5lukas.kinvs.GUI
import de.md5lukas.kinvs.GUIPage
import de.md5lukas.kinvs.GUIPattern
import de.md5lukas.kinvs.items.GUIContent
import de.md5lukas.kinvs.items.GUIItem
import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Folder
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.WaypointHolder
import de.md5lukas.waypoints.api.gui.GUIDisplayable
import de.md5lukas.waypoints.api.gui.GUIFolder
import de.md5lukas.waypoints.gui.pages.GUIFolderPage
import de.md5lukas.waypoints.gui.pages.WaypointPage
import org.bukkit.entity.Player
import java.util.*

class WaypointsGUI(
    internal val plugin: WaypointsPlugin,
    internal val viewer: Player,
    internal val target: UUID,
) {

    internal companion object {
        /*
        * t = title
        * u = pUblic
        * r = pRivate
        * e = pErmission
        */
        val createWaypointPattern = GUIPattern(
            "_________",
            "____t____",
            "_________",
            "_u_____e_",
            "____r___b",
        )

        const val OPEN_REMOVE_LAST = 1
        const val OPEN_NO_PUSH = 2
    }

    internal val pageStack = ArrayDeque<GUIPage>()

    internal val isOwner = viewer.uniqueId == target

    internal val viewerData = plugin.api.getWaypointPlayer(viewer.uniqueId)
    internal val targetData = plugin.api.getWaypointPlayer(target)

    internal val translations = plugin.translations

    internal val gui = GUI(
        viewer, if (isOwner) {
            plugin.translations.INVENTORY_TITLE_SELF.text
        } else {
            val otherName = plugin.uuidUtils.getName(target)

            if (otherName.isEmpty) {
                throw IllegalArgumentException("A player with the UUID $target does not exist.")
            }

            plugin.translations.INVENTORY_TITLE_OTHER.withReplacements(Collections.singletonMap("name", otherName.get()))
        },
        6
    )

    fun openOverview() {
        open(GUIFolderPage(this, targetData))
    }

    fun openHolder(holder: WaypointHolder) {
        open(GUIFolderPage(this, holder))
    }

    fun openFolder(folder: Folder) {
        open(GUIFolderPage(this, folder))
    }

    fun openWaypoint(waypoint: Waypoint) {
        open(WaypointPage(this, waypoint))
    }

    fun openCreateFolder(waypointHolder: WaypointHolder) {
    }

    fun openCreateWaypoint(waypointHolder: WaypointHolder, folder: Folder?) {
    }

    internal fun open(page: GUIPage, actions: Int = 0) {
        if ((actions and OPEN_REMOVE_LAST) == OPEN_REMOVE_LAST) {
            pageStack.pop()
        }
        if ((actions and OPEN_NO_PUSH) != OPEN_NO_PUSH) {
            pageStack.push(page)
        }
        gui.activePage = page
        gui.update()
    }

    internal fun goBack() {
        if (pageStack.isEmpty()) {
            viewer.closeInventory()
            return
        }

        gui.activePage = pageStack.pop()
        gui.update()
    }

    internal fun getListingContent(guiFolder: GUIFolder): PaginationList<GUIDisplayable> {
        val content = PaginationList<GUIDisplayable>(5 * 9)

        if (isOwner) {
            if (guiFolder === targetData && viewerData.showGlobals) {
                if (plugin.api.publicWaypoints.waypointsAmount > 0) {
                    content.add(plugin.api.publicWaypoints)
                }
                if (plugin.api.permissionWaypoints.getWaypointsVisibleForPlayer(viewer) > 0) {
                    content.add(plugin.api.permissionWaypoints)
                }
            }
        }

        content.addAll(guiFolder.waypoints)

        content.addAll(guiFolder.folders)

        content.sortWith(viewerData.sortBy)

        return content
    }

    internal fun toGUIContent(guiDisplayable: GUIDisplayable): GUIContent {
        return GUIItem(guiDisplayable.getItem(viewer)) {
            when (guiDisplayable) {
                is WaypointHolder -> openHolder(guiDisplayable)
                is Folder -> openFolder(guiDisplayable)
                is Waypoint -> openWaypoint(guiDisplayable)
                else -> throw IllegalStateException("The GUIDisplayable is of an unknown subclass ${guiDisplayable.javaClass.name}")
            }
        }
    }

    init {
        openOverview()
        gui.open()
    }
}