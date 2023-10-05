package de.md5lukas.waypoints.gui.items

import de.md5lukas.commons.paper.placeholder
import de.md5lukas.kinvs.items.GUICycleItem
import de.md5lukas.waypoints.gui.WaypointsGUI

class ToggleTemporaryWaypointsItem(wpGUI: WaypointsGUI) :
    GUICycleItem<Boolean>(
        listOf(
            true to
                wpGUI.translations.SETTINGS_TEMPORARY_WAYPOINTS_RECEIVABLE.getItem(
                    "name" placeholder wpGUI.viewer.name,
                ),
            false to
                wpGUI.translations.SETTINGS_TEMPORARY_WAYPOINTS_BLOCKED.getItem(
                    "name" placeholder wpGUI.viewer.name,
                )),
        {
          wpGUI.skedule { wpGUI.viewerData.setCanReceiveTemporaryWaypoints(it) }
          wpGUI.playSound { clickNormal }
          wpGUI.gui.update()
        }) {
  init {
    if (wpGUI.viewerData.canReceiveTemporaryWaypoints != currentValue) {
      cycle()
    }
  }
}
