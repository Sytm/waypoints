package de.md5lukas.waypoints.tasks

import de.md5lukas.waypoints.WaypointsPlugin

class CleanDatabaseTask(private val plugin: WaypointsPlugin) : Runnable {

    override fun run() {
        plugin.databaseManager.cleanDatabase()
    }
}