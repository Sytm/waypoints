package de.md5lukas.waypoints.api

import org.bukkit.Location

interface WaypointHolder {

    val folders: List<Folder>

    val topLevelWaypoints: List<Waypoint>

    val allWaypoints: List<Waypoint>


    fun createWaypoint(name: String, location: Location): Waypoint

    fun createFolder(name: String): Folder
}