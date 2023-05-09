package de.md5lukas.waypoints.gui.items

import de.md5lukas.kinvs.items.GUICycleItem
import de.md5lukas.waypoints.gui.WaypointsGUI

class ToggleGlobalsItem(wpGUI: WaypointsGUI, onChange: (Boolean) -> Unit) :
    GUICycleItem<Boolean>(
        listOf(
            true to wpGUI.translations.OVERVIEW_TOGGLE_GLOBALS_VISIBLE.item,
            false to wpGUI.translations.OVERVIEW_TOGGLE_GLOBALS_HIDDEN.item),
        {
          wpGUI.skedule { wpGUI.viewerData.setShowGlobals(it) }
          onChange(it)
        }) {
  init {
    if (wpGUI.viewerData.showGlobals != currentValue) {
      cycle()
    }
  }
}
