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
import de.md5lukas.waypoints.util.Formatters
import de.md5lukas.waypoints.util.format
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

class APIExtensions(
    private val plugin: WaypointsPlugin
) {
    private val translations = plugin.translations
    private val worldTranslations = plugin.worldTranslations

    fun GUIDisplayable.getItem(player: Player) = when (this) {
        is GUIFolder -> getItem(player)
        is Waypoint -> getItem(player)
        else -> throw IllegalStateException("Unknown GUIDisplayable subclass")
    }

    fun Waypoint.getItem(player: Player): ItemStack {
        val stack = when (type) {
            Type.DEATH -> translations.WAYPOINT_ICON_DEATH
            Type.PRIVATE -> translations.WAYPOINT_ICON_PRIVATE
            Type.PUBLIC -> translations.WAYPOINT_ICON_PUBLIC
            Type.PERMISSION -> translations.WAYPOINT_ICON_PERMISSION
        }.getItem(
            mapOf(
                "name" to name,
                "description" to (description ?: ""),
                "createdAt" to createdAt.format(Formatters.SHORT_DATE_TIME_FORMATTER),
                "world" to worldTranslations.getWorldName(location.world!!),
                "x" to location.x.format(),
                "y" to location.y.format(),
                "z" to location.z.format(),
                "blockX" to location.blockX.toString(),
                "blockY" to location.blockY.toString(),
                "blockZ" to location.blockZ.toString(),
                "distance" to if (player.world == location.world) {
                    MathHelper.distance2D(player.location, location).format()
                } else {
                    translations.TEXT_DISTANCE_OTHER_WORLD.text
                },
            )
        )

        material?.also {
            stack.type = it
        }

        return stack
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
            Type.PUBLIC -> translations.ICON_PUBLIC.getItem(Collections.singletonMap("amount", amountVisibleToPlayer.toString()))
            Type.PERMISSION -> translations.ICON_PERMISSION.getItem(Collections.singletonMap("amount", amountVisibleToPlayer.toString()))
            else -> throw IllegalStateException("A waypoint holder for a player cannot be a GUI item")
        }

        itemStack.amount = MathHelper.clamp(1, 64, amountVisibleToPlayer)

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
            mapOf(
                "name" to name,
                "description" to (description ?: ""),
                "createdAt" to createdAt.format(Formatters.SHORT_DATE_TIME_FORMATTER),
                "amount" to fetchedAmount.toString()
            )
        )

        stack.amount = MathHelper.clamp(1, 64, fetchedAmount)

        material?.also {
            stack.type = it
        }

        return stack
    }

    private fun Folder.getItemDeath(): ItemStack {
        val fetchedAmount = amount
        val stack = translations.FOLDER_ICON_DEATH.getItem(Collections.singletonMap("amount", fetchedAmount.toString()))

        stack.amount = MathHelper.clamp(1, 64, fetchedAmount)

        return stack
    }
}