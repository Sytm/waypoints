package de.md5lukas.waypoints.config.pointers

import org.bukkit.configuration.ConfigurationSection

class HologramConfiguration {

    var enabled = false
        private set

    var interval: Int = 0
        private set

    var distanceFromPlayer: Int = 0
        private set

    val distanceFromPlayerSquared: Int
        get() = distanceFromPlayer * distanceFromPlayer

    var preventOcclusion: Boolean = false
        private set

    var hologramHeightOffset: Double = 0.0
        private set

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        enabled = cfg.getBoolean("enabled")

        interval = cfg.getInt("interval")

        distanceFromPlayer = cfg.getInt("distanceFromPlayer")

        preventOcclusion = cfg.getBoolean("preventOcclusion")

        hologramHeightOffset = cfg.getDouble("hologramHeightOffset")
    }
}