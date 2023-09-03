package de.md5lukas.waypoints.util

import com.okkero.skedule.future
import de.md5lukas.commons.paper.plainDisplayName
import de.md5lukas.schedulers.AbstractScheduler
import net.wesjd.anvilgui.AnvilGUI.*
import org.bukkit.Material

operator fun StateSnapshot.component1(): Boolean = outputItem.type === Material.AIR

operator fun StateSnapshot.component2(): String = text

fun replaceInputText(text: String) = ResponseAction { anvilGUI, _ ->
  anvilGUI.inventory.getItem(Slot.INPUT_LEFT)!!.let {
    it.plainDisplayName = "_"
    it.plainDisplayName = text
  }
}

inline fun Builder.onClickSuspending(
    scheduler: AbstractScheduler,
    crossinline block: suspend (Int, StateSnapshot) -> List<ResponseAction>
): Builder {
  this.onClickAsync { slot, state -> scheduler.future { block(slot, state) } }
  return this
}
