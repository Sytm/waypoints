package de.md5lukas.waypoints.config.general

import de.md5lukas.waypoints.util.getStringNotNull
import org.bukkit.configuration.ConfigurationSection

class GeneralConfiguration {

    var language: String = ""
        private set

    val uuidCacheConfiguration = UUIDCacheConfiguration()

    val waypointCreationConfiguration = WaypointCreationConfiguration()

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        language = cfg.getStringNotNull("language")

        uuidCacheConfiguration.loadFromConfiguration(cfg.getConfigurationSection("uuidCache")!!)

        waypointCreationConfiguration.loadFromConfiguration(cfg.getConfigurationSection("waypointCreation")!!)
    }
}