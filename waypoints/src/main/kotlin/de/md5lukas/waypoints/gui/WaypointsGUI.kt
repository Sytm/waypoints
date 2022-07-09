package de.md5lukas.waypoints.gui

import de.md5lukas.commons.collections.PaginationList
import de.md5lukas.kinvs.GUI
import de.md5lukas.kinvs.GUIPattern
import de.md5lukas.kinvs.items.GUIContent
import de.md5lukas.kinvs.items.GUIItem
import de.md5lukas.waypoints.WaypointsPermissions
import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Folder
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.WaypointHolder
import de.md5lukas.waypoints.api.gui.GUIDisplayable
import de.md5lukas.waypoints.api.gui.GUIFolder
import de.md5lukas.waypoints.config.general.WorldNotFoundAction
import de.md5lukas.waypoints.gui.pages.*
import de.md5lukas.waypoints.util.*
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Location
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
    }

    private val pageStack = ArrayDeque<BasePage>()

    internal val apiExtensions = plugin.apiExtensions

    internal inline fun <T> extendApi(block: APIExtensions.() -> T) = apiExtensions.block()

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
        5
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

    fun openPlayerTracking() {
        open(PlayerTrackingPage(this))
    }

    fun openCreateFolder(waypointHolder: WaypointHolder) {
        AnvilGUI.Builder().plugin(plugin).text(translations.FOLDER_CREATE_ENTER_NAME.text).onComplete { _, name ->
            val result = when (waypointHolder.type) {
                Type.PRIVATE -> createFolderPrivate(plugin, viewer, name)
                Type.PUBLIC -> createFolderPublic(plugin, viewer, name)
                Type.PERMISSION -> createFolderPermission(plugin, viewer, name)
                else -> throw IllegalStateException("Cannot create folders of the type ${waypointHolder.type}")
            }

            return@onComplete when (result) {
                NameTaken -> AnvilGUI.Response.text(translations.FOLDER_CREATE_ENTER_NAME.text)
                LimitReached, is SuccessFolder -> AnvilGUI.Response.close()
                else -> throw IllegalStateException("Invalid return value $result")
            }
        }.onClose {
            (gui.activePage as BasePage).update()
            plugin.runTask {
                gui.open()
            }
        }.open(viewer)
    }

    fun openCreateWaypoint(type: Type, folder: Folder?, location: Location = viewer.location) {
        var waypoint: Waypoint? = null

        var name: String? = null
        var permission: String? = null
        AnvilGUI.Builder().plugin(plugin).text(translations.WAYPOINT_CREATE_ENTER_NAME.text).onComplete { _, enteredText ->
            if (name == null) {
                name = enteredText

                if (type == Type.PERMISSION && permission == null) {
                    return@onComplete AnvilGUI.Response.text(translations.WAYPOINT_CREATE_ENTER_PERMISSION.text)
                }
            } else if (type == Type.PERMISSION && permission == null) {
                permission = enteredText
            }

            val result = name!!.let { name ->
                when (type) {
                    Type.PRIVATE -> createWaypointPrivate(plugin, viewer, name, location)
                    Type.PUBLIC -> createWaypointPublic(plugin, viewer, name, location)
                    Type.PERMISSION -> createWaypointPermission(plugin, viewer, name, permission!!, location)
                    else -> throw IllegalArgumentException("Cannot create waypoints with the gui of the type $type")
                }
            }

            return@onComplete when (result) {
                LimitReached -> AnvilGUI.Response.close()
                NameTaken -> {
                    name = null
                    AnvilGUI.Response.text(translations.WAYPOINT_CREATE_ENTER_NAME.text)
                }
                is SuccessWaypoint -> {
                    folder?.let {
                        result.waypoint.folder = it
                        open(GUIFolderPage(this, folder))
                    }

                    waypoint = result.waypoint

                    AnvilGUI.Response.close()
                }
                else -> throw IllegalStateException("Invalid return value $result")
            }
        }.onClose {
            val capturedWaypoint = waypoint

            if (capturedWaypoint == null) {
                goBack()
            } else {
                openWaypoint(capturedWaypoint)
            }
            plugin.runTask {
                gui.open()
            }
        }.open(viewer)
    }

    private var firstOpen = true

    internal fun open(page: BasePage) {
        if (firstOpen) {
            firstOpen = false
        } else {
            pageStack.push(gui.activePage as BasePage)
        }
        gui.activePage = page
        gui.update()
    }

    internal fun goBack() {
        if (pageStack.isEmpty()) {
            viewer.closeInventory()
            return
        }

        val page = pageStack.pop()
        page.update()

        gui.activePage = page
        gui.update()
    }

    internal fun getListingContent(guiFolder: GUIFolder): PaginationList<GUIDisplayable> {
        val content = PaginationList<GUIDisplayable>(ListingPage.PAGINATION_LIST_PAGE_SIZE)

        if (isOwner && guiFolder === targetData) {
            if (viewerData.showGlobals && plugin.waypointsConfig.general.features.globalWaypoints) {
                val public = plugin.api.publicWaypoints
                if (public.waypointsAmount > 0 || viewer.hasPermission(WaypointsPermissions.MODIFY_PUBLIC)) {
                    content.add(public);
                }
                val permission = plugin.api.permissionWaypoints;
                if (permission.getWaypointsVisibleForPlayer(viewer) > 0 || viewer.hasPermission(WaypointsPermissions.MODIFY_PERMISSION)) {
                    content.add(permission)
                }
            }
            if (plugin.waypointsConfig.general.features.deathWaypoints) {
                val deathFolder = targetData.deathFolder
                if (deathFolder.amount > 0) {
                    content.add(deathFolder)
                }
            }
            if (plugin.waypointsConfig.playerTracking.enabled && viewer.hasPermission(WaypointsPermissions.TRACKING_ENABLED)) {
                content.add(PlayerTrackingDisplayable)
            }
        }

        if (guiFolder.type == Type.PERMISSION && !viewer.hasPermission(WaypointsPermissions.MODIFY_PERMISSION)) {
            guiFolder.waypoints.forEach { waypoint ->
                if (viewer.hasPermission(waypoint.permission!!)) {
                    content.add(waypoint)
                }
            }

            guiFolder.folders.forEach { folder ->
                if (folder.getAmountVisibleForPlayer(viewer) > 0) {
                    content.add(folder)
                }
            }
        } else {
            content.addAll(guiFolder.waypoints)

            content.addAll(guiFolder.folders)
        }

        if (plugin.waypointsConfig.general.worldNotFound !== WorldNotFoundAction.SHOW) {
            content.removeAll {
                if (it is Waypoint && it.location.world === null) {
                    if (plugin.waypointsConfig.general.worldNotFound === WorldNotFoundAction.DELETE) {
                        it.delete()
                    }
                    true
                } else {
                    false
                }
            }
        }

        content.sortWith(viewerData.sortBy)

        return content
    }

    internal fun toGUIContent(guiDisplayable: GUIDisplayable): GUIContent {
        return extendApi {
            GUIItem(guiDisplayable.getItem(viewer)) {
                when (guiDisplayable) {
                    is WaypointHolder -> openHolder(guiDisplayable)
                    is Folder -> openFolder(guiDisplayable)
                    is Waypoint -> openWaypoint(guiDisplayable)
                    is PlayerTrackingDisplayable -> openPlayerTracking()
                    else -> throw IllegalStateException("The GUIDisplayable is of an unknown subclass ${guiDisplayable.javaClass.name}")
                }
            }
        }
    }

    internal fun getHolderForType(type: Type) = when (type) {
        Type.PRIVATE, Type.DEATH -> targetData
        Type.PUBLIC -> plugin.api.publicWaypoints
        Type.PERMISSION -> plugin.api.permissionWaypoints
    }

    init {
        openOverview()
        gui.open()
    }
}
