package de.md5lukas.waypoints.api

import java.util.*

/**
 * This interface provides the entry-point to use the Waypoints API.
 *
 * It can be obtained by using a ServiceProvider like this:
 * ```java
 * RegisteredServiceProvider<WaypointsAPI> rsp = Bukkit.getServicesManager().getRegistration(WaypointsAPI.class);
 * WaypointsAPI api = rsp.getProvider();
 * ```
 * or with Kotlin like this:
 * ```
 * val rsp = Bukkit.servicesManager.getRegistration(WaypointsAPI::class)
 * val api = api.provider
 * ```
 *
 * A very important note regarding this API is, that the default SQLite implementation will execute everything synchronous.
 */
interface WaypointsAPI {

    /**
     * Check if a player exists in the database.
     * For a player to exist [getWaypointPlayer] only has to be called once without doing anything else.
     *
     * @param uuid The UUID of the player
     * @return true if the player has been requested before
     */
    fun waypointsPlayerExists(uuid: UUID): Boolean

    /**
     * Get the player-profile for the matching UUID. If the player-profile has never been requested before, a new profile will be created with default values.
     *
     * @param uuid The UUID of the player
     * @return The player-profile
     */
    fun getWaypointPlayer(uuid: UUID): WaypointsPlayer

    /**
     * The abstract holder containing public waypoints and folders.
     */
    val publicWaypoints: WaypointHolder

    /**
     * The abstract holder containing permission waypoints and folders.
     */
    val permissionWaypoints: WaypointHolder

    /**
     * Retrieve a waypoint of any type with the given UUID from the database.
     *
     * @param uuid The UUID of the waypoint
     * @return The waypoint if it exists
     */
    fun getWaypointByID(uuid: UUID): Waypoint?

    /**
     * Get access to some interesting statistics of total waypoints and folders.
     */
    val statistics: Statistics
}