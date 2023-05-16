package de.md5lukas.waypoints.api

/** This interface provides access to some usage statistics of the plugin */
interface Statistics {

  /** The total amount of waypoints in the database. Counts every [Type] */
  val totalWaypoints: Int

  /** The total amount of waypoints of the type [Type.PRIVATE] */
  val privateWaypoints: Int

  /** The total amount of waypoints of the type [Type.DEATH] */
  val deathWaypoints: Int

  /** The total amount of waypoints of the type [Type.PUBLIC] */
  val publicWaypoints: Int

  /** The total amount of waypoints of the type [Type.PERMISSION] */
  val permissionWaypoints: Int

  /** The total amount of folders in the database. Counts every [Type] */
  val totalFolders: Int

  /** The total amount of folders of the type [Type.PRIVATE] */
  val privateFolders: Int

  /** The total amount of folders of the type [Type.PUBLIC] */
  val publicFolders: Int

  /** The total amount of folders of the type [Type.PERMISSION] */
  val permissionFolders: Int

  /** The size of the SQLite database file as reported by [java.io.File.length] */
  val databaseSize: Long
}
