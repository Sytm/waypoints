package de.md5lukas.waypoints.pointer

import de.md5lukas.waypoints.api.BeaconColor
import de.md5lukas.waypoints.api.StaticTrackable
import org.bukkit.Location
import java.util.*

class StaticTrackableImpl(override val location: Location) : StaticTrackable {
    override val id: UUID = UUID.randomUUID()
    override val beaconColor: BeaconColor? = null
}