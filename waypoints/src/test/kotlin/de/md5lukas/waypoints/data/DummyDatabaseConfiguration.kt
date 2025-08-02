package de.md5lukas.waypoints.data

import java.time.Period

object DummyDatabaseConfiguration : DatabaseConfiguration {
  override val deathWaypointRetentionPeriod: Period = Period.ofDays(1)
}
