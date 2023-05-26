package de.md5lukas.waypoints.api.sqlite

import de.md5lukas.jdbc.select
import de.md5lukas.jdbc.selectFirst
import de.md5lukas.jdbc.update
import de.md5lukas.waypoints.api.Folder
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.base.DatabaseManager
import de.md5lukas.waypoints.api.event.FolderPostDeleteEvent
import de.md5lukas.waypoints.api.event.FolderPreDeleteEvent
import de.md5lukas.waypoints.api.gui.GUIType
import java.sql.ResultSet
import java.time.OffsetDateTime
import java.util.UUID
import kotlinx.coroutines.withContext
import org.bukkit.Material
import org.bukkit.permissions.Permissible

internal class FolderImpl
private constructor(
    private val dm: DatabaseManager,
    override val id: UUID,
    override val createdAt: OffsetDateTime,
    override val type: Type,
    override val owner: UUID?,
    name: String,
    description: String?,
    material: Material?,
) : Folder {

  constructor(
      dm: DatabaseManager,
      row: ResultSet
  ) : this(
      dm = dm,
      id = UUID.fromString(row.getString("id")),
      createdAt = OffsetDateTime.parse(row.getString("createdAt")),
      type = Type.valueOf(row.getString("type")),
      owner = row.getString("owner")?.let(UUID::fromString),
      name = row.getString("name"),
      description = row.getString("description"),
      material = row.getString("material")?.let { Material.valueOf(it) },
  )

  override var name: String = name
    private set

  override suspend fun setName(name: String) {
    this.name = name
    set("name", name)
  }

  override var description: String? = description
    private set

  override suspend fun setDescription(description: String?) {
    this.description = description
    set("description", description)
  }

  override var material: Material? = material
    private set

  override suspend fun setMaterial(material: Material?) {
    this.material = material
    set("material", material?.name)
  }

  override suspend fun getAmount(): Int =
      withContext(dm.asyncDispatcher) {
        dm.connection.selectFirst(
            "SELECT COUNT(*) FROM waypoints WHERE folder = ?;", id.toString()) {
              getInt(1)
            }!!
      }

  override suspend fun getAmountVisibleForPlayer(permissible: Permissible): Int =
      if (type == Type.PERMISSION) {
        withContext(dm.asyncDispatcher) {
          dm.connection
              .select("SELECT permission FROM main.waypoints WHERE folder = ?;", id.toString()) {
                getString("permission")
              }
              .count { permissible.hasPermission(it) }
        }
      } else {
        getAmount()
      }

  override suspend fun getFolders(): List<Folder> = emptyList()

  override suspend fun getWaypoints(): List<Waypoint> =
      withContext(dm.asyncDispatcher) {
        dm.connection.select("SELECT * FROM waypoints WHERE folder = ?;", id.toString()) {
          val id = UUID.fromString(this.getString("id"))
          dm.instanceCache.waypoints.get(id) { WaypointImpl(dm, this) }
        }
      }

  private suspend fun set(column: String, value: Any?) {
    withContext(dm.asyncDispatcher) {
      dm.connection.update("UPDATE folders SET $column = ? WHERE id = ?;", value, id.toString())
    }
  }

  override suspend fun delete(): Unit =
      withContext(dm.asyncDispatcher) {
        FolderPreDeleteEvent(!dm.testing, this@FolderImpl).callEvent()
        dm.connection.update("DELETE FROM folders WHERE id = ?", id.toString())
        FolderPostDeleteEvent(!dm.testing, this@FolderImpl).callEvent()
      }

  override val guiType: GUIType = GUIType.FOLDER

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Folder

    return id == other.id
  }

  override fun hashCode(): Int {
    return id.hashCode()
  }

  override fun toString(): String {
    return "FolderImpl(id=$id, type=$type, owner=$owner, name='$name')"
  }
}
