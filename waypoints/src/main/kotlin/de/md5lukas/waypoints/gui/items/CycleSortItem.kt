package de.md5lukas.waypoints.gui.items

import de.md5lukas.kinvs.items.GUICycleItem
import de.md5lukas.waypoints.api.OverviewSort
import de.md5lukas.waypoints.gui.WaypointsGUI
import org.bukkit.ChatColor
import org.bukkit.inventory.ItemStack

class CycleSortItem(wpGUI: WaypointsGUI, onCycle: (OverviewSort) -> Unit) : GUICycleItem<OverviewSort>(
    getOverviewSortCycleValues(wpGUI),
    {
        wpGUI.viewerData.sortBy = it
        onCycle(it)
    }
) {

    init {
        while (wpGUI.viewerData.sortBy != currentValue) {
            cycle()
        }
    }

    private companion object {
        fun getOverviewSortCycleValues(wpGUI: WaypointsGUI): List<Pair<OverviewSort, ItemStack>> {
            val result: MutableList<Pair<OverviewSort, ItemStack>> = mutableListOf()

            val builder = StringBuilder()

            OverviewSort.values().forEach { current ->
                wpGUI.translations.OVERVIEW_CYCLE_SORT_OPTIONS.forEachIndexed { index, translation ->
                    if (index > 0) {
                        builder.append('\n')
                    }
                    builder.append(
                        if (translation.first == current) {
                            wpGUI.translations.OVERVIEW_CYCLE_SORT_ACTIVE_COLOR.text
                        } else {
                            wpGUI.translations.OVERVIEW_CYCLE_SORT_INACTIVE_COLOR.text
                        }
                    ).append(translation.second.text).append(ChatColor.RESET)
                }

                val selectionList = builder.toString()
                builder.clear()

                result.add(current to wpGUI.translations.OVERVIEW_CYCLE_SORT.getItem(mapOf("selectionList" to selectionList)))
            }

            return result
        }
    }
}