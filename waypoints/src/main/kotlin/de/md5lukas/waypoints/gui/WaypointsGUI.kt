package de.md5lukas.waypoints.gui

import de.md5lukas.waypoints.WaypointsPlugin
import org.bukkit.entity.Player
import java.util.*

class WaypointsGUI(
    private val plugin: WaypointsPlugin
) {

    fun openOverview(player: Player, target: UUID) {
        val isOwner = player.uniqueId == target

    }
}