package de.md5lukas.waypoints.config

import de.md5lukas.waypoints.config.pointers.PointerConfiguration
import org.bukkit.configuration.ConfigurationSection

class WaypointsConfig {

    val pointerConfiguration = PointerConfiguration()

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        pointerConfiguration.loadFromConfiguration(cfg)
    }
}