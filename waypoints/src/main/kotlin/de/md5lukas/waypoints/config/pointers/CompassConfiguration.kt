package de.md5lukas.waypoints.config.pointers

import org.bukkit.configuration.ConfigurationSection

class CompassConfiguration {

    var enabled: Boolean = false
        private set

    var interval: Int = 0
        private set

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        enabled = cfg.getBoolean("enabled")

        interval = cfg.getInt("interval")
    }
}