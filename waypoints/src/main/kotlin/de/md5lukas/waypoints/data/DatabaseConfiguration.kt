package de.md5lukas.waypoints.data

import java.time.Period

interface DatabaseConfiguration {

  val deathWaypointRetentionPeriod: Period
}
