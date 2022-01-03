package de.md5lukas.waypoints.pointer

import de.md5lukas.waypoints.api.Waypoint
import org.bukkit.Location

data class ActivePointer(val waypoint: Waypoint, var translatedTarget: Location?)
