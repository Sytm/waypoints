package de.md5lukas.waypoints.util

import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.util.Vector

fun Plugin.runTask(block: () -> Unit) {
    server.scheduler.runTask(this, block)
}

fun Plugin.runTaskAsync(block: () -> Unit) {
    server.scheduler.runTaskAsynchronously(this, block)
}

fun ConfigurationSection.getStringNotNull(path: String): String =
    getString(path) ?: throw IllegalArgumentException("The configuration key $path is not present")

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