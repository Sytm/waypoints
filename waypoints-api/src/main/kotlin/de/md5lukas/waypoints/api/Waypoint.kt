package de.md5lukas.waypoints.api

import de.md5lukas.waypoints.api.gui.GUIDisplayable
import org.bukkit.Location
import org.bukkit.Material
import java.util.*

interface Waypoint : GUIDisplayable {

    /**
     * The unique ID of the waypoint.
     *
     * @see [WaypointsAPI.getWaypointByID] For getting any waypoint based on this ID
     */
    val id: UUID

    /**
     * The UUID of the owner of this waypoint if this waypoint is either of type [Type.PRIVATE] or a [Type.DEATH], null otherwise
     */
    val owner: UUID?

    /**
     * The folder this waypoint is inside, null otherwise
     */
    suspend fun getFolder(): Folder?

    suspend fun setFolder(folder: Folder?)

    /**
     * The name of the waypoint
     */
    override var name: String

    /**
     * The name of the waypoint, optionally prefixed with the folder name
     */
    suspend fun getFullPath(): String

    /**
     * The description of the waypoint, null if none has been provided
     */
    var description: String?

    /**
     * The required permission to see this waypoint. Required to be non-null if the waypoint is of type [Type.PERMISSION], null otherwise
     */
    var permission: String?

    /**
     * The optional customized material this waypoint should appear as in the GUI
     */
    var material: Material?

    var beaconColor: Material?

    /**
     * The location the waypoint has been created at
     */
    val location: Location

    /**
     * Get access to metadata unique for each player - waypoint combination.
     *
     * @param owner The UUID of the player to get the metadata for
     * @return The unique WaypointMeta instance
     */
    suspend fun getWaypointMeta(owner: UUID): WaypointMeta

    /**
     * Get custom data for this waypoint.
     *
     * @param key The key of the custom data
     * @return The data associated with the key
     */
    suspend fun getCustomData(key: String): String?

    /**
     * Set custom data for this waypoint.
     *
     * If [data] is null, the entry for the custom data is deleted.
     *
     * @param key The key of the custom data
     * @param data The data to save with the key
     */
    suspend fun setCustomData(key: String, data: String?)

    /**
     * Deletes this waypoint from the database.
     *
     * First a [de.md5lukas.waypoints.api.event.WaypointPreDeleteEvent] is triggered, in case you still need the waypoint.
     * After that the [de.md5lukas.waypoints.api.event.WaypointPostDeleteEvent] is triggered with the waypoint removed from the database.
     */
    suspend fun delete()
}