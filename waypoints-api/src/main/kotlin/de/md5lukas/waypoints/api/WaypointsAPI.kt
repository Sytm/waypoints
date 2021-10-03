package de.md5lukas.waypoints.api

import java.util.*

interface WaypointsAPI {

    companion object {
        lateinit var INSTANCE: WaypointsAPI
    }

    fun waypointsPlayerExists(uuid: UUID): Boolean

    fun getWaypointPlayer(uuid: UUID): WaypointsPlayer

    val publicWaypoints: WaypointHolder

    val permissionWaypoints: WaypointHolder
}