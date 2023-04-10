package de.md5lukas.waypoints.pointers

import org.bukkit.Location

class TemporaryWaypointTrackable(
    override val location: Location,
    override val beaconColor: BeaconColor? = null
) : StaticTrackable