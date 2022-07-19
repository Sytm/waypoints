package de.md5lukas.waypoints.config.general

import de.md5lukas.waypoints.util.getConfigurationSectionNotNull
import de.md5lukas.waypoints.util.getListNotNull
import de.md5lukas.waypoints.util.getStringNotNull
import org.bukkit.configuration.ConfigurationSection

class GeneralConfiguration {

    var language: String = ""
        private set

    var worldNotFound: WorldNotFoundAction = WorldNotFoundAction.SHOW
        private set

        val uuidCache = UUIDCacheConfiguration()

    val features = FeaturesConfiguration()

    val waypoints = LimitConfiguration()

    val folders = LimitConfiguration()

    val customIconFilter = CustomIconFilterConfiguration()

    val openWithItem = OpenWithItemConfiguration()

    val teleport = TeleportConfiguration()

    val connectedWorlds = ConnectedWorldsConfiguration()

    val pointToDeathWaypointOnDeath = PointToDeathWaypointOnDeathConfiguration()

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        language = cfg.getStringNotNull("language")

        worldNotFound = WorldNotFoundAction.valueOf(cfg.getStringNotNull("worldNotFound"))

        uuidCache.loadFromConfiguration(cfg.getConfigurationSectionNotNull("uuidCache"))

        features.loadFromConfiguration(cfg.getConfigurationSectionNotNull("features"))

        waypoints.loadFromConfiguration(cfg.getConfigurationSectionNotNull("waypoints"))

        folders.loadFromConfiguration(cfg.getConfigurationSectionNotNull("folders"))

        customIconFilter.loadFromConfiguration(cfg.getConfigurationSectionNotNull("customIconFilter"))

        openWithItem.loadFromConfiguration(cfg.getConfigurationSectionNotNull("openWithItem"))

        teleport.loadFromConfiguration(cfg.getConfigurationSectionNotNull("teleport"))

        connectedWorlds.loadFromConfiguration(cfg.getListNotNull("connectedWorlds"))

        pointToDeathWaypointOnDeath.loadFromConfiguration(cfg.getConfigurationSectionNotNull("pointToDeathWaypointOnDeath"))
    }
}