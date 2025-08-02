package de.md5lukas.waypoints.api

import java.util.*
import org.bukkit.Location

interface PublicWaypointHolder : WaypointHolder {

  suspend fun getWaypointsAmount(creator: UUID): Int

  suspend fun getFoldersAmount(creator: UUID): Int

  /**
   * Creates a new Waypoint in this holder with the given name and location, created by an arbitrary
   * creator
   *
   * @param name The name of the waypoint
   * @param location The location of the waypoint
   * @param creator The player that created this waypoint
   * @return The newly created waypoint
   */
  suspend fun createWaypoint(name: String, location: Location, creator: UUID): Waypoint

  /**
   * Creates a new folder in this holder with the given name
   *
   * @param name The name of the folder
   * @return The newly created folder
   */
  suspend fun createFolder(name: String, creator: UUID): Folder
}
