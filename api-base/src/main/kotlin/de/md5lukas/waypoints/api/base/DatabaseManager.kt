package de.md5lukas.waypoints.api.base

import de.md5lukas.waypoints.api.WaypointsAPI
import org.bukkit.plugin.Plugin
import java.sql.Connection

abstract class DatabaseManager(
    val plugin: Plugin
) {
    val instanceCache: InstanceCache = InstanceCache()

    abstract val api: WaypointsAPI

    abstract val connection: Connection

    fun initDatabase() {
        initConnection()
        createTables()
        upgradeDatabase()
        cleanDatabase()
    }

    protected open fun initConnection() {}

    protected open fun createTables() {}

    protected open fun upgradeDatabase() {}

    protected open fun cleanDatabase() {}

    open fun close() {}
}