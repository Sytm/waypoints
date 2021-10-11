package de.md5lukas.waypoints.api

import org.bukkit.Location
import java.util.*

interface WaypointsPlayer : WaypointHolder {

    val id: UUID

    var showGlobals: Boolean

    var sortBy: OverviewSort

    fun addDeathLocation(location: Location)

    val deathFolder: Folder
}