package de.md5lukas.waypoints.config.integrations

import org.bukkit.configuration.ConfigurationSection

class BlueMapConfiguration {

    var enabled: Boolean = false
        private set

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        enabled = cfg.getBoolean("enabled")
    }
}