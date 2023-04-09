package de.md5lukas.waypoints.util

import com.google.common.math.DoubleMath
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import kotlin.math.roundToInt

inline fun Plugin.runTask(crossinline block: () -> Unit) {
    server.scheduler.runTask(this, Runnable { block() })
}

inline fun Plugin.runTaskAsync(crossinline block: () -> Unit) {
    server.scheduler.runTaskAsynchronously(this, Runnable { block() })
}

fun Plugin.callEvent(event: Event) {
    server.pluginManager.callEvent(event)
}

fun Plugin.registerEvents(listener: Listener) {
    server.pluginManager.registerEvents(listener, this)
}

fun ConfigurationSection.getStringNotNull(path: String): String =
    getString(path) ?: throw IllegalArgumentException("The configuration key ${getFullPath(path)} is not present")

fun ConfigurationSection.getConfigurationSectionNotNull(path: String): ConfigurationSection =
    getConfigurationSection(path) ?: throw IllegalArgumentException("The configuration section ${getFullPath(path)} is not present")

@Suppress("UNCHECKED_CAST")
fun <T> ConfigurationSection.getListNotNull(path: String): List<T> =
    getList(path) as List<T>? ?: throw IllegalArgumentException("The list at ${getFullPath(path)} is not present")

private fun ConfigurationSection.getFullPath(path: String): String =
    if (currentPath!!.isEmpty()) {
        path
    } else {
        "$currentPath.$path"
    }

fun Player.teleportKeepOrientation(location: Location) {
    val target = location.clone()
    target.pitch = this.location.pitch
    target.yaw = this.location.yaw
    this.teleport(target)
}

fun parseLocationString(player: Player, input: String): Location? {
    val parts = input.split("/", " ")

    if (parts.size != 3) {
        return null
    }

    return try {
        Location(player.world, parts[0].toDouble(), parts[1].toDouble(), parts[2].toDouble())
    } catch (_: NumberFormatException) {
        null
    }
}

fun isLocationOutOfBounds(location: Location): Boolean {
    val world = location.world
    if (!world.worldBorder.isInside(location)) {
        return true
    }

    return location.y.roundToInt() !in world.minHeight..world.maxHeight
}

fun Location.fuzzyEquals(other: Location, tolerance: Double) =
    world === other.world && DoubleMath.fuzzyEquals(x, other.x, tolerance)
            && DoubleMath.fuzzyEquals(y, other.y, tolerance) && DoubleMath.fuzzyEquals(x, other.x, tolerance)