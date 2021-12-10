package de.md5lukas.waypoints.config

import de.md5lukas.waypoints.config.general.GeneralConfiguration
import de.md5lukas.waypoints.config.integrations.IntegrationsConfiguration
import de.md5lukas.waypoints.config.inventory.InventoryConfiguration
import de.md5lukas.waypoints.config.pointers.PointerConfiguration
import de.md5lukas.waypoints.util.getConfigurationSectionNotNull
import org.bukkit.configuration.ConfigurationSection

class WaypointsConfiguration() {

    val general = GeneralConfiguration()

    val pointer = PointerConfiguration()

    val inventory = InventoryConfiguration()

    val integrations = IntegrationsConfiguration()

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        general.loadFromConfiguration(cfg.getConfigurationSectionNotNull("general"))
        pointer.loadFromConfiguration(cfg.getConfigurationSectionNotNull("pointers"))
        inventory.loadFromConfiguration(cfg.getConfigurationSectionNotNull("inventory"))
        inventory.loadFromConfiguration(cfg.getConfigurationSectionNotNull("integrations"))
    }
}