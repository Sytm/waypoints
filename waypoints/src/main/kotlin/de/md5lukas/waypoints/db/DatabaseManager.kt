package de.md5lukas.waypoints.db

import de.md5lukas.jdbc.SQLiteHelper
import de.md5lukas.jdbc.selectFirst
import de.md5lukas.jdbc.update
import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.OverviewSort
import java.io.File
import java.sql.Connection
import java.util.logging.Level

class DatabaseManager(
    val plugin: WaypointsPlugin,
    file: File
) : SQLiteHelper(file) {

    private val schemaVersion: Int = 0
    val instanceCache = InstanceCache()

    fun initDatabase() {
        initConnection()
        createTables()
        upgradeDatabase()
    }

    private fun initConnection() {
        connection.update("PRAGMA foreign_keys = ON;")
    }

    private fun createTables() {
        with(connection) {
            update(
                """
                CREATE TABLE IF NOT EXISTS database_meta (
                  id INTEGER NOT NULL PRIMARY KEY,
                  schemaVersion INTEGER NOT NULL
                );
            """
            )
            update("INSERT OR IGNORE INTO database_meta(id, schemaVersion) VALUES (?, ?);", 0, schemaVersion)
            update(
                """
                CREATE TABLE IF NOT EXISTS player_data (
                  id TEXT NOT NULL PRIMARY KEY,
                  
                  showGlobals BOOLEAN NOT NULL DEFAULT 1,
                  sortBy TEXT NOT NULL DEFAULT '${OverviewSort.TYPE_ASCENDING.name}'
                );
            """
            )
            update(
                """
                CREATE TABLE IF NOT EXISTS folders (
                  id TEXT NOT NULL PRIMARY KEY,
                  
                  createdAt DATE NOT NULL,
                  
                  type TEXT NOT NULL,
                  owner TEXT,
                  
                  name TEXT NOT NULL,
                  description TEXT,
                  material TEXT,
                  
                  FOREIGN KEY (owner) REFERENCES player_data(id) ON DELETE CASCADE 
                );
            """
            )
            update(
                """
                CREATE TABLE IF NOT EXISTS waypoints (
                  id TEXT NOT NULL PRIMARY KEY,
                  
                  createdAt DATE NOT NULL,
                  
                  type TEXT NOT NULL,
                  owner TEXT,
                  folder TEXT,
                  
                  name TEXT NOT NULL,
                  description TEXT,
                  permission TEXT,
                  material TEXT,
                  beaconColor TEXT,
                  
                  world TEXT NOT NULL,
                  x REAL NOT NULL,
                  y REAL NOT NULL,
                  z REAL NOT NULL,
                  
                  FOREIGN KEY (owner) REFERENCES player_data(id) ON DELETE CASCADE,
                  FOREIGN KEY (folder) REFERENCES folders(id) ON DELETE SET NULL
                );
            """
            )
            update(
                """
                CREATE TABLE IF NOT EXISTS waypoint_meta (
                  waypointId TEXT NOT NULL,
                  playerId TEXT NOT NULL,
                  
                  teleportations INTEGER NOT NULL DEFAULT 0,
                  
                  PRIMARY KEY (waypointId, playerId),
                  FOREIGN KEY (waypointId) REFERENCES waypoints(id) ON DELETE CASCADE,
                  FOREIGN KEY (playerId) REFERENCES player_data(id) ON DELETE CASCADE
                );
            """
            )
            update(
                """
               CREATE TABLE IF NOT EXISTS compass_storage (
                 playerId TEXT NOT NULL PRIMARY KEY,
                 
                 world TEXT NOT NULL,
                 x REAL NOT NULL,
                 y REAL NOT NULL,
                 z REAL NOT NULL,
                 
                 FOREIGN KEY (playerId) REFERENCES player_data(id) ON DELETE CASCADE
               );
            """
            )
        }
    }

    private val databaseUpgrades: LinkedHashMap<Int, Connection.() -> Unit> = LinkedHashMap()

    private fun upgradeDatabase() {
        with(connection) {
            val currentSchemaVersion = selectFirst("SELECT schemaVersion FROM database_meta WHERE id = ?", 0) {
                getInt("schemaVersion")
            } ?: throw IllegalStateException("Could not retrieve schema version of database")

            plugin.logger.log(Level.INFO, "Current database schema version: $currentSchemaVersion. Required database schema version: $schemaVersion")

            databaseUpgrades.forEach { (upgradesTo, upgrade) ->
                if (currentSchemaVersion < upgradesTo) {
                    try {
                        upgrade()
                        update("UPDATE database_meta SET schemaVersion = ? WHERE id = ?", upgradesTo, 0)
                    } catch (e: Exception) {
                        throw Exception("Could not perform database upgrade to version $upgradesTo", e)
                    }
                }
            }
        }
    }
}