package de.md5lukas.waypoints.gui.pages

import de.md5lukas.kinvs.GUIPattern
import de.md5lukas.kinvs.items.GUIItem
import de.md5lukas.waypoints.WaypointsPermissions
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.gui.WaypointsGUI
import de.md5lukas.waypoints.integrations.DynMapIntegration
import de.md5lukas.waypoints.integrations.SquareMapIntegration
import de.md5lukas.waypoints.util.*
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import net.wesjd.anvilgui.AnvilGUI
import java.util.*

class WaypointPage(wpGUI: WaypointsGUI, private val waypoint: Waypoint) : BasePage(wpGUI, wpGUI.extendApi { waypoint.type.getBackgroundItem() }) {

    private companion object {
        /**
         * w = Waypoint Icon
         * i = Get UUID (Global waypoints only)
         * u = Move to public folder
         * e = Move to permission folder
         * p = Change permission
         * s = Select
         * y = Dynmap custom icon
         * c = Select beacon color
         * f = Move to folder
         * r = rename
         * d = Delete
         * t = Teleport
         * b = Back
         */
        val waypointPattern = GUIPattern(
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
            Type.PRIVATE, Type.DEATH -> wpGUI.isOwner
            Type.PUBLIC -> wpGUI.viewer.hasPermission(WaypointsPermissions.MODIFY_PUBLIC)
            Type.PERMISSION -> wpGUI.viewer.hasPermission(WaypointsPermissions.MODIFY_PERMISSION)
        }

    private fun updatePage(update: Boolean = true) {
        applyPattern(
            waypointPattern,
            0, 0,
            background,
            'w' to wpGUI.extendApi {
                GUIItem(waypoint.getItem(wpGUI.viewer), if (canModifyWaypoint) {
                    {
                        val newMaterial = wpGUI.viewer.inventory.itemInMainHand.type

                        if (checkMaterialForCustomIcon(wpGUI.plugin, newMaterial)) {
                            waypoint.material = newMaterial

                            updatePage()
                        } else {
                            wpGUI.translations.MESSAGE_WAYPOINT_NEW_ICON_INVALID.send(wpGUI.viewer)
                        }
                    }
                } else null)
            },
            'i' to if (wpGUI.viewer.hasPermission(WaypointsPermissions.COMMAND_SCRIPTING) && isNotDeathWaypoint) {
                GUIItem(wpGUI.translations.WAYPOINT_GET_UUID.item) {
                    val messageString = wpGUI.translations.MESSAGE_WAYPOINT_GET_UUID.withReplacements(Collections.singletonMap("name", waypoint.name))

                    val clickEvent = ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, waypoint.id.toString())

                    val components = TextComponent.fromLegacyText(messageString)
                    components.forEach { component ->
                        component.clickEvent = clickEvent
                    }

                    wpGUI.viewer.spigot().sendMessage(*components)
                    wpGUI.viewer.closeInventory()
                }
            } else {
                background
            },
            'u' to if (wpGUI.plugin.waypointsConfig.general.features.globalWaypoints
                && waypoint.type !== Type.PUBLIC && isNotDeathWaypoint && canModifyWaypoint
                && wpGUI.viewer.hasPermission(WaypointsPermissions.MODIFY_PUBLIC)
            ) {
                GUIItem(wpGUI.translations.WAYPOINT_MAKE_PUBLIC.item) {
                    val nameMap = Collections.singletonMap(
                        "name",
                        waypoint.name
                    )
                    wpGUI.open(
                        ConfirmPage(
                            wpGUI,
                            wpGUI.translations.WAYPOINT_MAKE_PUBLIC_CONFIRM_QUESTION.getItem(nameMap),
                            wpGUI.translations.WAYPOINT_MAKE_PUBLIC_CONFIRM_FALSE.getItem(nameMap),
                            wpGUI.translations.WAYPOINT_MAKE_PUBLIC_CONFIRM_TRUE.getItem(nameMap),
                        ) {
                            if (it) {
                                when (val result = createWaypointPublic(wpGUI.plugin, wpGUI.viewer, waypoint.name, waypoint.location)) {
                                    is SuccessWaypoint -> {
                                        waypoint.copyFieldsTo(result.waypoint)
                                        waypoint.delete()
                                        wpGUI.goBack()
                                        wpGUI.goBack()
                                    }
                                    else -> wpGUI.goBack()
                                }
                            } else {
                                wpGUI.goBack()
                            }
                        }
                    )
                }
            } else {
                background
            },
            'e' to if (wpGUI.plugin.waypointsConfig.general.features.globalWaypoints
                && waypoint.type !== Type.PERMISSION && isNotDeathWaypoint && canModifyWaypoint
                && wpGUI.viewer.hasPermission(WaypointsPermissions.MODIFY_PERMISSION)
            ) {
                GUIItem(wpGUI.translations.WAYPOINT_MAKE_PERMISSION.item) {
                    val nameMap = Collections.singletonMap(
                        "name",
                        waypoint.name
                    )
                    wpGUI.open(
                        ConfirmPage(
                            wpGUI,
                            wpGUI.translations.WAYPOINT_MAKE_PERMISSION_CONFIRM_QUESTION.getItem(nameMap),
                            wpGUI.translations.WAYPOINT_MAKE_PERMISSION_CONFIRM_FALSE.getItem(nameMap),
                            wpGUI.translations.WAYPOINT_MAKE_PERMISSION_CONFIRM_TRUE.getItem(nameMap),
                        ) {
                            if (it) {
                                AnvilGUI.Builder().plugin(wpGUI.plugin).text(wpGUI.translations.WAYPOINT_CREATE_ENTER_PERMISSION.text)
                                    .onComplete { (permission) ->
                                        when (val result = createWaypointPermission(wpGUI.plugin, wpGUI.viewer, waypoint.name, permission, waypoint.location)) {
                                            is SuccessWaypoint -> {
                                                waypoint.copyFieldsTo(result.waypoint)
                                                waypoint.delete()
                                                wpGUI.goBack()
                                                wpGUI.goBack()
                                            }

                                            else -> wpGUI.goBack()
                                        }

                                        return@onComplete AnvilGUI.ResponseAction.close().asSingletonList()
                                    }.onClose {
                                        (wpGUI.gui.activePage as BasePage).update()
                                        wpGUI.plugin.runTask {
                                            wpGUI.gui.open()
                                        }
                                    }.open(wpGUI.viewer)
                            } else {
                                wpGUI.goBack()
                            }
                        }
                    )
                }
            } else {
                background
            },
            'p' to if (waypoint.type === Type.PERMISSION && canModifyWaypoint) {
                GUIItem(wpGUI.translations.WAYPOINT_EDIT_PERMISSION.getItem(Collections.singletonMap("permission", waypoint.permission ?: ""))) {
                    AnvilGUI.Builder().plugin(wpGUI.plugin).text(waypoint.permission).onComplete { (permission) ->
                        waypoint.permission = permission
                        return@onComplete AnvilGUI.ResponseAction.close().asSingletonList()
                    }.onClose {
                        updatePage()
                        wpGUI.plugin.runTask {
                            wpGUI.gui.open()
                        }
                    }.open(wpGUI.viewer)
                }
            } else {
                background
            },
            's' to if (waypoint.location.world !== null) {
                GUIItem(wpGUI.translations.WAYPOINT_SELECT.item) {
                    wpGUI.viewer.closeInventory()
                    wpGUI.plugin.api.pointerManager.enable(wpGUI.viewer, waypoint)
                }
            } else {
                background
            },
            'y' to if (canModifyWaypoint && waypoint.type === Type.PUBLIC) {
                if (wpGUI.plugin.dynMapIntegrationAvailable) {
                    createChangeCustomMapIconItem(DynMapIntegration.CUSTOM_DATA_KEY, wpGUI.plugin.waypointsConfig.integrations.dynmap.icon)
                } else if (wpGUI.plugin.squareMapIntegrationAvailable) {
                    createChangeCustomMapIconItem(SquareMapIntegration.CUSTOM_DATA_KEY, wpGUI.plugin.waypointsConfig.integrations.squaremap.icon)
                } else {
                    background
                }
            } else {
                background
            },
            'c' to if (canModifyWaypoint && isNotDeathWaypoint && wpGUI.plugin.waypointsConfig.pointer.beacon.enabled) {
                GUIItem(wpGUI.translations.WAYPOINT_SELECT_BEACON_COLOR.item) {
                    wpGUI.open(SelectBeaconColorPage(wpGUI, waypoint))
                }
            } else {
                background
            },
            'f' to if (canModifyWaypoint && isNotDeathWaypoint) {
                GUIItem(wpGUI.translations.WAYPOINT_MOVE_TO_FOLDER.item) {
                    wpGUI.open(MoveToFolderPage(wpGUI, waypoint))
                }
            } else {
                background
            },
            'r' to if (canModifyWaypoint && isNotDeathWaypoint) {
                GUIItem(wpGUI.translations.WAYPOINT_RENAME.item) {
                    wpGUI.viewer.closeInventory()
                    AnvilGUI.Builder().plugin(wpGUI.plugin).text(waypoint.name).onComplete { (newName) ->
                        val holder = wpGUI.getHolderForType(waypoint.type)

                        if (checkWaypointName(wpGUI.plugin, holder, newName)) {
                            waypoint.name = newName

                            updatePage()
                        } else {
                            when (waypoint.type) {
                                Type.PRIVATE -> wpGUI.translations.WAYPOINT_NAME_DUPLICATE_PRIVATE
                                Type.PUBLIC -> wpGUI.translations.WAYPOINT_NAME_DUPLICATE_PUBLIC
                                Type.PERMISSION -> wpGUI.translations.WAYPOINT_NAME_DUPLICATE_PERMISSION
                                else -> throw IllegalArgumentException("Waypoints of the type ${waypoint.type} have no name")
                            }.send(wpGUI.viewer)
                        }

                        return@onComplete AnvilGUI.ResponseAction.close().asSingletonList()
                    }.onClose {
                        wpGUI.plugin.runTask {
                            wpGUI.gui.open()
                        }
                    }.open(wpGUI.viewer)
                }
            } else {
                background
            },
            'd' to if (canModifyWaypoint) {
                GUIItem(wpGUI.translations.WAYPOINT_DELETE.item) {
                    val nameMap = Collections.singletonMap(
                        "name",
                        waypoint.name
                    )
                    wpGUI.open(
                        ConfirmPage(
                            wpGUI,
                            wpGUI.translations.WAYPOINT_DELETE_CONFIRM_QUESTION.getItem(nameMap),
                            wpGUI.translations.WAYPOINT_DELETE_CONFIRM_FALSE.getItem(nameMap),
                            wpGUI.translations.WAYPOINT_DELETE_CONFIRM_TRUE.getItem(nameMap),
                        ) {
                            if (it) {
                                waypoint.delete()
                                wpGUI.goBack()
                                wpGUI.goBack()
                            } else {
                                wpGUI.goBack()
                            }
                        }
                    )
                }
            } else {
                background
            },
            't' to if ((wpGUI.viewer.hasPermission(wpGUI.plugin.teleportManager.getTeleportPermission(waypoint)) ||
                        wpGUI.plugin.teleportManager.isTeleportEnabled(wpGUI.targetData, waypoint)) && waypoint.location.world !== null
            ) {
                GUIItem(
                    wpGUI.translations.WAYPOINT_TELEPORT.getItem(
                        mapOf(
                            "paymentNotice" to (wpGUI.plugin.teleportManager.getTeleportCostDescription(wpGUI.viewer, waypoint) ?: ""),
                            "mustVisit" to if (wpGUI.plugin.teleportManager.isAllowedToTeleportToWaypoint(wpGUI.viewer, waypoint)) {
                                ""
                            } else {
                                wpGUI.translations.WAYPOINT_TELEPORT_MUST_VISIT.text
                            }
                        ),
                        true
                    )
                ) {
                    if (wpGUI.plugin.teleportManager.isAllowedToTeleportToWaypoint(wpGUI.viewer, waypoint)) {
                        wpGUI.viewer.closeInventory()
                        wpGUI.plugin.teleportManager.teleportPlayerToWaypoint(wpGUI.viewer, waypoint)
                    }
                }
            } else {
                background
            },
            'b' to GUIItem(wpGUI.translations.GENERAL_BACK.item) {
                wpGUI.goBack()
            },
        )

        if (update) {
            wpGUI.gui.update()
        }
    }

    private fun createChangeCustomMapIconItem(customDataKey: String, defaultIcon: String) = GUIItem(wpGUI.translations.WAYPOINT_CHANGE_MAP_ICON.item) {
        wpGUI.viewer.closeInventory()
        AnvilGUI.Builder().plugin(wpGUI.plugin)
            .text(waypoint.getCustomData(customDataKey) ?: defaultIcon)
            .onComplete { (newIcon) ->
                waypoint.setCustomData(
                    customDataKey, newIcon.ifBlank {
                        null
                    }
                )
                return@onComplete AnvilGUI.ResponseAction.close().asSingletonList()
            }.onClose {
                wpGUI.plugin.runTask {
                    wpGUI.gui.open()
                }
            }.open(wpGUI.viewer)
    }

    init {
        updatePage(false)
    }
}