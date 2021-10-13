package de.md5lukas.waypoints.db

import de.md5lukas.jdbc.selectFirst
import de.md5lukas.waypoints.api.Type

class Statistics(
    private val dm: DatabaseManager
) {

    val totalWaypoints: Int
        get() = dm.connection.selectFirst(
            "SELECT COUNT(*) FROM waypoints;"
        ) {
            getInt(1)
        } ?: 0

    val privateWaypoints: Int
        get() = getWaypointCount(Type.PRIVATE)

    val deathWaypoints: Int
        get() = getWaypointCount(Type.DEATH)

    val publicWaypoints: Int
        get() = getWaypointCount(Type.PUBLIC)

    val permissionWaypoints: Int
        get() = getWaypointCount(Type.PERMISSION)


    val totalFolders: Int
        get() = dm.connection.selectFirst(
            "SELECT COUNT(*) FROM folders;"
        ) {
            getInt(1)
        } ?: 0

    val privateFolders: Int
        get() = getFolderCount(Type.PRIVATE)

    val deathFolders: Int
        get() = getFolderCount(Type.DEATH)

    val publicFolders: Int
        get() = getFolderCount(Type.PUBLIC)

    val permissionFolders: Int
        get() = getFolderCount(Type.PERMISSION)

    private fun getWaypointCount(type: Type) = dm.connection.selectFirst(
        "SELECT COUNT(*) FROM waypoints WHERE type = ?;",
        type.name
    ) {
        getInt(1)
    } ?: 0

    private fun getFolderCount(type: Type) = dm.connection.selectFirst(
        "SELECT COUNT(*) FROM folders WHERE type = ?;",
        type.name
    ) {
        getInt(1)
    } ?: 0

    val databaseSize: Long
        get() = dm.file.length()
}