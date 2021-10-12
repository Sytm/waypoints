package de.md5lukas.waypoints.db.impl

import de.md5lukas.jdbc.update
import de.md5lukas.waypoints.api.WaypointMeta
import de.md5lukas.waypoints.db.DatabaseManager
import de.md5lukas.waypoints.util.runTaskAsync
import java.sql.ResultSet
import java.util.*

class WaypointMetaImpl private constructor(
    private val dm: DatabaseManager,
    override val waypoint: UUID,
    override val owner: UUID,
    teleportations: Int,
) : WaypointMeta {

    constructor(dm: DatabaseManager, row: ResultSet) : this(
        dm = dm,
        waypoint = UUID.fromString(row.getString("waypointId")),
        owner = UUID.fromString(row.getString("playerId")),
        teleportations = row.getInt("teleportations"),
    )

    override var teleportations: Int = teleportations
        set(value) {
            field = value
            set("teleportations", value)
        }

    private fun set(column: String, value: Any?) {
        dm.plugin.runTaskAsync {
            dm.connection.update("UPDATE waypoint_meta SET $column = ? WHERE waypointId = ? AND playerId = ?;", value, waypoint.toString(), owner.toString())
        }
    }
}