package de.md5lukas.waypoints.api

import org.bukkit.Location
import java.util.*

interface Trackable {

    val id: UUID

    val location: Location

    val beaconColor: BeaconColor?
}