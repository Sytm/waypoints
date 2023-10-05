package de.md5lukas.waypoints.gui.items

import de.md5lukas.commons.paper.appendLore
import de.md5lukas.commons.paper.placeholder
import de.md5lukas.kinvs.items.GUICycleItem
import de.md5lukas.waypoints.gui.WaypointsGUI
import de.md5lukas.waypoints.pointers.variants.PointerVariant

class TogglePointerItem(wpGUI: WaypointsGUI, variant: PointerVariant, onChange: () -> Unit) :
    GUICycleItem<Boolean>(
        run {
          val name = "name" placeholder wpGUI.translations.SETTINGS_POINTERS_NAMES[variant]!!.text
          val description = wpGUI.translations.SETTINGS_POINTERS_DESCRIPTIONS[variant]!!.text
          listOf(
              true to
                  wpGUI.translations.SETTINGS_POINTERS_ON.getItem(name).also {
                    it.appendLore(description)
                  },
              false to
                  wpGUI.translations.SETTINGS_POINTERS_OFF.getItem(name).also {
                    it.appendLore(description)
                  },
          )
        },
        {
          wpGUI.skedule { wpGUI.viewerData.setPointerEnabled(variant.key, it) }
          onChange()
          wpGUI.playSound { clickNormal }
          wpGUI.gui.update()
        }) {
  init {
    if (wpGUI.viewerData.isPointerEnabled(variant.key) != currentValue) {
      cycle()
    }
  }
}
