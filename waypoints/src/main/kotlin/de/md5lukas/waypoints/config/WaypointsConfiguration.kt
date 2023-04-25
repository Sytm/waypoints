package de.md5lukas.waypoints.config

import de.md5lukas.waypoints.config.database.DatabaseConfigurationImpl
import de.md5lukas.waypoints.config.general.GeneralConfiguration
import de.md5lukas.waypoints.config.integrations.IntegrationsConfiguration
import de.md5lukas.waypoints.config.inventory.InventoryConfiguration
import de.md5lukas.waypoints.config.pointers.PointerConfigurationImpl
import de.md5lukas.waypoints.config.tracking.PlayerTrackingConfiguration

class WaypointsConfiguration {

    val general = GeneralConfiguration()

    val pointers = PointerConfigurationImpl()

    val inventory = InventoryConfiguration()

    val integrations = IntegrationsConfiguration()

    val playerTracking = PlayerTrackingConfiguration()

    val database = DatabaseConfigurationImpl()
}