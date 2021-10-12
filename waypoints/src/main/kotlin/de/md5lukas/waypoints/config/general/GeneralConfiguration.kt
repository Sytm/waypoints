package de.md5lukas.waypoints.config.general

import de.md5lukas.waypoints.util.getConfigurationSectionNotNull
import de.md5lukas.waypoints.util.getStringNotNull
import org.bukkit.configuration.ConfigurationSection

class GeneralConfiguration {

    var language: String = ""
        private set

    val uuidCache = UUIDCacheConfiguration()

    val waypoints = LimitConfiguration()

    val folders = LimitConfiguration()

    val customIconFilter = CustomIconFilterConfiguration()

    val openWithItem = OpenWithItemConfig()

    val teleport = TeleportConfiguration()

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        language = cfg.getStringNotNull("language")

        uuidCache.loadFromConfiguration(cfg.getConfigurationSectionNotNull("uuidCache"))

        waypoints.loadFromConfiguration(cfg.getConfigurationSectionNotNull("waypoints"))

        folders.loadFromConfiguration(cfg.getConfigurationSectionNotNull("folders"))

        customIconFilter.loadFromConfiguration(cfg.getConfigurationSectionNotNull("customIconFilter"))

        openWithItem.loadFromConfiguration(cfg.getConfigurationSectionNotNull("openWithItem"))

        teleport.loadFromConfiguration(cfg.getConfigurationSectionNotNull("teleport"))
    }
}