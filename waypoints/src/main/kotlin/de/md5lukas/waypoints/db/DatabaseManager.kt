package de.md5lukas.waypoints.db

import de.md5lukas.waypoints.jdbc.SQLiteHelper
import de.md5lukas.waypoints.jdbc.update
import de.md5lukas.waypoints.WaypointsPlugin
import java.io.File

class DatabaseManager(
    val plugin: WaypointsPlugin,
    file: File
) : SQLiteHelper(file) {

    fun initDatabase() {
        initConnection()
        createTables()
    }

    private fun initConnection() {
        connection.update("PRAGMA foreign_keys = ON;")
    }

    private fun createTables() {
        with (connection) {
            update("""
                CREATE TABLE IF NOT EXISTS player_data (
                  id TEXT NOT NULL PRIMARY KEY,
                  
                  showGlobals BOOLEAN NOT NULL DEFAULT 1,
                  sortBy TEXT NOT NULL DEFAULT 'TYPE'
                );
            """)
            update("""
                CREATE TABLE IF NOT EXISTS folders (
                  id TEXT NOT NULL PRIMARY KEY,
                  
                  type TEXT NOT NULL,
                  owner TEXT,
                  
                  name TEXT NOT NULL,
                  description TEXT,
                  material TEXT,
                  
                  FOREIGN KEY (owner) REFERENCES player_data(id) ON DELETE CASCADE 
                );
            """)
            update("""
                CREATE TABLE IF NOT EXISTS waypoints (
                  id TEXT NOT NULL PRIMARY KEY,
                  
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
                  FOREIGN KEY (folder) REFERENCES folders(id) ON DELETE CASCADE
                );
            """)
            update("""
                CREATE TABLE IF NOT EXISTS waypoint_meta (
                  waypointId TEXT NOT NULL,
                  playerId TEXT NOT NULL,
                  
                  teleportations INTEGER NOT NULL DEFAULT 0,
                  
                  PRIMARY KEY (waypointId, playerId),
                  FOREIGN KEY (waypointId) REFERENCES waypoints(id) ON DELETE CASCADE,
                  FOREIGN KEY (playerId) REFERENCES player_data(id) ON DELETE CASCADE
                );
            """)
            update("""
               CREATE TABLE IF NOT EXISTS compass_storage (
                 playerId TEXT NOT NULL PRIMARY KEY,
                 
                 world TEXT NOT NULL,
                 x REAL NOT NULL,
                 y REAL NOT NULL,
                 z REAL NOT NULL,
                 
                 FOREIGN KEY (playerId) REFERENCES player_data(id) ON DELETE CASCADE
               );
            """)
        }
    }
}