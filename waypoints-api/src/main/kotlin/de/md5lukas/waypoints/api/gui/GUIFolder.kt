package de.md5lukas.waypoints.api.gui

import de.md5lukas.waypoints.api.Folder
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.future

/**
 * An extension of the GUIDisplayable providing information necessary to use the displayable as a
 * folder
 */
interface GUIFolder : GUIDisplayable {

  /** Every folder contained in this displayable */
  @JvmSynthetic suspend fun getFolders(): List<Folder>

  fun getFoldersCF() = future { getFolders() }

  /** Every waypoint contained in this displayable */
  @JvmSynthetic suspend fun getWaypoints(): List<Waypoint>

  fun getWaypointsCF() = future { getWaypoints() }
}
