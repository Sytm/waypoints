package de.md5lukas.waypoints.config

import de.md5lukas.waypoints.config.database.DatabaseConfigurationImpl
import de.md5lukas.waypoints.config.general.GeneralConfiguration
import de.md5lukas.waypoints.config.integrations.IntegrationsConfiguration
import de.md5lukas.waypoints.config.inventory.InventoryConfiguration
import de.md5lukas.waypoints.config.pointers.PointerConfigurationImpl
import de.md5lukas.waypoints.config.tracking.PlayerTrackingConfiguration
import de.md5lukas.waypoints.util.getConfigurationSectionNotNull
import org.bukkit.configuration.ConfigurationSection

class WaypointsConfiguration() {

    val general = GeneralConfiguration()

    val pointers = PointerConfigurationImpl()

    val inventory = InventoryConfiguration()

    val integrations = IntegrationsConfiguration()

    val playerTracking = PlayerTrackingConfiguration()

    val database = DatabaseConfigurationImpl()

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        general.loadFromConfiguration(cfg.getConfigurationSectionNotNull("general"))
        pointers.loadFromConfiguration(cfg.getConfigurationSectionNotNull("pointers"))
        inventory.loadFromConfiguration(cfg.getConfigurationSectionNotNull("inventory"))
        integrations.loadFromConfiguration(cfg.getConfigurationSectionNotNull("integrations"))
        playerTracking.loadFromConfiguration(cfg.getConfigurationSectionNotNull("playerTracking"))
        database.loadFromConfiguration(cfg.getConfigurationSectionNotNull("database"))
    }
}