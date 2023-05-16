package de.md5lukas.waypoints.api.gui

/** This enum provides categories to sort the overview in the GUI with. */
enum class GUIType {
  /** The item for the permissions waypoints */
  PERMISSION_HOLDER,

  /** The item for the public waypoints and player tracking */
  PUBLIC_HOLDER,

  /** The item for the death folder */
  DEATH_FOLDER,

  /** The items for other folders */
  FOLDER,

  /** The items for the waypoints */
  WAYPOINT,
}
