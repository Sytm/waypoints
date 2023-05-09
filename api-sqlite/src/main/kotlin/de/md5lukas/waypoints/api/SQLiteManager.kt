package de.md5lukas.waypoints.api

import de.md5lukas.jdbc.SQLiteHelper
import de.md5lukas.jdbc.select
import de.md5lukas.jdbc.selectFirst
import de.md5lukas.jdbc.update
import de.md5lukas.waypoints.api.base.DatabaseConfiguration
import de.md5lukas.waypoints.api.base.DatabaseManager
import de.md5lukas.waypoints.api.sqlite.WaypointsAPIImpl
import kotlinx.coroutines.CoroutineDispatcher
import org.bukkit.Material
import org.bukkit.plugin.Plugin
import java.io.File
import java.sql.Connection
import java.time.OffsetDateTime
import java.util.logging.Level

class SQLiteManager(
    plugin: Plugin,
    databaseConfiguration: DatabaseConfiguration,
    val file: File?,
    disableInstanceCache: Boolean = false,
    dispatcher: CoroutineDispatcher? = null
) : DatabaseManager(plugin, databaseConfiguration, disableInstanceCache, dispatcher) {

    private val schemaVersion: Int = 5
    private val sqliteHelper = if (file === null) {
        SQLiteHelper()
    } else {
        SQLiteHelper(file)
    }

    override val api: WaypointsAPI by lazy {
        WaypointsAPIImpl(this)
    }

    override val connection: Connection
        get() = sqliteHelper.connection

    override fun initConnection() {
        connection.update("PRAGMA foreign_keys = ON;")
    }

    override fun createTables() {
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
                  sortBy TEXT NOT NULL DEFAULT '${OverviewSort.TYPE_ASCENDING.name}',
                  canBeTracked BOOLEAN NOT NULL DEFAULT 0,
                  lastSelectedWaypoint TEXT,
                  
                  FOREIGN KEY (lastSelectedWaypoint) REFERENCES waypoints(id) ON DELETE SET NULL
                );
            """
            )
            update(
                """
                CREATE TABLE IF NOT EXISTS player_data_typed (
                  playerId TEXT NOT NULL,
                  type TEXT NOT NULL,
                  
                  cooldownUntil TEXT,
                  teleportations INTEGER NOT NULL DEFAULT 0,
                  
                  PRIMARY KEY (playerId, type),
                  FOREIGN KEY (playerId) REFERENCES player_data(id) ON DELETE CASCADE
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
                  visited BOOLEAN NOT NULL DEFAULT 0,
                  
                  PRIMARY KEY (waypointId, playerId),
                  FOREIGN KEY (waypointId) REFERENCES waypoints(id) ON DELETE CASCADE,
                  FOREIGN KEY (playerId) REFERENCES player_data(id) ON DELETE CASCADE
                );
            """
            )
            update(
                """
                CREATE TABLE IF NOT EXISTS selected_waypoints (
                  playerId TEXT NOT NULL,
                  waypointId TEXT NOT NULL,
                  'index' INTEGER NOT NULL,
                  
                  PRIMARY KEY (playerId, waypointId),
                  FOREIGN KEY (playerId) REFERENCES player_data(id) ON DELETE CASCADE,
                  FOREIGN KEY (waypointId) REFERENCES waypoints(id) ON DELETE CASCADE
                );
            """
            )
            update(
                """
                CREATE TABLE IF NOT EXISTS waypoint_custom_data (
                  waypointId TEXT NOT NULL,
                  key TEXT NOT NULL,
                  
                  data TEXT NOT NULL,
                  
                  PRIMARY KEY (waypointId, key),
                  FOREIGN KEY (waypointId) REFERENCES waypoints(id) ON DELETE CASCADE
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

    override fun cleanDatabase() {
        // Remove death waypoints older than the specified amount of time, if the amount is non-zero
        if (!databaseConfiguration.deathWaypointRetentionPeriod.isZero) {
            connection.update(
                "DELETE FROM waypoints WHERE type = ? AND datetime(createdAt) <= datetime(?)",
                Type.DEATH.name,
                OffsetDateTime.now().minus(databaseConfiguration.deathWaypointRetentionPeriod).toString()
            )
        }
    }

    private val databaseUpgrades = LinkedHashMap<Int, Connection.() -> Unit>().also {
        it[1] = {
            update("ALTER TABLE player_data ADD COLUMN lastSelectedWaypoint TEXT REFERENCES waypoints (id) ON DELETE SET NULL;")
        }
        it[2] = {
            update("ALTER TABLE player_data ADD COLUMN canBeTracked BOOLEAN NOT NULL DEFAULT 0;")
        }
        @Suppress("SqlResolve") // Table has been deleted, duh
        it[3] = {
            select("SELECT playerId, type, cooldownUntil FROM player_cooldown;") {
                update(
                    "INSERT INTO player_data_typed(playerId, type, cooldownUntil) VALUES (?, ?, ?);",
                    getString("playerId"), getString("type"), getString("cooldownUntil")
                )
            }
            update("DROP TABLE player_cooldown;")
        }
        it[4] = {
            update("ALTER TABLE waypoint_meta ADD COLUMN visited BOOLEAN NOT NULL DEFAULT 0;")
        }
        it[5] = {
            mapOf(
                "CLEAR" to Material.GLASS.name,
                "LIGHT_GRAY" to Material.LIGHT_GRAY_STAINED_GLASS.name,
                "GRAY" to Material.GRAY_STAINED_GLASS.name,
                "PINK" to Material.PINK_STAINED_GLASS.name,
                "LIME" to Material.LIME_STAINED_GLASS.name,
                "YELLOW" to Material.YELLOW_STAINED_GLASS.name,
                "LIGHT_BLUE" to Material.LIGHT_BLUE_STAINED_GLASS.name,
                "MAGENTA" to Material.MAGENTA_STAINED_GLASS.name,
                "ORANGE" to Material.ORANGE_STAINED_GLASS.name,
                "WHITE" to Material.WHITE_STAINED_GLASS.name,
                "BLACK" to Material.BLACK_STAINED_GLASS.name,
                "RED" to Material.RED_STAINED_GLASS.name,
                "GREEN" to Material.GREEN_STAINED_GLASS.name,
                "BROWN" to Material.BROWN_STAINED_GLASS.name,
                "BLUE" to Material.BLUE_STAINED_GLASS.name,
                "CYAN" to Material.CYAN_STAINED_GLASS.name,
                "PURPLE" to Material.PURPLE_STAINED_GLASS.name,
            ).forEach { (old, new) ->
                update("UPDATE waypoints SET beaconColor = ? WHERE beaconColor = ?;", new, old)
            }
        }
    }

    override fun upgradeDatabase() {
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

    override fun close() {
        sqliteHelper.close()
    }
}