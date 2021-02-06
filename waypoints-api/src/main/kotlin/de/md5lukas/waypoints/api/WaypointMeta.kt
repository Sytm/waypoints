package de.md5lukas.waypoints.api

import java.util.*

interface WaypointMeta {

    val waypoint: UUID
    val owner: UUID

    var teleportations: Int
}