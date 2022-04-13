package de.md5lukas.waypoints.gui.pages

import de.md5lukas.kinvs.GUIPage
import de.md5lukas.kinvs.items.GUIContent
import de.md5lukas.kinvs.items.GUIItem
import de.md5lukas.waypoints.gui.WaypointsGUI
import org.bukkit.inventory.ItemStack

open class BasePage(
    protected val wpGUI: WaypointsGUI,
    background: ItemStack
) : GUIPage(wpGUI.gui) {

    open fun update() {}

    protected val background: GUIContent = GUIItem(background)
}