package de.md5lukas.waypoints.api

import java.util.*

interface WaypointsAPI {

    companion object {
        var INSTANCE: WaypointsAPI
            set(value) = if (nullableInstance == null) {
                nullableInstance = value
            } else {
                throw IllegalStateException("The instance has already been set")
            }
            get() = nullableInstance ?: throw IllegalStateException("There has been no instance set yet")

        private var nullableInstance: WaypointsAPI? = null
    }

    fun waypointsPlayerExists(uuid: UUID): Boolean

    fun getWaypointPlayer(uuid: UUID): WaypointsPlayer

    val publicWaypoints: WaypointHolder

    val permissionWaypoints: WaypointHolder

    fun getWaypointByID(uuid: UUID): Waypoint?

    val statistics: Statistics
}