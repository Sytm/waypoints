package de.md5lukas.waypoints.pointers

import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

class PlayerTrackable(val player: Player) : Trackable {

    override val id: UUID = player.uniqueId
    override val location: Location
        get() = player.location

    override val hologramText: String? = null
}