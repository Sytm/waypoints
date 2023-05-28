package de.md5lukas.waypoints.api

import java.time.OffsetDateTime
import java.util.*
import org.bukkit.Location

/**
 * The WaypointsPlayer is an extensions of the WaypointHolder that allows processing player-specific
 * data
 */
interface WaypointsPlayer : WaypointHolder {

  /** The UUID of the player this profile belongs to */
  val id: UUID

  /** Whether the player wants to see global waypoints in the GUI */
  val showGlobals: Boolean

  suspend fun setShowGlobals(showGlobals: Boolean)

  /** The method to sort the items in GUI with */
  val sortBy: OverviewSort

  suspend fun setSortBy(sortBy: OverviewSort)

  /** Whether the player wants to be trackable with player-tracking or not. */
  val canBeTracked: Boolean

  suspend fun setCanBeTracked(canBeTracked: Boolean)

  val enabledPointers: Map<String, Boolean>

  suspend fun setEnabledPointers(enabledPointers: Map<String, Boolean>)

  fun isPointerEnabled(key: String) = enabledPointers.getOrDefault(key, true)

  suspend fun setPointerEnabled(key: String, value: Boolean) {
    setEnabledPointers(enabledPointers.toMutableMap().also { it[key] = value })
  }

  /**
   * Get the point in time at which a cooldown for teleporting has expired if present.
   *
   * @param type The type the cooldown is valid for
   * @return The time the cooldown expires or null
   */
  suspend fun getCooldownUntil(type: Type): OffsetDateTime?

  /**
   * Set a new cooldown for the player and the time at which it expires.
   *
   * @param type The type the cooldown is valid for
   * @param cooldownUntil The point in time at which the cooldown should expire
   */
  suspend fun setCooldownUntil(type: Type, cooldownUntil: OffsetDateTime)

  /**
   * Get the amount of teleportations this player has performed to the given waypoint type
   *
   * @param type The type the counter is applicable to
   * @return The amount of teleportations
   */
  suspend fun getTeleportations(type: Type): Int

  /**
   * Update the amount of teleportations the player has performed to the given waypoint type
   *
   * @param type The type the counter is applicable to
   * @param teleportations The new amount of teleportations
   */
  suspend fun setTeleportations(type: Type, teleportations: Int)

  /** Adds a new location to the death history of the player. */
  suspend fun addDeathLocation(location: Location)

  /**
   * Abstract folder representing the saved death history of the player. The only way to add
   * waypoints to it is by calling [addDeathLocation].
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
   * All the concurrent Waypoints the player has selected
   *
   * @see [de.md5lukas.waypoints.pointers.PointerManager]
   */
  suspend fun getSelectedWaypoints(): List<Waypoint>

  suspend fun setSelectedWaypoints(selected: List<Waypoint>)

  /**
   * The compass target the player currently has before it got overwritten by the compass pointer
   */
  suspend fun getCompassTarget(): Location?

  suspend fun setCompassTarget(location: Location)
}
