package de.md5lukas.waypoints.pointer

import de.md5lukas.waypoints.api.BeaconColor
import de.md5lukas.waypoints.api.StaticTrackable
import org.bukkit.Location
import java.util.*

open class StaticTrackableImpl(
    override val location: Location,
    override val beaconColor: BeaconColor? = null,
    override val hologramText: String? = null
) : StaticTrackable {
    override val id: UUID = UUID.randomUUID()
}

class TemporaryWaypointTrackable(location: Location) : StaticTrackableImpl(location)