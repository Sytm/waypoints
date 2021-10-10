package de.md5lukas.waypoints.gui.pages

import de.md5lukas.kinvs.GUIPage
import de.md5lukas.kinvs.GUIPattern
import de.md5lukas.kinvs.items.GUIItem
import de.md5lukas.waypoints.gui.WaypointsGUI
import org.bukkit.inventory.ItemStack
import java.util.*

class ConfirmPage(wpGUI: WaypointsGUI, questionItem: ItemStack, falseItem: ItemStack, trueItem: ItemStack, onClick: (Boolean) -> Unit) :
    GUIPage(wpGUI.gui) {
    private companion object {
        /**
         * q = question
         * f = false
         * t = true
         */
        val confirmPattern = GUIPattern(
            "_________",
            "____q____",
            "_________",
            "_f_____t_",
            "_________",
        )
    }

    init {
        applyPattern(
            confirmPattern,
            0, 0,
            GUIItem(wpGUI.translations.CONFIRM_BACKGROUND.item),
            'q' to GUIItem(questionItem),
            'f' to GUIItem(falseItem) {
                onClick(false)
            },
            't' to GUIItem(trueItem) {
                onClick(true)
            },
        )
    }
}