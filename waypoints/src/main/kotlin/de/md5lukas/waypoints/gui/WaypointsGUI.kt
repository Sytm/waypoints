package de.md5lukas.waypoints.gui

import com.okkero.skedule.SynchronizationContext
import com.okkero.skedule.skedule
import com.okkero.skedule.switchContext
import de.md5lukas.commons.paper.placeholder
import de.md5lukas.commons.paper.plainDisplayName
import de.md5lukas.kinvs.GUI
import de.md5lukas.schedulers.Schedulers
import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.*
import de.md5lukas.waypoints.config.sounds.SoundsConfiguration
import de.md5lukas.waypoints.gui.pages.*
import de.md5lukas.waypoints.util.*
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.await
import net.kyori.adventure.sound.Sound
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class WaypointsGUI(
    internal val plugin: WaypointsPlugin,
    internal val viewer: Player,
    target: UUID,
) {

  private val pageStack = ArrayDeque<BasePage>()

  internal val apiExtensions
    get() = plugin.apiExtensions

  internal inline fun <T> extendApi(block: APIExtensions.() -> T) = apiExtensions.block()

  internal val isOwner = viewer.uniqueId == target

  internal lateinit var viewerData: WaypointsPlayer
    private set

  internal lateinit var targetData: WaypointsPlayer
    private set

  internal val translations
    get() = plugin.translations

  internal lateinit var gui: GUI
    private set

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

  suspend fun openShared() {
    val page = SharedWaypointsPage(this).apply { init() }
    switchContext(SynchronizationContext.SYNC)
    open(page)
  }

  fun openCreateFolder(waypointHolder: WaypointHolder) {
    AnvilGUI.Builder()
        .plugin(plugin)
        .itemLeft(
            ItemStack(Material.PAPER).also {
              it.plainDisplayName = translations.FOLDER_CREATE_ENTER_NAME.rawText
            })
        .onClickSuspending(scheduler) { slot, (isOutputInvalid, name) ->
          if (slot != AnvilGUI.Slot.OUTPUT || isOutputInvalid) return@onClickSuspending emptyList()

          val result =
              when (waypointHolder.type) {
                Type.PRIVATE -> createFolderPrivate(plugin, viewer, name)
                Type.PUBLIC -> createFolderPublic(plugin, viewer, name)
                Type.PERMISSION -> createFolderPermission(plugin, viewer, name)
                else ->
                    throw IllegalStateException(
                        "Cannot create folders of the type ${waypointHolder.type}")
              }

          if (result is SuccessFolder) {
            playSound { clickSuccess }
          } else {
            playSound { clickError }
          }

          return@onClickSuspending listOf(
              when (result) {
                NameTaken -> replaceInputText(translations.FOLDER_CREATE_ENTER_NAME.rawText)
                LimitReached,
                is SuccessFolder -> AnvilGUI.ResponseAction.close()
                else -> throw IllegalStateException("Invalid return value $result")
              })
        }
        .onClose {
          (gui.activePage as BasePage).update()
          schedule { gui.open() }
        }
        .open(viewer)
  }

  fun openCreateWaypoint(type: Type, folder: Folder?, location: Location = viewer.location) {
    var waypoint: Waypoint? = null

    var name: String? = null
    var permission: String? = null
    AnvilGUI.Builder()
        .plugin(plugin)
        .itemLeft(
            ItemStack(Material.PAPER).also {
              it.plainDisplayName = translations.WAYPOINT_CREATE_ENTER_NAME.rawText
            })
        .onClickSuspending(scheduler) { slot, (isOutputInvalid, enteredText) ->
          if (slot != AnvilGUI.Slot.OUTPUT || isOutputInvalid) return@onClickSuspending emptyList()

          if (name == null) {
            name = enteredText

            if (type == Type.PERMISSION && permission == null) {
              playSound { clickNormal }
              return@onClickSuspending listOf(
                  replaceInputText(translations.WAYPOINT_CREATE_ENTER_PERMISSION.rawText))
            }
          } else if (type == Type.PERMISSION && permission == null) {
            permission = enteredText
          }

          val result =
              name!!.let { name ->
                when (type) {
                  Type.PRIVATE -> createWaypointPrivate(plugin, viewer, name, location)
                  Type.PUBLIC -> createWaypointPublic(plugin, viewer, name, location)
                  Type.PERMISSION ->
                      createWaypointPermission(plugin, viewer, name, permission!!, location)
                  else ->
                      throw IllegalArgumentException(
                          "Cannot create waypoints with the gui of the type $type")
                }
              }

          if (result !is SuccessWaypoint) {
            playSound { clickError }
          }

          return@onClickSuspending listOf(
              when (result) {
                LimitReached -> AnvilGUI.ResponseAction.close()
                NameTaken -> {
                  name = null
                  replaceInputText(translations.WAYPOINT_CREATE_ENTER_NAME.rawText)
                }
                is SuccessWaypoint -> {
                  folder?.let {
                    result.waypoint.setFolder(it)
                    open(GUIFolderPage(this, folder))
                  }

                  waypoint = result.waypoint

                  AnvilGUI.ResponseAction.close()
                }
                else -> throw IllegalStateException("Invalid return value $result")
              })
        }
        .onClose {
          val capturedWaypoint = waypoint

          skedule {
            if (capturedWaypoint == null) {
              switchContext(SynchronizationContext.SYNC)
              goBack()
            } else {
              openWaypoint(capturedWaypoint)
            }
            switchContext(SynchronizationContext.SYNC)
            gui.open()
          }
        }
        .open(viewer)
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

  internal fun getHolderForType(type: Type) =
      when (type) {
        Type.PRIVATE,
        Type.DEATH -> targetData
        Type.PUBLIC -> plugin.api.publicWaypoints
        Type.PERMISSION -> plugin.api.permissionWaypoints
      }

  internal val scheduler = Schedulers.entity(plugin, viewer)

  internal fun schedule(block: Runnable) = scheduler.schedule(null, block)

  internal fun skedule(
      sync: SynchronizationContext = SynchronizationContext.ASYNC,
      block: suspend CoroutineScope.() -> Unit
  ) = scheduler.skedule(sync, block)

  internal inline fun playSound(sound: SoundsConfiguration.() -> Sound) {
    viewer.playSound(plugin.waypointsConfig.sounds.sound())
  }

  init {
    skedule {
      viewerData = plugin.api.getWaypointPlayer(viewer.uniqueId)
      targetData = plugin.api.getWaypointPlayer(target)
      gui =
          GUI(
              viewer,
              5,
              if (isOwner) {
                plugin.translations.INVENTORY_TITLE_SELF.text
              } else {
                val otherName = plugin.uuidUtils.getNameAsync(target).await()

                if (otherName === null) {
                  throw IllegalArgumentException("A player with the UUID $target does not exist.")
                }

                plugin.translations.INVENTORY_TITLE_OTHER.withReplacements(
                    "name" placeholder otherName)
              },
          )
      openOverview()
      switchContext(SynchronizationContext.SYNC)
      playSound { openGui }
      gui.open()
    }
  }
}
