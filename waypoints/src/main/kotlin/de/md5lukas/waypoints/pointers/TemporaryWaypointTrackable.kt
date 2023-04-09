package de.md5lukas.waypoints.pointers

import net.kyori.adventure.text.Component
import org.bukkit.Location
import java.util.*

class TemporaryWaypointTrackable(
    override val location: Location,
    override val beaconColor: BeaconColor? = null
) : StaticTrackable {
    override val id: UUID = UUID.randomUUID()
    override val hologramText: Component? = null
}