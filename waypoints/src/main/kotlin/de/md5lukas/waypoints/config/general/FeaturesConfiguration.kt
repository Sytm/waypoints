package de.md5lukas.waypoints.config.general

import org.bukkit.configuration.ConfigurationSection

class FeaturesConfiguration {

    var globalWaypoints = true
        private set

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        globalWaypoints = cfg.getBoolean("globalWaypoints")
    }
}