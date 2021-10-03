package de.md5lukas.waypoints.api

import de.md5lukas.waypoints.api.gui.GUIFolder
import org.bukkit.Location
import org.bukkit.entity.Player

interface WaypointHolder : GUIFolder {

    override val folders: List<Folder>

    override val waypoints: List<Waypoint>

    val allWaypoints: List<Waypoint>

    val waypointsAmount: Int

    val foldersAmount: Int

    fun getWaypointsVisibleForPlayer(player: Player): Int

    fun createWaypoint(name: String, location: Location): Waypoint

    fun createFolder(name: String): Folder

    fun isDuplicateWaypointName(name: String): Boolean

    fun isDuplicateFolderName(name: String): Boolean
}