package de.md5lukas.waypoints.api.sqlite

import de.md5lukas.jdbc.update
import de.md5lukas.waypoints.api.WaypointMeta
import de.md5lukas.waypoints.api.base.DatabaseManager
import de.md5lukas.waypoints.api.base.getUUID
import java.sql.ResultSet
import java.util.*
import kotlinx.coroutines.withContext

class WaypointMetaImpl
private constructor(
    private val dm: DatabaseManager,
    override val waypoint: UUID,
    override val owner: UUID,
    teleportations: Int,
    visited: Boolean,
) : WaypointMeta {

  constructor(
      dm: DatabaseManager,
      row: ResultSet
  ) : this(
      dm = dm,
      waypoint = row.getUUID("waypointId")!!,
      owner = row.getUUID("playerId")!!,
      teleportations = row.getInt("teleportations"),
      visited = row.getBoolean("visited"),
  )

  override var teleportations: Int = teleportations
    private set

  override suspend fun setTeleportations(teleportations: Int) {
    this.teleportations = teleportations
    set("teleportations", teleportations)
  }

  override var visited: Boolean = visited
    private set

  override suspend fun setVisisted(visited: Boolean) {
    this.visited = visited
    set("visited", visited)
  }

  private suspend fun set(column: String, value: Any?) {
    withContext(dm.asyncDispatcher) {
      dm.connection.update(
          "UPDATE waypoint_meta SET $column = ? WHERE waypointId = ? AND playerId = ?;",
          value,
          waypoint.toString(),
          owner.toString())
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as WaypointMeta

    if (waypoint != other.waypoint) return false
    return owner == other.owner
  }

  override fun hashCode(): Int {
    var result = waypoint.hashCode()
    result = 31 * result + owner.hashCode()
    return result
  }

  override fun toString(): String {
    return "WaypointMetaImpl(waypoint=$waypoint, owner=$owner, teleportations=$teleportations, visited=$visited)"
  }
}
