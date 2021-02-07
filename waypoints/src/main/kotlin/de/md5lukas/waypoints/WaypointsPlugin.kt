package de.md5lukas.waypoints

import de.md5lukas.waypoints.api.WaypointsAPI
import de.md5lukas.waypoints.config.WaypointsConfig
import de.md5lukas.waypoints.db.DatabaseManager
import de.md5lukas.waypoints.db.impl.WaypointsAPIImpl
import de.md5lukas.waypoints.pointer.PointerManager
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class WaypointsPlugin : JavaPlugin() {

    private lateinit var databaseManager: DatabaseManager
    lateinit var waypointsConfig: WaypointsConfig
        private set
    lateinit var pointerManager: PointerManager
        private set

    override fun onEnable() {
        loadConfiguration()
        initDatabase()
        initPointerManager()
    }

    override fun onDisable() {
        databaseManager.close()
    }

    private fun loadConfiguration() {
        TODO("Load pointer config and set compass storage")
    }

    private fun initDatabase() {
        databaseManager = DatabaseManager(this, File(dataFolder, "waypoints.db"))

        databaseManager.initDatabase()

        WaypointsAPI.setInstance(WaypointsAPIImpl(databaseManager))
    }

    private fun initPointerManager() {
        pointerManager = PointerManager(this)
    }
}