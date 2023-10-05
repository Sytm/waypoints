package de.md5lukas.waypoints.gui.items

import de.md5lukas.kinvs.items.GUICycleItem
import de.md5lukas.waypoints.gui.WaypointsGUI

class ToggleGlobalsItem(wpGUI: WaypointsGUI) :
    GUICycleItem<Boolean>(
        listOf(
            true to wpGUI.translations.SETTINGS_TOGGLE_GLOBALS_VISIBLE.item,
            false to wpGUI.translations.SETTINGS_TOGGLE_GLOBALS_HIDDEN.item),
        {
          wpGUI.skedule { wpGUI.viewerData.setShowGlobals(it) }
          wpGUI.playSound { clickNormal }
          wpGUI.gui.update()
        }) {
  init {
    if (wpGUI.viewerData.showGlobals != currentValue) {
      cycle()
    }
  }
}
