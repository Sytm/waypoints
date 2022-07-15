package de.md5lukas.waypoints.pointer

import de.md5lukas.waypoints.api.Trackable
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

class PlayerTrackable(private val player: Player) : Trackable {

    override val id: UUID = player.uniqueId
    override val location: Location
        get() = player.location
}