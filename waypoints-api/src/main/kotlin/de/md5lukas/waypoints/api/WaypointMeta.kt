package de.md5lukas.waypoints.api

import java.util.*

/**
 * The WaypointMeta interface provides the required functionality to save data that is unique between every waypoint and player,
 * like per-waypoint per-player teleportation counters.
 */
interface WaypointMeta {

    /**
     * The id of the [Waypoint] this instances belongs to
     */
    val waypoint: UUID

    /**
     * The id of the [WaypointsPlayer] this instance belongs to
     */
    val owner: UUID

    /**
     * The amount of teleportations the player has performed to this waypoint
     */
    var teleportations: Int

    /**
     * Whether the player has visited the location of this waypoint or not
     */
    var visited: Boolean
}