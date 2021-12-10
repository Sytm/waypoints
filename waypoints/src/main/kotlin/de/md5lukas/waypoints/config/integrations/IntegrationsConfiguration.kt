package de.md5lukas.waypoints.config.integrations

import de.md5lukas.waypoints.util.getConfigurationSectionNotNull
import org.bukkit.configuration.ConfigurationSection

class IntegrationsConfiguration {

    val dynmap = DynMapConfiguration()

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        dynmap.loadFromConfiguration(cfg.getConfigurationSectionNotNull("dynmap"))
    }
}