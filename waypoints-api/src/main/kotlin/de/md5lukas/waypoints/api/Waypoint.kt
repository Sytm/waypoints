package de.md5lukas.waypoints.api

import de.md5lukas.waypoints.api.gui.GUIDisplayable
import org.bukkit.Location
import org.bukkit.Material
import java.util.*

interface Waypoint : GUIDisplayable, StaticTrackable {

    override val id: UUID

    val owner: UUID?

    var folder: Folder?

    override var name: String

    var description: String?

    var permission: String?

    var material: Material?

    override var beaconColor: BeaconColor?

    override val location: Location

    fun getWaypointMeta(owner: UUID): WaypointMeta

    fun setCustomData(key: String, data: String?)

    fun getCustomData(key: String): String?

    fun delete()
}