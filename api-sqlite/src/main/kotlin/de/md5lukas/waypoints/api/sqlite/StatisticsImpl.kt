package de.md5lukas.waypoints.api.sqlite

import de.md5lukas.jdbc.selectFirst
import de.md5lukas.waypoints.api.SQLiteManager
import de.md5lukas.waypoints.api.Statistics
import de.md5lukas.waypoints.api.Type

class StatisticsImpl(
    private val dm: SQLiteManager
) : Statistics {

    override val totalWaypoints: Int
        get() = dm.connection.selectFirst(
            "SELECT COUNT(*) FROM waypoints;"
        ) {
            getInt(1)
        } ?: 0

    override val privateWaypoints: Int
        get() = getWaypointCount(Type.PRIVATE)

    override val deathWaypoints: Int
        get() = getWaypointCount(Type.DEATH)

    override val publicWaypoints: Int
        get() = getWaypointCount(Type.PUBLIC)

    override val permissionWaypoints: Int
        get() = getWaypointCount(Type.PERMISSION)


    override val totalFolders: Int
        get() = dm.connection.selectFirst(
            "SELECT COUNT(*) FROM folders;"
        ) {
            getInt(1)
        } ?: 0

    override val privateFolders: Int
        get() = getFolderCount(Type.PRIVATE)

    override val publicFolders: Int
        get() = getFolderCount(Type.PUBLIC)

    override val permissionFolders: Int
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

    override val databaseSize: Long
        get() = dm.file?.length() ?: 0
}