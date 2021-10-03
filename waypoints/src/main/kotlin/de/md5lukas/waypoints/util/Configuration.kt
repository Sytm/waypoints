package de.md5lukas.waypoints.util

import org.bukkit.configuration.ConfigurationSection

fun ConfigurationSection.getStringNotNull(path: String): String =
    getString(path) ?: throw IllegalArgumentException("The configuration key $path is not present")
