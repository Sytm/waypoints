package de.md5lukas.waypoints.api

import de.md5lukas.waypoints.api.gui.GUIDisplayable
import org.bukkit.Location
import org.bukkit.Material
import java.util.*

interface Waypoint : GUIDisplayable {

    val id: UUID

    override val createdAt: Long

    override val type: Type

    val owner: UUID?

    var folder: Folder?

    override var name: String

    var description: String?

    var permission: String?

    var material: Material?

    var beaconColor: BeaconColor?

    val location: Location

    fun getWaypointMeta(owner: UUID): WaypointMeta

    fun delete()
}