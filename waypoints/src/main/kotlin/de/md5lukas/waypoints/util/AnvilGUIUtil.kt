package de.md5lukas.waypoints.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.wesjd.anvilgui.AnvilGUI.StateSnapshot
import net.wesjd.anvilgui.AnvilGUI.ResponseAction
import net.wesjd.anvilgui.AnvilGUI.Slot
import org.bukkit.inventory.ItemStack

operator fun StateSnapshot.component1(): String = outputItem.plainDisplayName

var ItemStack.plainDisplayName: String
    get() = if (hasItemMeta()) {
        val meta = itemMeta
        if (meta.hasDisplayName()) {
            meta.displayName()?.let { PlainTextComponentSerializer.plainText().serialize(it) } ?: ""
        } else ""
    } else ""
    set(value) {
        editMeta {
            it.displayName(Component.text(value))
        }
    }

fun replaceInputText(text: String) = ResponseAction { anvilGUI, _ ->
    anvilGUI.inventory.getItem(Slot.OUTPUT)!!.also {
        it.plainDisplayName = text
        anvilGUI.inventory.setItem(Slot.INPUT_LEFT, it)
    }
}