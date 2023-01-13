package de.md5lukas.waypoints.api

import org.bukkit.Location
import java.time.OffsetDateTime
import java.util.*

/**
 * The WaypointsPlayer is an extensions of the WaypointHolder that allows processing player-specific data
 */
interface WaypointsPlayer : WaypointHolder {

    /**
     * The UUID of the player this profile belongs to
     */
    val id: UUID

    /**
     * Whether the player wants to see global waypoints in the GUI
     */
    var showGlobals: Boolean

    /**
     * The method to sort the items in GUI with
     */
    var sortBy: OverviewSort

    /**
     * Whether the player wants to be trackable with player-tracking or not.
     */
    var canBeTracked: Boolean

    /**
     * Get the point in time at which a cooldown for teleporting has expired if present.
     *
     * @param type The type the cooldown is valid for
     * @return The time the cooldown expires or null
     */
    fun getCooldownUntil(type: Type): OffsetDateTime?

    /**
     * Set a new cooldown for the player and the time at which it expires.
     *
     * @param type The type the cooldown is valid for
     * @param cooldownUntil The point in time at which the cooldown should expire
     */
    fun setCooldownUntil(type: Type, cooldownUntil: OffsetDateTime)

    /**
     * Get the amount of teleportations this player has performed to the given waypoint type
     *
     * @param type The type the counter is applicable to
     * @return The amount of teleportations
     */
    fun getTeleportations(type: Type): Int

    /**
     * Update the amount of teleportations the player has performed to the given waypoint type
     *
     * @param type The type the counter is applicable to
     * @param teleportations The new amount of teleportations
     */
    fun setTeleportations(type: Type, teleportations: Int)

    /**
     * Adds a new location to the death history of the player.
     */
    fun addDeathLocation(location: Location)

    /**
     * Abstract folder representing the saved death history of the player.
     * The only way to add waypoints to it is by calling [addDeathLocation].
     *
     * The returned folder implementation behaves differently compared to a normal implementation.
     * - [Folder.id] is the id of the player
     * - [Folder.createdAt] is always at Epoch in the System-Timezone
     * - [Folder.name] cannot be changed
     * - [Folder.description] cannot be changed
     * - [Folder.material] cannot be changed
     * - [Folder.delete] cannot be called
     */
    val deathFolder: Folder

    /**
     * The last waypoint the player got direction indicators for.
     * It is set to `null` if the player either reaches the waypoint or deselects it manually.
     * Only if the player disconnects it will be saved.
     *
     * @see [PointerManager]
     */
    var lastSelectedWaypoint: Waypoint?

    /**
     * Sets the compass target the player currently has before it got overwritten by the compass pointer
     *
     * @param location The location of the compass target to save
     */
    fun setCompassTarget(location: Location)

    /**
     * Gets the compass target the player had before it got overwritten by the compass pointer.
     *
     * @return The location that got saved or null
     */
    fun getCompassTarget(): Location?
}