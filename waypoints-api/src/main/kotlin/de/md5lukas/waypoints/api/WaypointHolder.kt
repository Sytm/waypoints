package de.md5lukas.waypoints.api

import de.md5lukas.waypoints.api.gui.GUIFolder
import org.bukkit.Location
import org.bukkit.permissions.Permissible

/** The WaypointHolder provides methods and properties to accumulate waypoints and folders */
interface WaypointHolder : GUIFolder {

  /**
   * All the waypoints that belong to this WaypointHolder disregarding the folder it is in.
   *
   * @see getWaypointsAmount If you just need the total amount without the overhead of creating the
   *   objects and counting the list.
   */
  @JvmSynthetic suspend fun getAllWaypoints(): List<Waypoint>

  /**
   * The total amount of waypoints that belong to this WaypointHolder disregarding the folder it is
   * in.
   */
  @JvmSynthetic suspend fun getWaypointsAmount(): Int

  /** The total amount of folders that belong to this WaypointHolder. */
  @JvmSynthetic suspend fun getFoldersAmount(): Int

  /**
   * The total amount of waypoints that belong to this WaypointHolder disregarding the folder it is
   * in.
   *
   * If the type of this holder is [Type.PERMISSION], then the waypoints the player does not have
   * the permission for are omitted from the amount.
   */
  @JvmSynthetic suspend fun getWaypointsVisibleForPlayer(permissible: Permissible): Int

  /**
   * Creates a new Waypoint in this holder with the given name and location
   *
   * @param name The name of the waypoint
   * @param location The location of the waypoint
   * @return The newly created waypoint
   */
  @JvmSynthetic suspend fun createWaypoint(name: String, location: Location): Waypoint

  /**
   * Creates a new folder in this holder with the given name
   *
   * @param name The name of the folder
   * @return The newly created folder
   */
  @JvmSynthetic suspend fun createFolder(name: String): Folder

  /**
   * Checks if a waypoint with the provided name already exists. The case of the name is ignored
   * during the comparison.
   *
   * @param name The name to look for
   * @return `true` if a waypoint exists with the name
   */
  @JvmSynthetic suspend fun isDuplicateWaypointName(name: String): Boolean

  /**
   * Checks if a folder with the provided name already exists. The case of the name is ignored
   * during the comparison.
   *
   * @param name The name to look for
   * @return `true` if a folder exists with the name
   */
  @JvmSynthetic suspend fun isDuplicateFolderName(name: String): Boolean

  /**
   * Searches for folders
   *
   * An empty string will return all folders like [getFolders]
   *
   * @param query The text that folder names must match
   * @param permissible The permissible to check the permissions for
   * @return All matching folders, or none
   */
  @JvmSynthetic
  suspend fun searchFolders(
      query: String,
      permissible: Permissible? = null
  ): List<SearchResult<out Folder>>

  /**
   * Searches for waypoints in this holder, case is ignored.
   *
   * If the query contains a forward slash, the part before it will be used as the folder name that
   * must match.
   *
   * An empty string will return all waypoints like [getAllWaypoints]
   *
   * @param query The text that waypoint names must match
   * @return All matching waypoints, or none
   */
  @JvmSynthetic
  suspend fun searchWaypoints(
      query: String,
      permissible: Permissible? = null
  ): List<SearchResult<out Waypoint>>
}
