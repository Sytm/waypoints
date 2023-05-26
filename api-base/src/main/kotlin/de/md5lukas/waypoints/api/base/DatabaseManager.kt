package de.md5lukas.waypoints.api.base

import de.md5lukas.waypoints.api.WaypointsAPI
import java.sql.Connection
import kotlinx.coroutines.Dispatchers
import org.bukkit.plugin.Plugin

abstract class DatabaseManager(
    val plugin: Plugin,
    val databaseConfiguration: DatabaseConfiguration,
    val testing: Boolean,
) {

  val asyncDispatcher =
      if (testing) {
        Dispatchers.Unconfined
      } else {
        Dispatchers.IO
      }

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
