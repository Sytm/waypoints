package de.md5lukas.waypoints.api

import java.util.*

interface WaypointsPlayer : WaypointHolder {

    val id: UUID

    var showGlobals: Boolean

    var sortBy: OverviewSort
}