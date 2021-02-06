package de.md5lukas.waypoints

import de.md5lukas.waypoints.api.WaypointsAPI
import de.md5lukas.waypoints.db.DatabaseManager
import de.md5lukas.waypoints.db.impl.WaypointsAPIImpl
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class WaypointsPlugin : JavaPlugin() {

    private lateinit var databaseManager: DatabaseManager

    override fun onEnable() {
        initDatabase()
    }

    private fun initDatabase() {
        databaseManager = DatabaseManager(this, File(dataFolder, "waypoints.db"))

        databaseManager.initDatabase()

        WaypointsAPI.setInstance(WaypointsAPIImpl(databaseManager))
    }
}