package de.md5lukas.waypoints.api

import de.md5lukas.waypoints.api.gui.GUIDisplayable
import java.time.OffsetDateTime
import java.util.UUID
import org.bukkit.Location
import org.bukkit.Material

interface Waypoint : GUIDisplayable, Deletable {

  /**
   * The unique ID of the waypoint.
   *
   * @see [WaypointsAPI.getWaypointByID] For getting any waypoint based on this ID
   */
  val id: UUID

  /**
   * The UUID of the owner of this waypoint if this waypoint is either of type [Type.PRIVATE] or a
   * [Type.DEATH], null otherwise
   */
  val owner: UUID?

  /** The folder this waypoint is inside, null otherwise */
  @JvmSynthetic suspend fun getFolder(): Folder?

  fun getFolderCF() = future { getFolder() }

  @JvmSynthetic suspend fun setFolder(folder: Folder?)

  fun setFolderCF(folder: Folder?) = future { setFolder(folder) }

  /** The name of the waypoint */
  override val name: String

  @JvmSynthetic suspend fun setName(name: String)

  fun setNameCF(name: String) = future { setName(name) }

  /** The name of the waypoint, optionally prefixed with the folder name */
  @JvmSynthetic suspend fun getFullPath(): String

  fun getFullPathCF() = future { getFullPath() }

  /** The description of the waypoint, null if none has been provided */
  val description: String?

  @JvmSynthetic suspend fun setDescription(description: String?)

  fun setDescriptionCF(description: String?) = future { setDescription(description) }

  /**
   * The required permission to see this waypoint. Required to be non-null if the waypoint is of
   * type [Type.PERMISSION], null otherwise
   */
  val permission: String?

  @JvmSynthetic suspend fun setPermission(permission: String)

  fun setPermissionCF(permission: String) = future { setPermission(permission) }

  /** The optional customized material this waypoint should appear as in the GUI */
  val icon: Icon?

  @JvmSynthetic suspend fun setIcon(icon: Icon?)

  fun setIconCF(icon: Icon?) = future { setIcon(icon) }

  val beaconColor: Material?

  @JvmSynthetic suspend fun setBeaconColor(beaconColor: Material?)

  fun setBeaconColorCF(beaconColor: Material?) = future { setBeaconColor(beaconColor) }

  /** The location the waypoint has been created at */
  val location: Location

  /**
   * Get access to metadata unique for each player - waypoint combination.
   *
   * @param owner The UUID of the player to get the metadata for
   * @return The unique WaypointMeta instance
   */
  @JvmSynthetic suspend fun getWaypointMeta(owner: UUID): WaypointMeta

  fun getWaypointMetaCF(owner: UUID) = future { getWaypointMeta(owner) }

  /**
   * Get custom data for this waypoint.
   *
   * @param key The key of the custom data
   * @return The data associated with the key
   */
  @JvmSynthetic suspend fun getCustomData(key: String): String?

  fun getCustomDataCF(key: String) = future { getCustomData(key) }

  /**
   * Set custom data for this waypoint.
   *
   * If [data] is null, the entry for the custom data is deleted.
   *
   * @param key The key of the custom data
   * @param data The data to save with the key
   */
  @JvmSynthetic suspend fun setCustomData(key: String, data: String?)

  fun setCustomDataCF(key: String, data: String?) = future { setCustomData(key, data) }

  @JvmSynthetic suspend fun shareWith(with: UUID, expires: OffsetDateTime? = null)

  fun shareWithCF(with: UUID, expires: OffsetDateTime?) = future { shareWith(with, expires) }

  @JvmSynthetic suspend fun getSharedWith(): List<WaypointShare>

  fun getSharedWithCF() = future { getSharedWith() }

  /**
   * Deletes this waypoint from the database.
   *
   * First a [de.md5lukas.waypoints.api.event.WaypointPreDeleteEvent] is triggered, in case you
   * still need the waypoint. After that the
   * [de.md5lukas.waypoints.api.event.WaypointPostDeleteEvent] is triggered with the waypoint
   * removed from the database.
   */
  @JvmSynthetic override suspend fun delete()
}
