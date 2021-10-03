package de.md5lukas.waypoints.config.general

import de.md5lukas.waypoints.util.getStringNotNull
import org.bukkit.configuration.ConfigurationSection

class GeneralConfiguration {

    var language: String = ""
        private set

    val uuidCache = UUIDCacheConfiguration()

    val waypoints = LimitConfiguration()

    val folders = LimitConfiguration()

    val customIconFilter = CustomIconFilterConfiguration()

    val teleport = TeleportConfiguration()

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        language = cfg.getStringNotNull("language")

        uuidCache.loadFromConfiguration(cfg.getConfigurationSection("uuidCache")!!)

        waypoints.loadFromConfiguration(cfg.getConfigurationSection("waypoints")!!)

        folders.loadFromConfiguration(cfg.getConfigurationSection("folders")!!)

        customIconFilter.loadFromConfiguration(cfg.getConfigurationSection("customIconFilter")!!)

        teleport.loadFromConfiguration(cfg.getConfigurationSection("teleport")!!)
    }
}