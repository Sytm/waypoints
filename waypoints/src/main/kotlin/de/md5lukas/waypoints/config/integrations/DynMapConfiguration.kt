package de.md5lukas.waypoints.config.integrations

import de.md5lukas.waypoints.util.getStringNotNull
import org.bukkit.configuration.ConfigurationSection

class DynMapConfiguration {

    var enabled: Boolean = false
        private set

    var icon: String = ""
        private set

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        enabled = cfg.getBoolean("enabled")
        icon = cfg.getStringNotNull("icon")
    }
}