package de.md5lukas.waypoints.api

import java.util.*

interface WaypointsAPI {

    companion object : WaypointsAPI by WaypointsAPI.instance {
        private lateinit var instance: WaypointsAPI

        fun setInstance(instance: WaypointsAPI) {
            if (!this::instance.isInitialized) {
                this.instance = instance
            }
        }
    }

    fun getWaypointPlayer(uuid: UUID): WaypointsPlayer

    val publicWaypoints: WaypointHolder

    val permissionWaypoints: WaypointHolder
}