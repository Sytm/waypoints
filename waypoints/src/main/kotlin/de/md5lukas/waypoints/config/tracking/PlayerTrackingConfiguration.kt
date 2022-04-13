package de.md5lukas.waypoints.config.tracking

import org.bukkit.configuration.ConfigurationSection

class PlayerTrackingConfiguration {

    var enabled = false
        private set

    var toggleable = false
        private set

    var trackingRequiresTrackable = false
        private set

    var notification = false
        private set

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        enabled = cfg.getBoolean("enabled")
        toggleable = cfg.getBoolean("toggleable")
        trackingRequiresTrackable = cfg.getBoolean("trackingRequiresTrackable")
        notification = cfg.getBoolean("notification")
    }
}