package de.md5lukas.waypoints.pointers

import org.bukkit.Location
import org.bukkit.entity.Player

class PlayerTrackable(val player: Player) : Trackable {

    override val location: Location
        get() = player.location
}