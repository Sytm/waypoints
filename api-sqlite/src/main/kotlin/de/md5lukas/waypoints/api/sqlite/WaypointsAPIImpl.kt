package de.md5lukas.waypoints.api.sqlite

import de.md5lukas.jdbc.selectFirst
import de.md5lukas.jdbc.update
import de.md5lukas.waypoints.api.*
import java.util.*
import kotlinx.coroutines.withContext

internal class WaypointsAPIImpl(
    private val dm: SQLiteManager,
) : WaypointsAPI {

  override suspend fun getWaypointPlayer(uuid: UUID): WaypointsPlayer =
      withContext(dm.asyncDispatcher) {
        // Must add the canBeTracked 0, because some users might have an old database that has 1
        // has the default value and that cannot be altered.
        dm.connection.update(
            "INSERT INTO player_data(id, canBeTracked) VALUES(?, 0) ON CONFLICT DO NOTHING;",
            uuid.toString())
        dm.connection.selectFirst("SELECT * FROM player_data WHERE id = ?;", uuid.toString()) {
          WaypointsPlayerImpl(dm, this)
        }!!
      }

  override suspend fun waypointsPlayerExists(uuid: UUID): Boolean =
      withContext(dm.asyncDispatcher) {
        dm.connection.selectFirst(
            "SELECT EXISTS(SELECT 1 FROM player_data WHERE id = ?);", uuid.toString()) {
              getInt(1) == 1
            }
            ?: false
      }

  override val publicWaypoints: WaypointHolder = WaypointHolderImpl(dm, Type.PUBLIC, null)
  override val permissionWaypoints: WaypointHolder = WaypointHolderImpl(dm, Type.PERMISSION, null)

  override suspend fun getWaypointByID(uuid: UUID): Waypoint? =
      dm.connection.selectFirst("SELECT * FROM waypoints WHERE id = ?", uuid.toString()) {
        WaypointImpl(dm, this)
      }

  override val statistics: Statistics = StatisticsImpl(dm)
}
