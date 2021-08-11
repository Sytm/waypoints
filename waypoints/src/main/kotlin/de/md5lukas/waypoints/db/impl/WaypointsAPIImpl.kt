package de.md5lukas.waypoints.db.impl

import de.md5lukas.waypoints.jdbc.selectFirst
import de.md5lukas.waypoints.api.*
import de.md5lukas.waypoints.db.DatabaseManager
import java.util.*

internal class WaypointsAPIImpl(private val dm: DatabaseManager) : WaypointsAPI {

    override fun getWaypointPlayer(uuid: UUID): WaypointsPlayer {
        return dm.connection.selectFirst("INSERT OR IGNORE INTO player_data(id) VALUES(?); SELECT id, showGlobals, sortBy FROM player_data WHERE id = ?;",
            uuid.toString(), uuid.toString()) {
            WaypointsPlayerImpl(dm, this)
        } as WaypointsPlayer
    }

    override val publicWaypoints: WaypointHolder = WaypointHolderImpl(dm, Type.PUBLIC, null)
    override val permissionWaypoints: WaypointHolder = WaypointHolderImpl(dm, Type.PERMISSION, null)
}