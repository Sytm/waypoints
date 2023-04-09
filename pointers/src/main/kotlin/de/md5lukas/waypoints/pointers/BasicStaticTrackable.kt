package de.md5lukas.waypoints.pointers

import org.bukkit.Location
import java.util.*

open class BasicStaticTrackable(
    override val location: Location,
    override val beaconColor: BeaconColor? = null,
    override val hologramText: String? = null
) : StaticTrackable {
    override val id: UUID = UUID.randomUUID()
}