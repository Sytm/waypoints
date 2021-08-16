package de.md5lukas.waypoints.config.pointers

import de.md5lukas.waypoints.db.CompassStorage
import org.bukkit.configuration.ConfigurationSection

class CompassConfiguration {

    var enabled: Boolean = false
        private set

    lateinit var compassStorage: CompassStorage

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        with(cfg) {
            enabled = cfg.getBoolean("enabled")
        }
    }
}