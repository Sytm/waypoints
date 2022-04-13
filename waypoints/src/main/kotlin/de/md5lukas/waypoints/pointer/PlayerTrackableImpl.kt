package de.md5lukas.waypoints.pointer

import de.md5lukas.waypoints.api.BeaconColor
import de.md5lukas.waypoints.api.PlayerTrackable
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

class PlayerTrackableImpl(private val player: Player) : PlayerTrackable {

    override val id: UUID = player.uniqueId
    override val location: Location
        get() = player.location
    override val beaconColor: BeaconColor? = null
}