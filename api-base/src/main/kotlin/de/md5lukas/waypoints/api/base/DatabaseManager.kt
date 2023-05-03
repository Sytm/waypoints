package de.md5lukas.waypoints.api.base

import com.okkero.skedule.dispatcher
import de.md5lukas.waypoints.api.WaypointsAPI
import kotlinx.coroutines.CoroutineDispatcher
import org.bukkit.plugin.Plugin
import java.sql.Connection

abstract class DatabaseManager(
    val plugin: Plugin,
    val databaseConfiguration: DatabaseConfiguration,
    disableInstanceCache: Boolean,
    dispatcher: CoroutineDispatcher?,
) {
    val instanceCache: InstanceCache = InstanceCache(disableInstanceCache)

    val asyncDispatcher = dispatcher ?: plugin.dispatcher(true)

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

    open fun cleanDatabase() {}

    open fun close() {}
}