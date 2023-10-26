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

  @JvmSynthetic suspend fun setShowGlobals(showGlobals: Boolean)

  fun setShowGlobalsCF(showGlobals: Boolean) = future { setShowGlobals(showGlobals) }

  /** The method to sort the items in GUI with */
  val sortBy: OverviewSort

  @JvmSynthetic suspend fun setSortBy(sortBy: OverviewSort)

  fun setSortByCF(sortBy: OverviewSort) = future { setSortBy(sortBy) }

  /** Whether the player wants to be trackable with player-tracking or not. */
  val canBeTracked: Boolean

  @JvmSynthetic suspend fun setCanBeTracked(canBeTracked: Boolean)

  fun setCanBeTrackedCF(canBeTracked: Boolean) = future { setCanBeTracked(canBeTracked) }

  /** Whether the player can receive temporary waypoints from other players or not */
  val canReceiveTemporaryWaypoints: Boolean

  @JvmSynthetic suspend fun setCanReceiveTemporaryWaypoints(canReceiveTemporaryWaypoints: Boolean)

  fun setCanReceiveTemporaryWaypointsCF(canReceiveTemporaryWaypoints: Boolean) = future {
    setCanReceiveTemporaryWaypoints(canReceiveTemporaryWaypoints)
  }

  val enabledPointers: Map<String, Boolean>

  @JvmSynthetic suspend fun setEnabledPointers(enabledPointers: Map<String, Boolean>)

  fun setEnabledPointersCF(enabledPointers: Map<String, Boolean>) = future {
    setEnabledPointers(enabledPointers)
  }

  fun isPointerEnabled(key: String) = enabledPointers.getOrDefault(key, true)

  @JvmSynthetic
  suspend fun setPointerEnabled(key: String, value: Boolean) {
    setEnabledPointers(enabledPointers.toMutableMap().also { it[key] = value })
  }

  fun setPointerEnabledCF(key: String, value: Boolean) = future { setPointerEnabled(key, value) }

  /**
   * Get the point in time at which a cooldown for teleporting has expired if present.
   *
   * @param type The type the cooldown is valid for
   * @return The time the cooldown expires or null
   */
  @JvmSynthetic suspend fun getCooldownUntil(type: Type): OffsetDateTime?

  fun getCooldownUntilCF(type: Type) = future { getCooldownUntil(type) }

  /**
   * Set a new cooldown for the player and the time at which it expires.
   *
   * @param type The type the cooldown is valid for
   * @param cooldownUntil The point in time at which the cooldown should expire
   */
  @JvmSynthetic suspend fun setCooldownUntil(type: Type, cooldownUntil: OffsetDateTime)

  fun setCooldownUntilCF(type: Type, cooldownUntil: OffsetDateTime) = future {
    setCooldownUntil(type, cooldownUntil)
  }

  /**
   * Get the amount of teleportations this player has performed to the given waypoint type
   *
   * @param type The type the counter is applicable to
   * @return The amount of teleportations
   */
  @JvmSynthetic suspend fun getTeleportations(type: Type): Int

  fun getTeleporationsCF(type: Type) = future { getTeleportations(type) }

  /**
   * Update the amount of teleportations the player has performed to the given waypoint type
   *
   * @param type The type the counter is applicable to
   * @param teleportations The new amount of teleportations
   */
  @JvmSynthetic suspend fun setTeleportations(type: Type, teleportations: Int)

  fun setTeleportationsCF(type: Type, teleportations: Int) = future {
    setTeleportations(type, teleportations)
  }

  /** Adds a new location to the death history of the player. */
  @JvmSynthetic suspend fun addDeathLocation(location: Location)

  fun addDeathLocationCF(location: Location) = future { addDeathLocation(location) }

  /**
   * Abstract folder representing the saved death history of the player. The only way to add
   * waypoints to it is by calling [addDeathLocation].
   *
   * The returned folder implementation behaves differently compared to a normal implementation.
   * - [Folder.id] is the id of the player
   * - [Folder.createdAt] is always at Epoch in the System-Timezone
   * - [Folder.name] cannot be changed
   * - [Folder.description] cannot be changed
   * - [Folder.icon] cannot be changed
   * - [Folder.delete] cannot be called
   */
  val deathFolder: Folder

  /** All the concurrent Waypoints the player has selected */
  @JvmSynthetic suspend fun getSelectedWaypoints(): List<Waypoint>

  fun getSelectedWaypointsCF() = future { getSelectedWaypoints() }

  @JvmSynthetic suspend fun setSelectedWaypoints(selected: List<Waypoint>)

  fun setSelectedWaypointsCF(selected: List<Waypoint>) = future { setSelectedWaypoints(selected) }

  /**
   * The compass target the player currently has before it got overwritten by the compass pointer
   */
  @JvmSynthetic suspend fun getCompassTarget(): Location?

  fun getCompassTargetCF() = future { getCompassTarget() }

  @JvmSynthetic suspend fun setCompassTarget(location: Location)

  fun setCompassTargetCF(location: Location) = future { setCompassTarget(location) }

  @JvmSynthetic suspend fun getSharingWaypoints(): List<WaypointShare>

  fun getSharingWaypointsCF() = future { getSharingWaypoints() }

  @JvmSynthetic suspend fun hasSharedWaypoints(): Boolean

  fun hasSharedWaypointsCF() = future { hasSharedWaypoints() }

  @JvmSynthetic suspend fun getSharedWaypoints(): List<WaypointShare>

  fun getSharedWaypointsCF() = future { getSharedWaypoints() }
}
