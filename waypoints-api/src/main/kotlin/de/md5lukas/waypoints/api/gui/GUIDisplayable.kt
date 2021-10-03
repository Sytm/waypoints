package de.md5lukas.waypoints.api.gui

import de.md5lukas.waypoints.api.Type
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface GUIDisplayable {

    val type: Type
    val guiType: GUIType
    val name: String
    val createdAt: Long

    fun getItem(player: Player): ItemStack
}