package de.md5lukas.waypoints.gui.pages

import de.md5lukas.kinvs.GUIPage
import de.md5lukas.kinvs.GUIPattern
import de.md5lukas.kinvs.items.GUIItem
import de.md5lukas.waypoints.WaypointsPermissions
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.gui.WaypointsGUI
import de.md5lukas.waypoints.util.checkMaterialForCustomIcon
import de.md5lukas.waypoints.util.checkWaypointName
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import net.wesjd.anvilgui.AnvilGUI
import java.util.*

class WaypointPage(private val wpGUI: WaypointsGUI, private val waypoint: Waypoint) : GUIPage(wpGUI.gui) {

    private companion object {
        /**
         * w = Waypoint Icon
         * i = Get UUID (Global waypoints only)
         * s = Select
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
            "i___s___c",
            "_f_____r_",
            "d___t___b",
        )
    }

    private val background = GUIItem(
        when (waypoint.type) {
            Type.PRIVATE -> wpGUI.translations.BACKGROUND_PRIVATE
            Type.PUBLIC -> wpGUI.translations.BACKGROUND_PUBLIC
            Type.PERMISSION -> wpGUI.translations.BACKGROUND_PERMISSION
            else -> throw IllegalStateException("An overview of an gui folder of the type ${waypoint.type} is unsupported")
        }.item
    )
    private val isNotDeathWaypoint = waypoint.type != Type.DEATH
    private val isGlobalWaypoint = when (waypoint.type) {
        Type.PUBLIC, Type.PERMISSION -> true
        else -> false
    }

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
            'w' to GUIItem(waypoint.getItem(wpGUI.viewer), if (canModifyWaypoint) {
                {
                    val newMaterial = wpGUI.viewer.inventory.itemInMainHand.type

                    if (checkMaterialForCustomIcon(wpGUI.plugin, newMaterial)) {
                        waypoint.material = newMaterial

                        updatePage()
                    } else {
                        wpGUI.translations.WAYPOINT_NEW_ICON_INVALID.send(wpGUI.viewer)
                    }
                }
            } else null),
            'i' to if (canModifyWaypoint && isGlobalWaypoint) {
                GUIItem(wpGUI.translations.WAYPOINT_GET_UUID.item) {
                    val messageString = wpGUI.translations.WAYPOINT_MESSAGE_GET_UUID.withReplacements(Collections.singletonMap("name", waypoint.name))

                    val clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, waypoint.id.toString())

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
            's' to GUIItem(wpGUI.translations.WAYPOINT_SELECT.item) {
                wpGUI.viewer.closeInventory()
                wpGUI.plugin.pointerManager.enable(wpGUI.viewer, waypoint)
            },
            'c' to if (canModifyWaypoint && isNotDeathWaypoint) {
                GUIItem(wpGUI.translations.WAYPOINT_SELECT_BEACON_COLOR.item) {
                    TODO()
                }
            } else {
                background
            },
            'f' to if (canModifyWaypoint && isNotDeathWaypoint) {
                GUIItem(wpGUI.translations.WAYPOINT_MOVE_TO_FOLDER.item) {
                    TODO()
                }
            } else {
                background
            },
            'r' to if (canModifyWaypoint && isNotDeathWaypoint) {
                GUIItem(wpGUI.translations.WAYPOINT_RENAME.item) {
                    wpGUI.viewer.closeInventory()
                    AnvilGUI.Builder().plugin(wpGUI.plugin).text(waypoint.name).onComplete { _, newName ->
                        val holder = when (waypoint.type) {
                            Type.PRIVATE -> wpGUI.targetData
                            Type.PUBLIC -> wpGUI.plugin.api.publicWaypoints
                            Type.PERMISSION -> wpGUI.plugin.api.permissionWaypoints
                            else -> throw IllegalArgumentException("Waypoints of the type ${waypoint.type} have no name")
                        }

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

                        return@onComplete AnvilGUI.Response.close()
                    }.onClose {
                        wpGUI.gui.open()
                    }.open(wpGUI.viewer)
                }
            } else {
                background
            },
            'd' to if (canModifyWaypoint) {
                GUIItem(wpGUI.translations.WAYPOINT_DELETE.item) {
                    wpGUI.open(
                        ConfirmPage(
                            wpGUI,
                            wpGUI.translations.WAYPOINT_CONFIRM_DELETE_QUESTION.withReplacements(
                                Collections.singletonMap(
                                    "name",
                                    waypoint.name
                                )
                            ),
                            wpGUI.translations.WAYPOINT_CONFIRM_DELETE_FALSE.withReplacements(
                                Collections.singletonMap(
                                    "name",
                                    waypoint.name
                                )
                            ),
                            wpGUI.translations.WAYPOINT_CONFIRM_DELETE_TRUE.withReplacements(
                                Collections.singletonMap(
                                    "name",
                                    waypoint.name
                                )
                            ),
                        ) {
                            if (it) {
                                waypoint.delete()
                                wpGUI.goBack()
                            } else {
                                wpGUI.open(this)
                            }
                        }, WaypointsGUI.OPEN_REMOVE_LAST or WaypointsGUI.OPEN_NO_PUSH
                    )
                }
            } else {
                background
            },
            't' to if (wpGUI.viewer.hasPermission(wpGUI.plugin.teleportManager.getTeleportPermission(waypoint)) ||
                wpGUI.plugin.teleportManager.isTeleportEnabled(waypoint)
            ) {
                GUIItem(wpGUI.translations.WAYPOINT_TELEPORT.item) {
                    wpGUI.plugin.teleportManager.teleportPlayerToWaypoint(wpGUI.viewer, waypoint)
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

    init {
        updatePage(false)
    }
}