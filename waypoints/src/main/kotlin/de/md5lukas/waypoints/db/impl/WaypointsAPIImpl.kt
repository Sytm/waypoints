package de.md5lukas.waypoints.db.impl

import de.md5lukas.jdbc.selectFirst
import de.md5lukas.jdbc.update
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.WaypointHolder
import de.md5lukas.waypoints.api.WaypointsAPI
import de.md5lukas.waypoints.api.WaypointsPlayer
import de.md5lukas.waypoints.db.DatabaseManager
import java.util.*

internal class WaypointsAPIImpl(private val dm: DatabaseManager) : WaypointsAPI {

    override fun getWaypointPlayer(uuid: UUID): WaypointsPlayer {
        dm.connection.update("INSERT OR IGNORE INTO player_data(id) VALUES(?);", uuid.toString())
        return dm.connection.selectFirst("SELECT id, showGlobals, sortBy FROM player_data WHERE id = ?;", uuid.toString()) {
            WaypointsPlayerImpl(dm, this)
        } as WaypointsPlayer
    }

    override fun waypointsPlayerExists(uuid: UUID): Boolean {
        return dm.connection.selectFirst("SELECT EXISTS(SELECT 1 FROM player_data WHERE id = ?);", uuid.toString()) {
            getInt(0) == 1
        } ?: false
    }

    override val publicWaypoints: WaypointHolder = WaypointHolderImpl(dm, Type.PUBLIC, null)
    override val permissionWaypoints: WaypointHolder = WaypointHolderImpl(dm, Type.PERMISSION, null)
}