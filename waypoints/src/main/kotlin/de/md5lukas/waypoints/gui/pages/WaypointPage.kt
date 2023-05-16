package de.md5lukas.waypoints.gui.pages

import com.okkero.skedule.SynchronizationContext
import com.okkero.skedule.switchContext
import com.okkero.skedule.withSynchronizationContext
import de.md5lukas.kinvs.GUIPattern
import de.md5lukas.kinvs.items.GUIItem
import de.md5lukas.waypoints.WaypointsPermissions
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.gui.WaypointsGUI
import de.md5lukas.waypoints.integrations.DynMapIntegration
import de.md5lukas.waypoints.integrations.Pl3xMapIntegration
import de.md5lukas.waypoints.integrations.SquareMapIntegration
import de.md5lukas.waypoints.pointers.WaypointTrackable
import de.md5lukas.waypoints.util.SuccessWaypoint
import de.md5lukas.waypoints.util.asSingletonList
import de.md5lukas.waypoints.util.checkMaterialForCustomIcon
import de.md5lukas.waypoints.util.checkWaypointName
import de.md5lukas.waypoints.util.component1
import de.md5lukas.waypoints.util.component2
import de.md5lukas.waypoints.util.copyFieldsTo
import de.md5lukas.waypoints.util.createWaypointPermission
import de.md5lukas.waypoints.util.createWaypointPublic
import de.md5lukas.waypoints.util.onClickSuspending
import de.md5lukas.waypoints.util.placeholder
import de.md5lukas.waypoints.util.plainDisplayName
import de.md5lukas.waypoints.util.replaceInputText
import net.kyori.adventure.text.event.ClickEvent
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class WaypointPage(wpGUI: WaypointsGUI, private val waypoint: Waypoint) :
    BasePage(wpGUI, wpGUI.extendApi { waypoint.type.getBackgroundItem() }) {

  private companion object {
    /**
     * w = Waypoint Icon i = Get UUID (Global waypoints only) u = Move to public folder e = Move to
     * permission folder p = Change permission s = Select y = WebMap custom icon c = Select beacon
     * color f = Move to folder r = rename d = Delete t = Teleport b = Back
     */
    val waypointPattern =
        GUIPattern(
            "____w____",
            "_________",
            "i_u_s_y_c",
            "_f_e_p_r_",
            "d___t___b",
        )
  }

  private val isNotDeathWaypoint = waypoint.type !== Type.DEATH

  private val canModifyWaypoint =
      when (waypoint.type) {
        Type.PRIVATE,
        Type.DEATH -> wpGUI.isOwner
        Type.PUBLIC -> wpGUI.viewer.hasPermission(WaypointsPermissions.MODIFY_PUBLIC)
        Type.PERMISSION -> wpGUI.viewer.hasPermission(WaypointsPermissions.MODIFY_PERMISSION)
      }

  private suspend fun updatePage(update: Boolean = true) {
    applyPattern(
        waypointPattern,
        0,
        0,
        background,
        'w' to
            wpGUI.extendApi {
              GUIItem(
                  waypoint.getItem(wpGUI.viewer),
                  if (canModifyWaypoint) {
                    {
                      val newMaterial = wpGUI.viewer.inventory.itemInMainHand.type

                      if (checkMaterialForCustomIcon(wpGUI.plugin, newMaterial)) {
                        wpGUI.skedule {
                          waypoint.setMaterial(newMaterial)

                          updatePage()
                        }
                      } else {
                        wpGUI.translations.MESSAGE_WAYPOINT_NEW_ICON_INVALID.send(wpGUI.viewer)
                      }
                    }
                  } else null)
            },
        'i' to
            if (wpGUI.viewer.hasPermission(WaypointsPermissions.COMMAND_SCRIPTING) &&
                isNotDeathWaypoint) {
              GUIItem(wpGUI.translations.WAYPOINT_GET_UUID.item) {
                wpGUI.viewer.sendMessage(
                    wpGUI.translations.MESSAGE_WAYPOINT_GET_UUID.withReplacements(
                            "name" placeholder waypoint.name)
                        .clickEvent(
                            ClickEvent.clickEvent(
                                ClickEvent.Action.COPY_TO_CLIPBOARD, waypoint.id.toString())))
                wpGUI.viewer.closeInventory()
              }
            } else {
              background
            },
        'u' to
            if (wpGUI.plugin.waypointsConfig.general.features.globalWaypoints &&
                waypoint.type !== Type.PUBLIC &&
                isNotDeathWaypoint &&
                canModifyWaypoint &&
                wpGUI.viewer.hasPermission(WaypointsPermissions.MODIFY_PUBLIC)) {
              GUIItem(wpGUI.translations.WAYPOINT_MAKE_PUBLIC.item) {
                val nameResolver = "name" placeholder waypoint.name
                wpGUI.open(
                    ConfirmPage(
                        wpGUI,
                        wpGUI.translations.WAYPOINT_MAKE_PUBLIC_CONFIRM_QUESTION.getItem(
                            nameResolver),
                        wpGUI.translations.WAYPOINT_MAKE_PUBLIC_CONFIRM_FALSE.getItem(nameResolver),
                        wpGUI.translations.WAYPOINT_MAKE_PUBLIC_CONFIRM_TRUE.getItem(nameResolver),
                    ) {
                      if (it) {
                        wpGUI.skedule {
                          when (val result =
                              createWaypointPublic(
                                  wpGUI.plugin, wpGUI.viewer, waypoint.name, waypoint.location)) {
                            is SuccessWaypoint -> {
                              waypoint.copyFieldsTo(result.waypoint)
                              waypoint.delete()
                              wpGUI.goBack()
                              wpGUI.goBack()
                            }
                            else -> wpGUI.goBack()
                          }
                        }
                      } else {
                        wpGUI.goBack()
                      }
                    })
              }
            } else {
              background
            },
        'e' to
            if (wpGUI.plugin.waypointsConfig.general.features.globalWaypoints &&
                waypoint.type !== Type.PERMISSION &&
                isNotDeathWaypoint &&
                canModifyWaypoint &&
                wpGUI.viewer.hasPermission(WaypointsPermissions.MODIFY_PERMISSION)) {
              GUIItem(wpGUI.translations.WAYPOINT_MAKE_PERMISSION.item) {
                val nameResolver = "name" placeholder waypoint.name
                wpGUI.open(
                    ConfirmPage(
                        wpGUI,
                        wpGUI.translations.WAYPOINT_MAKE_PERMISSION_CONFIRM_QUESTION.getItem(
                            nameResolver),
                        wpGUI.translations.WAYPOINT_MAKE_PERMISSION_CONFIRM_FALSE.getItem(
                            nameResolver),
                        wpGUI.translations.WAYPOINT_MAKE_PERMISSION_CONFIRM_TRUE.getItem(
                            nameResolver),
                    ) {
                      if (it) {
                        AnvilGUI.Builder()
                            .plugin(wpGUI.plugin)
                            .itemLeft(
                                ItemStack(Material.PAPER).also { stack ->
                                  stack.plainDisplayName =
                                      wpGUI.translations.WAYPOINT_CREATE_ENTER_PERMISSION.rawText
                                })
                            .onClickSuspending(wpGUI.scheduler) {
                                slot,
                                (isOutputInvalid, permission) ->
                              if (slot != AnvilGUI.Slot.OUTPUT || isOutputInvalid)
                                  return@onClickSuspending emptyList()

                              when (val result =
                                  createWaypointPermission(
                                      wpGUI.plugin,
                                      wpGUI.viewer,
                                      waypoint.name,
                                      permission,
                                      waypoint.location)) {
                                is SuccessWaypoint -> {
                                  waypoint.copyFieldsTo(result.waypoint)
                                  waypoint.delete()
                                  wpGUI.goBack()
                                  wpGUI.goBack()
                                }
                                else -> wpGUI.goBack()
                              }

                              return@onClickSuspending AnvilGUI.ResponseAction.close()
                                  .asSingletonList()
                            }
                            .onClose {
                              (wpGUI.gui.activePage as BasePage).update()
                              wpGUI.schedule { wpGUI.gui.open() }
                            }
                            .open(wpGUI.viewer)
                      } else {
                        wpGUI.goBack()
                      }
                    })
              }
            } else {
              background
            },
        'p' to
            if (waypoint.type === Type.PERMISSION && canModifyWaypoint) {
              GUIItem(
                  wpGUI.translations.WAYPOINT_EDIT_PERMISSION.getItem(
                      "permission" placeholder (waypoint.permission ?: ""))) {
                    AnvilGUI.Builder()
                        .plugin(wpGUI.plugin)
                        .itemLeft(
                            ItemStack(Material.PAPER).also {
                              it.plainDisplayName = waypoint.permission ?: ""
                            })
                        .onClickSuspending(wpGUI.scheduler) { slot, (isOutputInvalid, permission) ->
                          if (slot != AnvilGUI.Slot.OUTPUT || isOutputInvalid)
                              return@onClickSuspending emptyList()

                          waypoint.setPermission(permission)
                          return@onClickSuspending AnvilGUI.ResponseAction.close().asSingletonList()
                        }
                        .onClose {
                          wpGUI.skedule {
                            updatePage()
                            switchContext(SynchronizationContext.SYNC)
                            wpGUI.gui.open()
                          }
                        }
                        .open(wpGUI.viewer)
                  }
            } else {
              background
            },
        's' to
            if (waypoint.location.world !== null) {
              if (wpGUI.plugin.pointerManager.getCurrentTargets(wpGUI.viewer).any {
                (it as? WaypointTrackable)?.waypoint == waypoint
              }) {
                GUIItem(wpGUI.translations.WAYPOINT_DESELECT.item) {
                  wpGUI.plugin.pointerManager.disable(wpGUI.viewer) {
                    WaypointTrackable.Extract(it) == waypoint
                  }
                  wpGUI.skedule { updatePage() }
                }
              } else {
                GUIItem(wpGUI.translations.WAYPOINT_SELECT.item) {
                  wpGUI.viewer.closeInventory()
                  wpGUI.plugin.pointerManager.enable(
                      wpGUI.viewer, WaypointTrackable(wpGUI.plugin, waypoint))
                }
              }
            } else {
              background
            },
        'y' to
            if (canModifyWaypoint && waypoint.type === Type.PUBLIC) {
              when {
                wpGUI.plugin.dynMapIntegrationAvailable -> {
                  createChangeCustomMapIconItem(
                      DynMapIntegration.CUSTOM_DATA_KEY,
                      wpGUI.plugin.waypointsConfig.integrations.dynmap.icon)
                }
                wpGUI.plugin.squareMapIntegrationAvailable -> {
                  createChangeCustomMapIconItem(
                      SquareMapIntegration.CUSTOM_DATA_KEY,
                      wpGUI.plugin.waypointsConfig.integrations.squaremap.icon)
                }
                wpGUI.plugin.pl3xMapIntegrationAvailable -> {
                  createChangeCustomMapIconItem(
                      Pl3xMapIntegration.CUSTOM_DATA_KEY,
                      wpGUI.plugin.waypointsConfig.integrations.pl3xmap.icon)
                }
                else -> {
                  background
                }
              }
            } else {
              background
            },
        'c' to
            if (canModifyWaypoint &&
                isNotDeathWaypoint &&
                wpGUI.plugin.waypointsConfig.pointers.beacon.enabled) {
              GUIItem(wpGUI.translations.WAYPOINT_SELECT_BEACON_COLOR.item) {
                wpGUI.open(SelectBeaconColorPage(wpGUI, waypoint))
              }
            } else {
              background
            },
        'f' to
            if (canModifyWaypoint && isNotDeathWaypoint) {
              GUIItem(wpGUI.translations.WAYPOINT_MOVE_TO_FOLDER.item) {
                wpGUI.skedule {
                  val page = MoveToFolderPage(wpGUI, waypoint).apply { init() }
                  switchContext(SynchronizationContext.SYNC)
                  wpGUI.open(page)
                }
              }
            } else {
              background
            },
        'r' to
            if (canModifyWaypoint && isNotDeathWaypoint) {
              GUIItem(wpGUI.translations.WAYPOINT_RENAME.item) {
                wpGUI.viewer.closeInventory()
                AnvilGUI.Builder()
                    .plugin(wpGUI.plugin)
                    .itemLeft(
                        ItemStack(Material.PAPER).also { it.plainDisplayName = waypoint.name })
                    .onClickSuspending(wpGUI.scheduler) { slot, (isOutputInvalid, newName) ->
                      if (slot != AnvilGUI.Slot.OUTPUT || isOutputInvalid)
                          return@onClickSuspending emptyList()

                      val holder = wpGUI.getHolderForType(waypoint.type)

                      if (checkWaypointName(wpGUI.plugin, holder, newName)) {
                        waypoint.setName(newName)

                        updatePage()
                      } else {
                        when (waypoint.type) {
                          Type.PRIVATE -> wpGUI.translations.WAYPOINT_NAME_DUPLICATE_PRIVATE
                          Type.PUBLIC -> wpGUI.translations.WAYPOINT_NAME_DUPLICATE_PUBLIC
                          Type.PERMISSION -> wpGUI.translations.WAYPOINT_NAME_DUPLICATE_PERMISSION
                          else ->
                              throw IllegalArgumentException(
                                  "Waypoints of the type ${waypoint.type} have no name")
                        }.send(wpGUI.viewer)
                        return@onClickSuspending replaceInputText(newName).asSingletonList()
                      }

                      return@onClickSuspending AnvilGUI.ResponseAction.close().asSingletonList()
                    }
                    .onClose { wpGUI.schedule { wpGUI.gui.open() } }
                    .open(wpGUI.viewer)
              }
            } else {
              background
            },
        'd' to
            if (canModifyWaypoint) {
              GUIItem(wpGUI.translations.WAYPOINT_DELETE.item) {
                val nameResolver = "name" placeholder waypoint.name
                wpGUI.open(
                    ConfirmPage(
                        wpGUI,
                        wpGUI.translations.WAYPOINT_DELETE_CONFIRM_QUESTION.getItem(nameResolver),
                        wpGUI.translations.WAYPOINT_DELETE_CONFIRM_FALSE.getItem(nameResolver),
                        wpGUI.translations.WAYPOINT_DELETE_CONFIRM_TRUE.getItem(nameResolver),
                    ) {
                      if (it) {
                        wpGUI.skedule {
                          waypoint.delete()
                          switchContext(SynchronizationContext.SYNC)
                          wpGUI.goBack()
                          wpGUI.goBack()
                        }
                      } else {
                        wpGUI.goBack()
                      }
                    })
              }
            } else {
              background
            },
        't' to
            if ((wpGUI.viewer.hasPermission(
                wpGUI.plugin.teleportManager.getTeleportPermission(waypoint)) ||
                wpGUI.plugin.teleportManager.isTeleportEnabled(wpGUI.targetData, waypoint)) &&
                waypoint.location.world !== null) {
              GUIItem(
                  wpGUI.translations.WAYPOINT_TELEPORT.getItem().also { stack ->
                    val currentLore = stack.lore()!!
                    wpGUI.plugin.teleportManager
                        .getTeleportCostDescription(wpGUI.viewer, waypoint)
                        ?.let { currentLore += it }
                    if (!wpGUI.plugin.teleportManager.isAllowedToTeleportToWaypoint(
                        wpGUI.viewer, waypoint)) {
                      currentLore += wpGUI.translations.WAYPOINT_TELEPORT_MUST_VISIT.text
                    }
                    stack.lore(currentLore)
                  }) {
                    wpGUI.skedule {
                      if (wpGUI.plugin.teleportManager.isAllowedToTeleportToWaypoint(
                          wpGUI.viewer, waypoint)) {
                        withSynchronizationContext(SynchronizationContext.SYNC) {
                          wpGUI.viewer.closeInventory()
                        }
                        wpGUI.plugin.teleportManager.teleportPlayerToWaypoint(
                            wpGUI.viewer, waypoint)
                      }
                    }
                  }
            } else {
              background
            },
        'b' to GUIItem(wpGUI.translations.GENERAL_BACK.item) { wpGUI.goBack() },
    )

    if (update) {
      withSynchronizationContext(SynchronizationContext.SYNC) { wpGUI.gui.update() }
    }
  }

  private fun createChangeCustomMapIconItem(customDataKey: String, defaultIcon: String) =
      GUIItem(wpGUI.translations.WAYPOINT_CHANGE_MAP_ICON.item) {
        wpGUI.skedule {
          val builder =
              AnvilGUI.Builder()
                  .plugin(wpGUI.plugin)
                  .itemLeft(
                      ItemStack(Material.PAPER).also {
                        it.plainDisplayName = waypoint.getCustomData(customDataKey) ?: defaultIcon
                      })
                  .onClickSuspending(wpGUI.scheduler) { slot, (isOutputInvalid, newIcon) ->
                    if (slot != AnvilGUI.Slot.OUTPUT || isOutputInvalid)
                        return@onClickSuspending emptyList()

                    waypoint.setCustomData(customDataKey, newIcon.ifBlank { null })
                    return@onClickSuspending AnvilGUI.ResponseAction.close().asSingletonList()
                  }
                  .onClose { wpGUI.schedule { wpGUI.gui.open() } }
          switchContext(SynchronizationContext.SYNC)
          wpGUI.viewer.closeInventory()
          builder.open(wpGUI.viewer)
        }
      }

  suspend fun init() {
    updatePage(false)
  }
}
