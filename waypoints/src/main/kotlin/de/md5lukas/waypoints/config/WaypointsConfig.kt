package de.md5lukas.waypoints.config

import de.md5lukas.waypoints.config.general.GeneralConfiguration
import de.md5lukas.waypoints.config.pointers.PointerConfiguration
import org.bukkit.configuration.ConfigurationSection

class WaypointsConfig() {

    val generalConfiguration = GeneralConfiguration()

    val pointerConfiguration = PointerConfiguration()

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        generalConfiguration.loadFromConfiguration(cfg.getConfigurationSection("general")!!)
        pointerConfiguration.loadFromConfiguration(cfg.getConfigurationSection("pointers")!!)
    }
}