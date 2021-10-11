package de.md5lukas.waypoints.gui.pages

import de.md5lukas.kinvs.GUIPattern
import de.md5lukas.kinvs.items.GUIItem
import de.md5lukas.waypoints.WaypointsPermissions
import de.md5lukas.waypoints.api.Folder
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.WaypointHolder
import de.md5lukas.waypoints.api.WaypointsPlayer
import de.md5lukas.waypoints.api.gui.GUIDisplayable
import de.md5lukas.waypoints.api.gui.GUIFolder
import de.md5lukas.waypoints.gui.WaypointsGUI
import de.md5lukas.waypoints.gui.items.CycleSortItem
import de.md5lukas.waypoints.gui.items.ToggleGlobalsItem
import de.md5lukas.waypoints.util.checkFolderName
import de.md5lukas.waypoints.util.checkMaterialForCustomIcon
import net.wesjd.anvilgui.AnvilGUI
import java.util.*

class GUIFolderPage(wpGUI: WaypointsGUI, private val guiFolder: GUIFolder) : ListingPage<GUIDisplayable>(wpGUI, guiFolder, {
    wpGUI.getListingContent(guiFolder)
}, wpGUI::toGUIContent) {

    private companion object {
        /**
         * Overview / Folder
         * p = Previous
         * f = Create Folder / Delete Folder
         * s = Cycle Sort
         * d = Deselect active waypoint / None
         * i = None / Folder Icon
         * t = Toggle Globals / Rename
         * w = Create Waypoint / Create waypoint in folder
         * b = None / Back
         * n = Next
         */
        val controlsPattern = GUIPattern("pfsditwbn")
    }

    private val isOverview = guiFolder is WaypointHolder
    private val isPlayerOverview = guiFolder is WaypointsPlayer

    private val canModify = when (guiFolder.type) {
        Type.PRIVATE -> wpGUI.isOwner && wpGUI.viewer.hasPermission(WaypointsPermissions.MODIFY_PRIVATE)
        Type.PUBLIC -> wpGUI.viewer.hasPermission(WaypointsPermissions.MODIFY_PUBLIC)
        Type.PERMISSION -> wpGUI.viewer.hasPermission(WaypointsPermissions.MODIFY_PERMISSION)
        else -> throw IllegalStateException("There cannot be gui folders of type ${guiFolder.type}")
    }

    override fun update() {
        updateListingContent()
        updateControls()
    }

    private fun updateControls(update: Boolean = true) {
        /**
         * Overview / Folder
         * p = Previous
         * f = Create Folder / Delete Folder
         * s = Cycle Sort
         * d = Deselect active waypoint / None
         * i = None / Folder Icon
         * t = Toggle Globals / Rename
         * w = Create Waypoint / Create waypoint in folder
         * b = None / Back
         * n = Next
         */
        applyPattern(
            controlsPattern,
            4,
            0,
            background,
            'p' to GUIItem(wpGUI.translations.GENERAL_PREVIOUS.item) {
                previousPage()
            },
            'f' to if (canModify) {
                if (isOverview) {
                    GUIItem(wpGUI.translations.OVERVIEW_CREATE_FOLDER.item) {
                        wpGUI.openCreateFolder(guiFolder as WaypointHolder)
                    }
                } else {
                    GUIItem(wpGUI.translations.FOLDER_DELETE.item) {
                        wpGUI.open(
                            ConfirmPage(
                                wpGUI,
                                wpGUI.translations.FOLDER_CONFIRM_DELETE_QUESTION.getItem(
                                    Collections.singletonMap(
                                        "name",
                                        guiFolder.name
                                    )
                                ),
                                wpGUI.translations.FOLDER_CONFIRM_DELETE_FALSE.getItem(
                                    Collections.singletonMap(
                                        "name",
                                        guiFolder.name
                                    )
                                ),
                                wpGUI.translations.FOLDER_CONFIRM_DELETE_TRUE.getItem(
                                    Collections.singletonMap(
                                        "name",
                                        guiFolder.name
                                    )
                                ),
                            ) {
                                if (it) {
                                    (guiFolder as Folder).delete()
                                    wpGUI.goBack()
                                    wpGUI.goBack()
                                } else {
                                    wpGUI.goBack()
                                }
                            }
                        )
                    }
                }
            } else {
                background
            },
            's' to CycleSortItem(wpGUI) {
                listingContent.sortWith(it)
                updateListingInInventory()
            },
            'd' to if (isOverview) {
                GUIItem(wpGUI.translations.OVERVIEW_DESELECT.item) {
                    wpGUI.plugin.pointerManager.disable(wpGUI.viewer)
                }
            } else {
                background
            },
            'i' to if (isOverview) {
                background
            } else {
                GUIItem(
                    (guiFolder as Folder).getItem(wpGUI.viewer)
                ) {
                    val newMaterial = wpGUI.viewer.inventory.itemInMainHand.type

                    if (checkMaterialForCustomIcon(wpGUI.plugin, newMaterial)) {
                        guiFolder.material = newMaterial

                        updateControls()
                    } else {
                        wpGUI.translations.FOLDER_NEW_ICON_INVALID.send(wpGUI.viewer)
                    }
                }
            },
            't' to if (wpGUI.isOwner && isPlayerOverview) {
                ToggleGlobalsItem(wpGUI) {
                    listingContent = wpGUI.getListingContent(guiFolder)
                    checkListingPageBounds()
                    updateListingInInventory()
                }
            } else {
                if (guiFolder is Folder && canModify) {
                    GUIItem(wpGUI.translations.FOLDER_RENAME.item) {
                        wpGUI.viewer.closeInventory()
                        AnvilGUI.Builder().plugin(wpGUI.plugin).text(guiFolder.name).onComplete { _, newName ->
                            val holder = wpGUI.getHolderForType(guiFolder.type)

                            if (checkFolderName(wpGUI.plugin, holder, newName)) {
                                guiFolder.name = newName

                                updateControls()
                            } else {
                                when (guiFolder.type) {
                                    Type.PRIVATE -> wpGUI.translations.FOLDER_NAME_DUPLICATE_PRIVATE
                                    Type.PUBLIC -> wpGUI.translations.FOLDER_NAME_DUPLICATE_PUBLIC
                                    Type.PERMISSION -> wpGUI.translations.FOLDER_NAME_DUPLICATE_PERMISSION
                                    else -> throw IllegalArgumentException("Folders of the type ${guiFolder.type} have no name")
                                }.send(wpGUI.viewer)
                            }

                            return@onComplete AnvilGUI.Response.close()
                        }.onClose {
                            wpGUI.gui.open()
                        }.open(wpGUI.viewer)
                    }
                } else {
                    background
                }
            },
            'w' to if (canModify) {
                GUIItem(wpGUI.translations.OVERVIEW_SET_WAYPOINT.item) {
                    wpGUI.openCreateWaypoint(guiFolder.type, if (guiFolder is Folder) guiFolder else null)
                }
            } else {
                background
            },
            'b' to if (isPlayerOverview) {
                background
            } else {
                GUIItem(wpGUI.translations.GENERAL_BACK.item) {
                    wpGUI.goBack()
                }
            },
            'n' to GUIItem(wpGUI.translations.GENERAL_NEXT.item) {
                nextPage()
            },
        )

        if (update) {
            wpGUI.gui.update()
        }
    }

    init {
        updateListingInInventory()
        updateControls(false)
    }
}