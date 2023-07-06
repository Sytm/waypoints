package de.md5lukas.waypoints.api

import de.md5lukas.waypoints.api.gui.GUIFolder
import java.util.*
import org.bukkit.Material
import org.bukkit.permissions.Permissible

interface Folder : GUIFolder {

  /** The unique ID of the folder */
  val id: UUID

  /**
   * The UUID of the owner of this folder if this folder is of the type [Type.PRIVATE], null
   * otherwise
   */
  val owner: UUID?

  /** The name of the folder */
  override val name: String

  @JvmSynthetic suspend fun setName(name: String)

  /** The description of the folder, null if none has been provided */
  val description: String?

  @JvmSynthetic suspend fun setDescription(description: String?)

  /** The optional customized material this folder should appear as in the GUI */
  val material: Material?

  @JvmSynthetic suspend fun setMaterial(material: Material?)

  /** The total amount of waypoints in this folder. */
  @JvmSynthetic suspend fun getAmount(): Int

  /**
   * The amount of waypoints the player has permission to see in this folder if the type is
   * [Type.PERMISSION]. Otherwise, identical to [getAmount]
   *
   * @param permissible The permissible to check the permissions against
   * @return The amount of waypoints visible for the player
   */
  @JvmSynthetic suspend fun getAmountVisibleForPlayer(permissible: Permissible): Int

  /**
   * Deletes this folder from the database.
   *
   * First a [de.md5lukas.waypoints.api.event.FolderPreDeleteEvent] is triggered, in case you still
   * need the folder. After that the [de.md5lukas.waypoints.api.event.FolderPostDeleteEvent] is
   * triggered with the waypoint removed from the database.
   */
  @JvmSynthetic suspend fun delete()
}
