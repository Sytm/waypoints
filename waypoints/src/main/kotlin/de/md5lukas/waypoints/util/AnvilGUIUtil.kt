package de.md5lukas.waypoints.util

import com.okkero.skedule.SynchronizationContext
import com.okkero.skedule.future
import com.okkero.skedule.switchContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.wesjd.anvilgui.AnvilGUI.*
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

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

inline fun Builder.onClickSuspending(plugin: Plugin, crossinline block: suspend (Int, StateSnapshot) -> List<ResponseAction>): Builder {
    this.onClickAsync { slot, state ->
        plugin.future(state.player) {
            val result = block(slot, state)
            switchContext(SynchronizationContext.SYNC)
            result
        }
    }
    return this
}