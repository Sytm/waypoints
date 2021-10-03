package de.md5lukas.waypoints.gui.pages

import de.md5lukas.commons.collections.PaginationList
import de.md5lukas.kinvs.GUIPage
import de.md5lukas.kinvs.GUIPattern
import de.md5lukas.kinvs.items.GUIContent
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

class GUIFolderPage(private val wpGUI: WaypointsGUI, private val guiFolder: GUIFolder) : GUIPage(wpGUI.gui) {

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
        val controlsPattern = GUIPattern("pfsditwb")
    }

    private val background = GUIItem(
        when (guiFolder.type) {
            Type.PRIVATE -> wpGUI.translations.BACKGROUND_PRIVATE
            Type.PUBLIC -> wpGUI.translations.BACKGROUND_PUBLIC
            Type.PERMISSION -> wpGUI.translations.BACKGROUND_PERMISSION
            else -> throw IllegalStateException("An overview of an gui folder of the type ${guiFolder.type} is unsupported")
        }.item
    )

    private var listingContent: PaginationList<GUIDisplayable> = wpGUI.getListingContent(guiFolder)
    private var listingPage = 0

    private fun checkListingPageBounds() {
        if (listingPage < 0) {
            listingPage = 0
        } else if (listingPage >= listingContent.pages()) {
            listingPage = listingContent.pages() - 1
        }
    }

    private fun isValidListingPage(page: Int) = page >= 0 && page < listingContent.pages()


    private val isOverview = guiFolder is WaypointHolder
    private val isPlayerOverview = guiFolder is WaypointsPlayer

    private val canCreateWaypoint = if (isPlayerOverview) {
        if (wpGUI.isOwner) {
            wpGUI.viewer.hasPermission(WaypointsPermissions.MODIFY_PRIVATE)
        } else {
            false
        }
    } else {
        when (guiFolder.type) {
            Type.PUBLIC -> wpGUI.viewer.hasPermission(WaypointsPermissions.MODIFY_PUBLIC)
            Type.PERMISSION -> wpGUI.viewer.hasPermission(WaypointsPermissions.MODIFY_PERMISSION)
            else -> throw IllegalStateException()
        }
    }
    private val canModifyFolder = if (isPlayerOverview) {
        if (wpGUI.isOwner) {
            wpGUI.viewer.hasPermission(WaypointsPermissions.MODIFY_FOLDER_PRIVATE)
        } else {
            false
        }
    } else {
        when (guiFolder.type) {
            Type.PUBLIC -> wpGUI.viewer.hasPermission(WaypointsPermissions.MODIFY_FOLDER_PUBLIC)
            Type.PERMISSION -> wpGUI.viewer.hasPermission(WaypointsPermissions.MODIFY_FOLDER_PERMISSION)
            else -> throw IllegalStateException()
        }
    }


    private fun updateListingInInventory() {
        val pageContent = listingContent.page(listingPage)
        for (row in 0..4) {
            for (column in 0..8) {
                val content = pageContent.getOrNull(row * 8 + column)
                if (content == null) {
                    grid[row][column] = GUIContent.AIR
                } else {
                    grid[row][column] = wpGUI.toGUIContent(content)
                }
            }
        }
        wpGUI.gui.update()
    }

    private fun previousPage() {
        if (!isValidListingPage(listingPage - 1))
            return
        listingPage--
        updateListingInInventory()
    }

    private fun nextPage() {
        if (!isValidListingPage(listingPage + 1))
            return
        listingPage++
        updateListingInInventory()
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
            5,
            0,
            background,
            'p' to GUIItem(wpGUI.translations.GENERAL_PREVIOUS.item) {
                previousPage()
            },
            'f' to if (canModifyFolder) {
                if (isOverview) {
                    GUIItem(wpGUI.translations.OVERVIEW_CREATE_FOLDER.item) {
                        wpGUI.openCreateFolder(guiFolder as WaypointHolder)
                    }
                } else {
                    GUIItem(wpGUI.translations.FOLDER_DELETE.item) {
                        wpGUI.open(
                            ConfirmPage(
                                wpGUI,
                                wpGUI.translations.FOLDER_CONFIRM_DELETE_QUESTION.withReplacements(
                                    Collections.singletonMap(
                                        "name",
                                        guiFolder.name
                                    )
                                ),
                                wpGUI.translations.FOLDER_CONFIRM_DELETE_FALSE.withReplacements(
                                    Collections.singletonMap(
                                        "name",
                                        guiFolder.name
                                    )
                                ),
                                wpGUI.translations.FOLDER_CONFIRM_DELETE_TRUE.withReplacements(
                                    Collections.singletonMap(
                                        "name",
                                        guiFolder.name
                                    )
                                ),
                            ) {
                                if (it) {
                                    (guiFolder as Folder).delete()
                                    wpGUI.goBack()
                                } else {
                                    wpGUI.open(this)
                                }
                            }, WaypointsGUI.OPEN_REMOVE_LAST or WaypointsGUI.OPEN_NO_PUSH
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
                if (guiFolder is Folder && canModifyFolder) {
                    GUIItem(wpGUI.translations.FOLDER_RENAME.item) {
                        wpGUI.viewer.closeInventory()
                        AnvilGUI.Builder().plugin(wpGUI.plugin).text(guiFolder.name).onComplete { _, newName ->
                            val holder = when (guiFolder.type) {
                                Type.PRIVATE -> wpGUI.targetData
                                Type.PUBLIC -> wpGUI.plugin.api.publicWaypoints
                                Type.PERMISSION -> wpGUI.plugin.api.permissionWaypoints
                                else -> throw IllegalArgumentException("Folders of the type ${guiFolder.type} have no name")
                            }

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
            'w' to if (canCreateWaypoint) {
                GUIItem(wpGUI.translations.OVERVIEW_SET_WAYPOINT.item) {
                    wpGUI.openCreateWaypoint(wpGUI.targetData, if (guiFolder is Folder) guiFolder else null)
                }
            } else {
                background
            },
            'b' to if (isOverview) {
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
        updateControls(false)
    }
}