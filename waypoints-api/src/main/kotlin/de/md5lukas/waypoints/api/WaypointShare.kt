package de.md5lukas.waypoints.api

import java.time.OffsetDateTime
import java.util.UUID

interface WaypointShare : Deletable {

  val owner: UUID
  val sharedWith: UUID

  val waypointId: UUID

  @JvmSynthetic suspend fun getWaypoint(): Waypoint

  fun getWaypointCF() = future { getWaypoint() }

  val expires: OffsetDateTime?

  @JvmSynthetic suspend fun setExpires(expires: OffsetDateTime?)

  fun setExpiresCF(expires: OffsetDateTime?) = future { setExpires(expires) }

  @JvmSynthetic override suspend fun delete()
}
