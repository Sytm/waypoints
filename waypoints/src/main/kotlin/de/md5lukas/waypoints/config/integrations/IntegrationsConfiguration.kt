package de.md5lukas.waypoints.config.integrations

import de.md5lukas.waypoints.util.getConfigurationSectionNotNull
import org.bukkit.configuration.ConfigurationSection

class IntegrationsConfiguration {

    val dynmap = DynMapConfiguration()
    val squaremap = SquareMapConfiguration()
    val bluemap = BlueMapConfiguration()

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        dynmap.loadFromConfiguration(cfg.getConfigurationSectionNotNull("dynmap"))
        squaremap.loadFromConfiguration(cfg.getConfigurationSectionNotNull("squaremap"))
        bluemap.loadFromConfiguration(cfg.getConfigurationSectionNotNull("bluemap"))
    }
}