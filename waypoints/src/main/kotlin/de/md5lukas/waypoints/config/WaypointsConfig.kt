package de.md5lukas.waypoints.config

import de.md5lukas.waypoints.config.general.GeneralConfiguration
import de.md5lukas.waypoints.config.inventory.InventoryConfiguration
import de.md5lukas.waypoints.config.pointers.PointerConfiguration
import de.md5lukas.waypoints.util.getConfigurationSectionNotNull
import org.bukkit.configuration.ConfigurationSection

class WaypointsConfig() {

    val general = GeneralConfiguration()

    val pointer = PointerConfiguration()

    val inventory = InventoryConfiguration()

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        general.loadFromConfiguration(cfg.getConfigurationSectionNotNull("general"))
        pointer.loadFromConfiguration(cfg.getConfigurationSectionNotNull("pointers"))
        inventory.loadFromConfiguration(cfg.getConfigurationSectionNotNull("inventory"))
    }
}