package de.md5lukas.waypoints.util

import de.md5lukas.waypoints.WaypointsPlugin
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.Plugin
import org.bukkit.util.Vector

fun Plugin.runTask(block: () -> Unit) {
    server.scheduler.runTask(this, block)
}

fun Plugin.runTaskAsync(block: () -> Unit) {
    server.scheduler.runTaskAsynchronously(this, block)
}

fun Plugin.callEvent(event: Event) {
    server.pluginManager.callEvent(event)
}

fun ConfigurationSection.getStringNotNull(path: String): String =
    getString(path) ?: throw IllegalArgumentException("The configuration key ${getFullPath(path)} is not present")

fun ConfigurationSection.getConfigurationSectionNotNull(path: String): ConfigurationSection =
    getConfigurationSection(path) ?: throw IllegalArgumentException("The configuration section ${getFullPath(path)} is not present")

private fun ConfigurationSection.getFullPath(path: String): String =
    if (currentPath!!.isEmpty()) {
        path
    } else {
        "$currentPath.$path"
    }

fun Vector.divide(d: Int): Vector {
    this.x /= d
    this.y /= d
    this.z /= d

    return this
}

fun Location.blockEquals(other: Location): Boolean =
    this.world == other.world && this.blockX == other.blockX && this.blockY == other.blockY && this.blockZ == other.blockZ


fun Location.getHighestBlock(): Block = world!!.getHighestBlockAt(this)

fun Player.sendActualBlock(location: Location) = this.sendBlockChange(location, location.block.blockData)

fun Player.teleportKeepOrientation(location: Location) {
    val target = location.clone()
    target.pitch = this.location.pitch
    target.yaw = this.location.yaw
    this.teleport(target)
}

inline fun ItemStack.itemMeta(meta: ItemMeta.() -> Unit) {
    itemMeta = itemMeta!!.also { it.meta() }
}

fun parseLocationString(plugin: WaypointsPlugin, player: Player, input: String): Location? {
    val parts = input.split("/")

    if (parts.size != 3) {
        plugin.translations.WAYPOINT_CREATE_COORDINATES_INVALID_FORMAT.send(player)
        return null
    }

    return try {
        Location(player.world, parts[0].toDouble(), parts[1].toDouble(), parts[2].toDouble())
    } catch (_: NumberFormatException) {
        plugin.translations.WAYPOINT_CREATE_COORDINATES_INVALID_FORMAT.send(player)
        null
    }
}