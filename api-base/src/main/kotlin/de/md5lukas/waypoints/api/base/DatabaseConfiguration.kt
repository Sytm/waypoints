package de.md5lukas.waypoints.api.base

import java.time.Period

interface DatabaseConfiguration {

    val deathWaypointRetentionPeriod: Period
}