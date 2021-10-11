package de.md5lukas.waypoints.gui.pages

import de.md5lukas.kinvs.GUIPage
import de.md5lukas.kinvs.items.GUIContent
import de.md5lukas.kinvs.items.GUIItem
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.gui.GUIDisplayable
import de.md5lukas.waypoints.gui.WaypointsGUI
import org.bukkit.inventory.ItemStack

open class BasePage(
    protected val wpGUI: WaypointsGUI,
    background: ItemStack
) : GUIPage(wpGUI.gui) {

    constructor(wpGUI: WaypointsGUI, guiDisplayable: GUIDisplayable) : this(
        wpGUI,
        when (guiDisplayable.type) {
            Type.PRIVATE -> wpGUI.translations.BACKGROUND_PRIVATE
            Type.DEATH -> wpGUI.translations.BACKGROUND_DEATH
            Type.PUBLIC -> wpGUI.translations.BACKGROUND_PUBLIC
            Type.PERMISSION -> wpGUI.translations.BACKGROUND_PERMISSION
        }.item
    )

    open fun update() {}

    protected val background: GUIContent = GUIItem(background)
}