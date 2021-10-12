package de.md5lukas.waypoints.api.gui

import de.md5lukas.waypoints.api.Type
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.time.OffsetDateTime

interface GUIDisplayable {

    val type: Type
    val guiType: GUIType
    val name: String
    val createdAt: OffsetDateTime

    fun getItem(player: Player): ItemStack
}