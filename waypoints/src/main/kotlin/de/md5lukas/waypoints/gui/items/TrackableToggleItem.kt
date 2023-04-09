package de.md5lukas.waypoints.gui.items

import de.md5lukas.kinvs.items.GUICycleItem
import de.md5lukas.waypoints.gui.WaypointsGUI
import de.md5lukas.waypoints.pointers.PlayerTrackable

class TrackableToggleItem(wpGUI: WaypointsGUI) : GUICycleItem<Boolean>(
    listOf(
        true to wpGUI.translations.TRACKING_TRACKABLE_ENABLED.item,
        false to wpGUI.translations.TRACKING_TRACKABLE_DISABLED.item
    ), {
        wpGUI.viewerData.canBeTracked = it
        if (!it) {
            val pm = wpGUI.plugin.pointerManager
            pm.disableAll(wpGUI.viewer.uniqueId)
            if (wpGUI.plugin.waypointsConfig.playerTracking.trackingRequiresTrackable && pm.getCurrentTarget(wpGUI.viewer) is PlayerTrackable) {
                pm.disable(wpGUI.viewer)
            }
        }
        wpGUI.gui.update()
    }) {
    init {
        if (wpGUI.viewerData.canBeTracked != currentValue) {
            cycle()
        }
    }
}