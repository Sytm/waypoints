package de.md5lukas.waypoints.api

import org.bukkit.entity.Player

interface PointerManager {

    fun enable(player: Player, waypoint: Waypoint)

    fun disable(player: Player)
}