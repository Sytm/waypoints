package de.md5lukas.waypoints.config.general

import org.bukkit.configuration.ConfigurationSection

class PointToDeathWaypointOnDeathConfiguration {

    var enabled = false
        private set

    var overwriteCurrent = false
        private set

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        enabled = cfg.getBoolean("enabled")
        overwriteCurrent = cfg.getBoolean("overwriteCurrent")
    }
}