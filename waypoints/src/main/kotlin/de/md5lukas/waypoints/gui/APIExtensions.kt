package de.md5lukas.waypoints.gui

import de.md5lukas.commons.MathHelper
import de.md5lukas.waypoints.WaypointsPermissions
import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Folder
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.WaypointHolder
import de.md5lukas.waypoints.api.gui.GUIDisplayable
import de.md5lukas.waypoints.api.gui.GUIFolder
import de.md5lukas.waypoints.util.placeholder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class APIExtensions(
    private val plugin: WaypointsPlugin
) {
    private val translations = plugin.translations
    private val worldTranslations = plugin.worldTranslations

    fun GUIDisplayable.getItem(player: Player) = when (this) {
        is GUIFolder -> getItem(player)
        is Waypoint -> getItem(player)
        is PlayerTrackingDisplayable -> translations.ICON_TRACKING.item
        else -> throw IllegalStateException("Unknown GUIDisplayable subclass")
    }

    fun Waypoint.getItem(player: Player): ItemStack {
        val stack = when (type) {
            Type.DEATH -> translations.WAYPOINT_ICON_DEATH
            Type.PRIVATE -> translations.WAYPOINT_ICON_PRIVATE
            Type.PUBLIC -> translations.WAYPOINT_ICON_PUBLIC
            Type.PERMISSION -> translations.WAYPOINT_ICON_PERMISSION
        }.getItem(*getResolvers(player))

        material?.also {
            stack.type = it
        }

        return stack
    }

    fun Waypoint.getResolvers(player: Player?) = arrayOf(
        "name" placeholder name,
        "description" placeholder (description ?: ""),
        "created_at" placeholder createdAt,
        "world" placeholder (location.world?.let { worldTranslations.getWorldName(it) } ?: translations.TEXT_WORLD_NOT_FOUND.text),
        "x" placeholder location.x,
        "y" placeholder location.y,
        "z" placeholder location.z,
        "block_x" placeholder location.blockX,
        "block_y" placeholder location.blockY,
        "block_z" placeholder location.blockZ,
        if (player !== null && player.world === location.world) {
            "distance" placeholder player.location.distance(location)
        } else {
            "distance" placeholder translations.TEXT_DISTANCE_OTHER_WORLD.text
        },
    )

    fun Waypoint.getIconMaterial(): Material = material ?: when (type) {
        Type.DEATH -> translations.WAYPOINT_ICON_DEATH
        Type.PRIVATE -> translations.WAYPOINT_ICON_PRIVATE
        Type.PUBLIC -> translations.WAYPOINT_ICON_PUBLIC
        Type.PERMISSION -> translations.WAYPOINT_ICON_PERMISSION
    }.material

    fun Waypoint.getHologramTranslations() = when (type) {
        Type.PRIVATE -> plugin.translations.POINTERS_HOLOGRAM_PRIVATE
        Type.DEATH -> plugin.translations.POINTERS_HOLOGRAM_DEATH
        Type.PUBLIC -> plugin.translations.POINTERS_HOLOGRAM_PUBLIC
        Type.PERMISSION -> plugin.translations.POINTERS_HOLOGRAM_PERMISSION
    }

    fun GUIFolder.getItem(player: Player) = when (this) {
        is WaypointHolder -> getItem(player)
        is Folder -> getItem(player)
        else -> throw IllegalStateException("Unknown GUIFolder subclass")
    }

    fun WaypointHolder.getItem(player: Player): ItemStack {
        val amountVisibleToPlayer = if (player.hasPermission(WaypointsPermissions.MODIFY_PERMISSION)) {
            waypointsAmount
        } else {
            getWaypointsVisibleForPlayer(player)
        }

        val itemStack = when (type) {
            Type.PUBLIC -> translations.ICON_PUBLIC.getItem("amount" placeholder amountVisibleToPlayer)
            Type.PERMISSION -> translations.ICON_PERMISSION.getItem("amount" placeholder amountVisibleToPlayer)
            else -> throw IllegalStateException("A waypoint holder for a player cannot be a GUI item")
        }

        itemStack.amount = if (plugin.waypointsConfig.inventory.disableFolderSizes) {
            1
        } else {
            MathHelper.clamp(1, 64, amountVisibleToPlayer)
        }

        return itemStack
    }

    fun Folder.getItem(player: Player): ItemStack {
        if (type === Type.DEATH) {
            return getItemDeath()
        }

        val fetchedAmount = if (player.hasPermission(WaypointsPermissions.MODIFY_PERMISSION)) {
            amount
        } else {
            getAmountVisibleForPlayer(player)
        }

        val stack = when (type) {
            Type.PRIVATE -> translations.FOLDER_ICON_PRIVATE
            Type.PUBLIC -> translations.FOLDER_ICON_PUBLIC
            Type.PERMISSION -> translations.FOLDER_ICON_PERMISSION
            else -> throw IllegalStateException("An folder with the type $type should not exist")
        }.getItem(
            "name" placeholder name,
            "description" placeholder (description ?: ""),
            "created_at" placeholder createdAt,
            "amount" placeholder fetchedAmount
        )

        stack.amount = if (plugin.waypointsConfig.inventory.disableFolderSizes) {
            1
        } else {
            MathHelper.clamp(1, 64, fetchedAmount)
        }

        material?.also {
            stack.type = it
        }

        return stack
    }

    fun Type.getBackgroundItem() = when (this) {
        Type.PRIVATE -> plugin.translations.BACKGROUND_PRIVATE
        Type.DEATH -> plugin.translations.BACKGROUND_DEATH
        Type.PUBLIC -> plugin.translations.BACKGROUND_PUBLIC
        Type.PERMISSION -> plugin.translations.BACKGROUND_PERMISSION
    }.item

    private fun Folder.getItemDeath(): ItemStack {
        val fetchedAmount = amount
        val stack = translations.FOLDER_ICON_DEATH.getItem("amount" placeholder fetchedAmount)

        stack.amount = MathHelper.clamp(1, 64, fetchedAmount)

        return stack
    }
}