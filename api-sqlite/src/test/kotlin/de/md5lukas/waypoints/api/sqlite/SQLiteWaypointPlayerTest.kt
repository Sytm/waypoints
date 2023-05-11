package de.md5lukas.waypoints.api.sqlite

import de.md5lukas.waypoints.api.DummyDatabaseConfiguration
import de.md5lukas.waypoints.api.SQLiteManager
import de.md5lukas.waypoints.api.WaypointPlayerTest
import de.md5lukas.waypoints.api.base.DatabaseManager

class SQLiteWaypointPlayerTest : WaypointPlayerTest() {

  override fun createDatabaseManager(): DatabaseManager {
    return SQLiteManager(plugin, DummyDatabaseConfiguration, null, true)
  }
}
