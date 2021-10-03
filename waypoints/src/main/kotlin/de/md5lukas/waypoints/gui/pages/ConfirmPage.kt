package de.md5lukas.waypoints.gui.pages

import de.md5lukas.kinvs.GUIPage
import de.md5lukas.kinvs.GUIPattern
import de.md5lukas.kinvs.items.GUIItem
import de.md5lukas.waypoints.gui.WaypointsGUI
import java.util.*

class ConfirmPage(wpGUI: WaypointsGUI, question: String, falseText: String, trueText: String, onClick: (Boolean) -> Unit) :
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
            'q' to GUIItem(wpGUI.translations.CONFIRM_QUESTION.getItem(Collections.singletonMap("text", question))),
            'f' to GUIItem(wpGUI.translations.CONFIRM_TRUE.getItem(Collections.singletonMap("text", falseText))) {
                onClick(false)
            },
            't' to GUIItem(wpGUI.translations.CONFIRM_FALSE.getItem(Collections.singletonMap("text", trueText))) {
                onClick(true)
            },
        )
    }
}