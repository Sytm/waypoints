package de.md5lukas.waypoints.config.general

import de.md5lukas.waypoints.util.getStringNotNull
import org.bukkit.configuration.ConfigurationSection

class AvailableWorldsConfiguration {

    var type: FilterType = FilterType.WHITELIST
        private set

    var worlds: List<String> = listOf()
        private set

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        type = if (cfg.getStringNotNull("type").equals("blacklist", true)) FilterType.BLACKLIST else FilterType.WHITELIST

        worlds = cfg.getStringList("worlds").map { it.lowercase() }
    }
}