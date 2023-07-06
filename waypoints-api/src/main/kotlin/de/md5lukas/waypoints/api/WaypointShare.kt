package de.md5lukas.waypoints.api

import java.time.OffsetDateTime
import java.util.UUID

interface WaypointShare {

  val owner: UUID
  val sharedWith: UUID

  val waypointId: UUID

  @JvmSynthetic suspend fun getWaypoint(): Waypoint

  val expires: OffsetDateTime?

  @JvmSynthetic suspend fun setExpires(expires: OffsetDateTime?)

  @JvmSynthetic suspend fun delete()
}
