package de.md5lukas.waypoints.config.pointers

import org.bukkit.configuration.ConfigurationSection

class HologramConfiguration {

    var enabled = false
        private set

    var interval = 0
        private set

    var maxDistance = 0
        private set(value) {
            if (value <= 0) {
                throw IllegalArgumentException("The maxDistance must be greater than zero ($value)")
            }
            field = value * value
        }

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        enabled = cfg.getBoolean("enabled")

        interval = cfg.getInt("interval")

        maxDistance = cfg.getInt("maxDistance")
    }
}