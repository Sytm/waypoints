package de.md5lukas.waypoints.api

import org.bukkit.Material
import java.util.*

interface Folder {

    val id: UUID

    val type: Type

    val owner: UUID?

    var name: String

    var description: String?

    var material: Material?

    val waypoints: List<Waypoint>
}