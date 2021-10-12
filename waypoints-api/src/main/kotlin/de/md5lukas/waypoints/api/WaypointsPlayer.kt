package de.md5lukas.waypoints.api

import org.bukkit.Location
import java.time.OffsetDateTime
import java.util.*

interface WaypointsPlayer : WaypointHolder {

    val id: UUID

    var showGlobals: Boolean

    var sortBy: OverviewSort

    fun getCooldownUntil(type: Type): OffsetDateTime?

    fun setCooldownUntil(type: Type, cooldownUntil: OffsetDateTime)

    fun addDeathLocation(location: Location)

    val deathFolder: Folder
}