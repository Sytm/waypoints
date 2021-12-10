package de.md5lukas.waypoints.config.integrations

import de.md5lukas.waypoints.util.getStringNotNull
import org.bukkit.configuration.ConfigurationSection

class DynMapConfiguration {

    var icon: String = ""
        private set

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        icon = cfg.getStringNotNull("icon")
    }
}