package de.md5lukas.waypoints.config.pointers

import org.bukkit.configuration.ConfigurationSection

class CompassConfiguration {

    var enabled: Boolean = false
        private set

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        with(cfg) {
            enabled = cfg.getBoolean("enabled")
        }
    }
}