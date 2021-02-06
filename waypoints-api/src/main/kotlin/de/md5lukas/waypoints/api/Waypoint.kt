package de.md5lukas.waypoints.api

import org.bukkit.Location
import org.bukkit.Material
import java.util.*

interface Waypoint {

    val id: UUID

    val type: Type

    val owner: UUID?

    var folder: Folder?

    var name: String

    var description: String?

    var permission: String?

    var material: Material?

    var beaconColor: BeaconColor?

    val location: Location

    fun getWaypointMeta(owner: UUID): WaypointMeta
}