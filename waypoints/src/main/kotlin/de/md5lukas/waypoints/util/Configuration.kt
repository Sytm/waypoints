package de.md5lukas.waypoints.util

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection

fun ConfigurationSection.getStringNotNull(path: String): String =
    getString(path) ?: throw IllegalArgumentException("The configuration key $path is not present")

fun ConfigurationSection.getLocation(path: String): Location? {
    return Location(
        Bukkit.getWorld(getString("$path.world") ?: return null) ?: return null,
        getDouble("$path.x"),
        getDouble("$path.y"),
        getDouble("$path.z")
    )
}
