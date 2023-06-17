package de.md5lukas.waypoints.gui.items

import de.md5lukas.kinvs.items.GUICycleItem
import de.md5lukas.waypoints.gui.WaypointsGUI
import de.md5lukas.waypoints.pointers.PlayerTrackable

class TrackableToggleItem(wpGUI: WaypointsGUI) :
    GUICycleItem<Boolean>(
        listOf(
            true to wpGUI.translations.TRACKING_TRACKABLE_ENABLED.item,
            false to wpGUI.translations.TRACKING_TRACKABLE_DISABLED.item),
        {
          wpGUI.skedule { wpGUI.viewerData.setCanBeTracked(it) }
          if (!it) {
            val pm = wpGUI.plugin.pointerManager
            pm.disableAll { trackable ->
              if (trackable is PlayerTrackable) {
                trackable.player == wpGUI.viewer
              } else false
            }
            if (wpGUI.plugin.waypointsConfig.playerTracking.trackingRequiresTrackable) {
              pm.disable(wpGUI.viewer) { trackable -> trackable is PlayerTrackable }
            }
          }
          wpGUI.gui.update()
          wpGUI.playSound { clickNormal }
        }) {
  init {
    if (wpGUI.viewerData.canBeTracked != currentValue) {
      cycle()
    }
  }
}
