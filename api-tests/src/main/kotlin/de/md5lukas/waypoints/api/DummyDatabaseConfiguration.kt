package de.md5lukas.waypoints.api

import de.md5lukas.waypoints.api.base.DatabaseConfiguration
import java.time.Period

object DummyDatabaseConfiguration : DatabaseConfiguration {
  override val deathWaypointRetentionPeriod: Period = Period.ofDays(1)
}
