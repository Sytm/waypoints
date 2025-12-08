package de.md5lukas.waypoints.data

import de.md5lukas.commons.paper.editMeta
import de.md5lukas.jdbc.SQLiteHelper
import de.md5lukas.jdbc.select
import de.md5lukas.jdbc.selectFirst
import de.md5lukas.jdbc.update
import de.md5lukas.waypoints.api.Icon
import de.md5lukas.waypoints.api.OverviewSort
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.WaypointsAPI
import de.md5lukas.waypoints.config.WaypointsConfiguration
import de.md5lukas.waypoints.data.sqlite.WaypointsAPIImpl
import de.md5lukas.waypoints.pointers.BeaconColor
import java.io.File
import java.sql.Connection
import java.time.OffsetDateTime
import java.util.logging.Level
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.Plugin

class SQLiteManager(
    plugin: Plugin,
    databaseConfiguration: WaypointsConfiguration.Database,
    val file: File?,
    testing: Boolean = false,
) : DatabaseManager(plugin, databaseConfiguration, testing) {

  private val schemaVersion: Int = 10
  private val sqliteHelper =
      if (file === null) {
        SQLiteHelper()
      } else {
        SQLiteHelper(file)
      }

  override val api: WaypointsAPI by lazy { WaypointsAPIImpl(this) }

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
            """)
      update(
          "INSERT OR IGNORE INTO database_meta(id, schemaVersion) VALUES (?, ?);", 0, schemaVersion)
      update(
          """
                CREATE TABLE IF NOT EXISTS player_data (
                  id TEXT NOT NULL PRIMARY KEY,
                  
                  showGlobals BOOLEAN NOT NULL DEFAULT 1,
                  sortBy TEXT NOT NULL DEFAULT '${OverviewSort.TYPE_ASCENDING.name}',
                  canBeTracked BOOLEAN NOT NULL DEFAULT 0,
                  canReceiveTemporaryWaypoints BOOLEAN NOT NULL DEFAULT 1,
                  enabledPointers TEXT
                );
            """)
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
                """)
      update(
          """
                CREATE TABLE IF NOT EXISTS folders (
                  id TEXT NOT NULL PRIMARY KEY,
                  
                  createdAt DATE NOT NULL,
                  
                  type TEXT NOT NULL,
                  owner TEXT,
                  
                  name TEXT NOT NULL,
                  description TEXT,
                  icon BLOB,
                  
                  FOREIGN KEY (owner) REFERENCES player_data(id) ON DELETE CASCADE 
                );
            """)
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
                  icon BLOB,
                  beaconColor TEXT,
                  
                  world TEXT NOT NULL,
                  x REAL NOT NULL,
                  y REAL NOT NULL,
                  z REAL NOT NULL,
                  
                  FOREIGN KEY (owner) REFERENCES player_data(id) ON DELETE CASCADE,
                  FOREIGN KEY (folder) REFERENCES folders(id) ON DELETE SET NULL
                );
            """)
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
            """)
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
            """)
      update(
          """
                CREATE TABLE IF NOT EXISTS waypoint_custom_data (
                  waypointId TEXT NOT NULL,
                  key TEXT NOT NULL,
                  
                  data TEXT NOT NULL,
                  
                  PRIMARY KEY (waypointId, key),
                  FOREIGN KEY (waypointId) REFERENCES waypoints(id) ON DELETE CASCADE
                );
                """)
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
            """)
      update(
          """
               CREATE TABLE IF NOT EXISTS waypoint_shares (
                 owner TEXT NOT NULL,
                 sharedWith TEXT NOT NULL,
                 shareId TEXT NOT NULL,
                 expires DATE,
                 
                 PRIMARY KEY (owner, sharedWith, shareId),
                 FOREIGN KEY (owner) REFERENCES player_data(id) ON DELETE CASCADE,
                 FOREIGN KEY (sharedWith) REFERENCES player_data(id) ON DELETE CASCADE,
                 FOREIGN KEY (shareId) REFERENCES waypoints(id) ON DELETE CASCADE
               );
      """)
    }
  }

  override fun cleanDatabase() {
    // Remove death waypoints older than the specified amount of time, if the amount is non-zero
    if (!databaseConfiguration.deathWaypointRetentionPeriod.isZero) {
      connection.update(
          "DELETE FROM waypoints WHERE type = ? AND datetime(createdAt) <= datetime(?);",
          Type.DEATH.name,
          OffsetDateTime.now().minus(databaseConfiguration.deathWaypointRetentionPeriod).toString(),
      )
      connection.update(
          "DELETE FROM waypoint_shares WHERE expires IS NOT NULL AND datetime(expires) <= datetime(?);",
          OffsetDateTime.now(),
      )
    }
  }

  private val databaseUpgrades =
      LinkedHashMap<Int, Connection.() -> Unit>().also {
        it[1] = {
          update(
              "ALTER TABLE player_data ADD COLUMN lastSelectedWaypoint TEXT REFERENCES waypoints (id) ON DELETE SET NULL;")
        }
        it[2] = {
          update("ALTER TABLE player_data ADD COLUMN canBeTracked BOOLEAN NOT NULL DEFAULT 0;")
        }
        @Suppress("SqlResolve") // Table has been deleted, duh
        it[3] = {
          select("SELECT playerId, type, cooldownUntil FROM player_cooldown;") {
            update(
                "INSERT INTO player_data_typed(playerId, type, cooldownUntil) VALUES (?, ?, ?);",
                getString("playerId"),
                getString("type"),
                getString("cooldownUntil"))
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
              )
              .forEach { (old, new) ->
                update("UPDATE waypoints SET beaconColor = ? WHERE beaconColor = ?;", new, old)
              }
        }
        it[6] = { update("ALTER TABLE player_data ADD COLUMN enabledPointers TEXT;") }
        it[7] = {
          update(
              "ALTER TABLE player_data ADD COLUMN canReceiveTemporaryWaypoints BOOLEAN NOT NULL DEFAULT 0;")
          update("UPDATE player_data SET canReceiveTemporaryWaypoints = 1;")
        }
        it[8] = {
          Material.values()
              .filter { material -> !material.isLegacy && material.name.endsWith("WALL_BANNER") }
              .forEach { material ->
                update(
                    "UPDATE waypoints SET material = ? WHERE material = ?;",
                    material.createBlockData().placementMaterial.name,
                    material.name,
                )
              }
        }
        it[9] = {
          BeaconColor.entries
              .map { color -> color.material.name to color.name }
              .forEach { (old, new) ->
                update("UPDATE waypoints SET beaconColor = ? WHERE beaconColor = ?;", new, old)
              }
        }
        @Suppress("SqlResolve") // material column dropped
        it[10] = {
          @Suppress("DEPRECATION") // That's why we are gonna migrate away from it
          fun parseIcon(string: String): ItemStack {
            val index = string.indexOf('|')

            return if (index >= 0) {
              ItemStack.of(Material.valueOf(string.take(index))).also { stack ->
                stack.editMeta<ItemMeta> { setCustomModelData(string.substring(index + 1).toInt()) }
              }
            } else {
              ItemStack.of(Material.valueOf(string))
            }
          }

          update("ALTER TABLE folders ADD COLUMN icon BLOB;")
          update("ALTER TABLE waypoints ADD COLUMN icon BLOB;")

          select("SELECT id, material FROM folders WHERE material IS NOT NULL;") {
            val newFormat = Icon.icon(parseIcon(getString("material"))).getBytes()
            update("UPDATE folders SET icon = ? WHERE id = ?;", newFormat, getString("id"))
          }
          select("SELECT id, material FROM waypoints WHERE material IS NOT NULL;") {
            val newFormat = Icon.icon(parseIcon(getString("material"))).getBytes()
            update("UPDATE waypoints SET icon = ? WHERE id = ?;", newFormat, getString("id"))
          }

          update("ALTER TABLE folders DROP material;")
          update("ALTER TABLE waypoints DROP material;")
        }
      }

  override fun upgradeDatabase() {
    with(connection) {
      val currentSchemaVersion =
          selectFirst("SELECT schemaVersion FROM database_meta WHERE id = ?;", 0) {
            getInt("schemaVersion")
          } ?: throw IllegalStateException("Could not retrieve schema version of database")
      if (currentSchemaVersion > schemaVersion) {
        throw IllegalStateException(
            "The database uses a schema that is newer than the plugin is made for (Database: $currentSchemaVersion, Plugin: $schemaVersion)")
      }

      if (currentSchemaVersion != schemaVersion) {
        plugin.logger.log(
            Level.INFO,
            "Current database schema version: $currentSchemaVersion. Required database schema version: $schemaVersion")
      }

      databaseUpgrades.forEach { (upgradesTo, upgrade) ->
        if (currentSchemaVersion < upgradesTo) {
          try {
            update("BEGIN TRANSACTION;")
            upgrade()
            update("UPDATE database_meta SET schemaVersion = ? WHERE id = ?;", upgradesTo, 0)
            update("COMMIT TRANSACTION;")
          } catch (e: Exception) {
            var suppressed: Exception? = null
            try {
              update("ROLLBACK TRANSACTION;")
            } catch (e2: Exception) {
              suppressed = e2
            }
            throw Exception("Could not perform database upgrade to version $upgradesTo", e).also {
              if (suppressed != null) {
                it.addSuppressed(suppressed)
              }
            }
          }
        }
      }
    }
  }

  override fun close() {
    sqliteHelper.close()
  }
}
