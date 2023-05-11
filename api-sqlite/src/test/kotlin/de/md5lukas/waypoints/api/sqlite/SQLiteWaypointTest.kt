package de.md5lukas.waypoints.api.sqlite

import de.md5lukas.waypoints.api.DummyDatabaseConfiguration
import de.md5lukas.waypoints.api.SQLiteManager
import de.md5lukas.waypoints.api.WaypointTest
import de.md5lukas.waypoints.api.base.DatabaseManager

class SQLiteWaypointTest : WaypointTest() {

  override fun createDatabaseManager(): DatabaseManager {
    return SQLiteManager(plugin, DummyDatabaseConfiguration, null, true)
  }
}
