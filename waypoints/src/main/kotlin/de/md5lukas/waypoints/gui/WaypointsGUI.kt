package de.md5lukas.waypoints.gui

import com.okkero.skedule.SynchronizationContext
import com.okkero.skedule.skedule
import com.okkero.skedule.switchContext
import de.md5lukas.commons.collections.PaginationList
import de.md5lukas.kinvs.GUI
import de.md5lukas.kinvs.items.GUIContent
import de.md5lukas.kinvs.items.GUIItem
import de.md5lukas.schedulers.NOOP
import de.md5lukas.schedulers.Schedulers
import de.md5lukas.waypoints.WaypointsPermissions
import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.*
import de.md5lukas.waypoints.api.gui.GUIDisplayable
import de.md5lukas.waypoints.api.gui.GUIFolder
import de.md5lukas.waypoints.config.general.WorldNotFoundAction
import de.md5lukas.waypoints.gui.pages.*
import de.md5lukas.waypoints.util.*
import kotlinx.coroutines.CoroutineScope
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

class WaypointsGUI(
    internal val plugin: WaypointsPlugin,
    internal val viewer: Player,
    target: UUID,
) {

    private val pageStack = ArrayDeque<BasePage>()

    internal val apiExtensions = plugin.apiExtensions

    internal inline fun <T> extendApi(block: APIExtensions.() -> T) = apiExtensions.block()

    internal val isOwner = viewer.uniqueId == target

    internal lateinit var viewerData: WaypointsPlayer
        private set
    internal lateinit var targetData: WaypointsPlayer
        private set

    internal val translations = plugin.translations

    internal val gui = GUI(
        viewer,
        plugin.server.createInventory(
            null,
            5 * 9,
            if (isOwner) {
                plugin.translations.INVENTORY_TITLE_SELF.text
            } else {
                val otherName = plugin.uuidUtils.getName(target)

                if (otherName.isEmpty) {
                    throw IllegalArgumentException("A player with the UUID $target does not exist.")
                }

                plugin.translations.INVENTORY_TITLE_OTHER.withReplacements("name" placeholder otherName.get())
            }
        ),
    )

    suspend fun openOverview() {
        val page = GUIFolderPage(this, targetData).apply { init() }
        switchContext(SynchronizationContext.SYNC)
        open(page)
    }

    suspend fun openHolder(holder: WaypointHolder) {
        val page = GUIFolderPage(this, holder).apply { init() }
        switchContext(SynchronizationContext.SYNC)
        open(page)
    }

    suspend fun openFolder(folder: Folder) {
        val page = GUIFolderPage(this, folder).apply { init() }
        switchContext(SynchronizationContext.SYNC)
        open(page)
    }

    suspend fun openWaypoint(waypoint: Waypoint) {
        val page = WaypointPage(this, waypoint).apply { init() }
        switchContext(SynchronizationContext.SYNC)
        open(page)
    }

    suspend fun openPlayerTracking() {
        val page = PlayerTrackingPage(this).apply { init() }
        switchContext(SynchronizationContext.SYNC)
        open(page)
    }

    fun openCreateFolder(waypointHolder: WaypointHolder) {
        AnvilGUI.Builder().plugin(plugin).itemLeft(ItemStack(Material.PAPER).also { it.plainDisplayName = translations.FOLDER_CREATE_ENTER_NAME.rawText })
            .onClickSuspending(plugin) { slot, (name) ->
                if (slot != AnvilGUI.Slot.OUTPUT)
                    return@onClickSuspending emptyList()

                val result = when (waypointHolder.type) {
                    Type.PRIVATE -> createFolderPrivate(plugin, viewer, name)
                    Type.PUBLIC -> createFolderPublic(plugin, viewer, name)
                    Type.PERMISSION -> createFolderPermission(plugin, viewer, name)
                    else -> throw IllegalStateException("Cannot create folders of the type ${waypointHolder.type}")
                }

                return@onClickSuspending when (result) {
                    NameTaken -> replaceInputText(translations.FOLDER_CREATE_ENTER_NAME.rawText)
                    LimitReached, is SuccessFolder -> AnvilGUI.ResponseAction.close()
                    else -> throw IllegalStateException("Invalid return value $result")
                }.asSingletonList()
            }.onClose {
                (gui.activePage as BasePage).update()
                schedule {
                    gui.open()
                }
            }.open(viewer)
    }

    fun openCreateWaypoint(type: Type, folder: Folder?, location: Location = viewer.location) {
        var waypoint: Waypoint? = null

        var name: String? = null
        var permission: String? = null
        AnvilGUI.Builder().plugin(plugin).itemLeft(ItemStack(Material.PAPER).also { it.plainDisplayName = translations.WAYPOINT_CREATE_ENTER_NAME.rawText })
            .onClickSuspending(plugin) { slot, (enteredText) ->
                if (slot != AnvilGUI.Slot.OUTPUT)
                    return@onClickSuspending emptyList()

                if (name == null) {
                    name = enteredText

                    if (type == Type.PERMISSION && permission == null) {
                        return@onClickSuspending replaceInputText(translations.WAYPOINT_CREATE_ENTER_PERMISSION.rawText).asSingletonList()
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

                return@onClickSuspending when (result) {
                    LimitReached -> AnvilGUI.ResponseAction.close().asSingletonList()
                    NameTaken -> {
                        name = null
                        replaceInputText(translations.WAYPOINT_CREATE_ENTER_NAME.rawText).asSingletonList()
                    }

                    is SuccessWaypoint -> {
                        folder?.let {
                            result.waypoint.setFolder(it)
                            open(GUIFolderPage(this, folder))
                        }

                        waypoint = result.waypoint

                        AnvilGUI.ResponseAction.close().asSingletonList()
                    }

                    else -> throw IllegalStateException("Invalid return value $result")
                }
            }.onClose {
                val capturedWaypoint = waypoint

                skedule {
                    if (capturedWaypoint == null) {
                        goBack()
                    } else {
                        openWaypoint(capturedWaypoint)
                    }
                    switchContext(SynchronizationContext.SYNC)
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

    internal suspend fun getListingContent(guiFolder: GUIFolder): PaginationList<GUIDisplayable> {
        val content = PaginationList<GUIDisplayable>(ListingPage.PAGINATION_LIST_PAGE_SIZE)

        if (isOwner && guiFolder === targetData) {
            if (viewerData.showGlobals && plugin.waypointsConfig.general.features.globalWaypoints) {
                val public = plugin.api.publicWaypoints
                if (public.getWaypointsAmount() > 0 || viewer.hasPermission(WaypointsPermissions.MODIFY_PUBLIC)) {
                    content.add(public)
                }
                val permission = plugin.api.permissionWaypoints
                if (permission.getWaypointsVisibleForPlayer(viewer) > 0 || viewer.hasPermission(WaypointsPermissions.MODIFY_PERMISSION)) {
                    content.add(permission)
                }
            }
            if (plugin.waypointsConfig.general.features.deathWaypoints) {
                val deathFolder = targetData.deathFolder
                if (deathFolder.getAmount() > 0) {
                    content.add(deathFolder)
                }
            }
            if (plugin.waypointsConfig.playerTracking.enabled && viewer.hasPermission(WaypointsPermissions.TRACKING_ENABLED)) {
                content.add(PlayerTrackingDisplayable)
            }
        }

        if (guiFolder.type == Type.PERMISSION && !viewer.hasPermission(WaypointsPermissions.MODIFY_PERMISSION)) {
            guiFolder.getWaypoints().forEach { waypoint ->
                if (viewer.hasPermission(waypoint.permission!!)) {
                    content.add(waypoint)
                }
            }

            guiFolder.getFolders().forEach { folder ->
                if (folder.getAmountVisibleForPlayer(viewer) > 0) {
                    content.add(folder)
                }
            }
        } else {
            content.addAll(guiFolder.getWaypoints())

            content.addAll(guiFolder.getFolders())
        }

        if (plugin.waypointsConfig.general.worldNotFound !== WorldNotFoundAction.SHOW) {
            val itr = content.iterator()
            while (itr.hasNext()) {
                val it = itr.next()
                if (it is Waypoint && it.location.world === null) {
                    if (plugin.waypointsConfig.general.worldNotFound === WorldNotFoundAction.DELETE) {
                        it.delete()
                    }
                    itr.remove()
                }
            }
        }

        content.sortWith(viewerData.sortBy)

        return content
    }

    internal suspend fun toGUIContent(guiDisplayable: GUIDisplayable): GUIContent {
        return extendApi {
            GUIItem(guiDisplayable.getItem(viewer)) {
                skedule {
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
    }

    internal fun getHolderForType(type: Type) = when (type) {
        Type.PRIVATE, Type.DEATH -> targetData
        Type.PUBLIC -> plugin.api.publicWaypoints
        Type.PERMISSION -> plugin.api.permissionWaypoints
    }

    private val scheduler = Schedulers.entity(plugin, viewer)

    internal fun schedule(block: Runnable) = scheduler.schedule(NOOP, block)
    internal fun skedule(sync: SynchronizationContext = SynchronizationContext.ASYNC, block: suspend CoroutineScope.() -> Unit) =
        scheduler.skedule(sync, block)


    init {
        skedule {
            viewerData = plugin.api.getWaypointPlayer(viewer.uniqueId)
            targetData = plugin.api.getWaypointPlayer(target)
            openOverview()
            switchContext(SynchronizationContext.SYNC)
            gui.open()
        }
    }
}
