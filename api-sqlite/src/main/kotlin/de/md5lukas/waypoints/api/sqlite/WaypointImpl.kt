package de.md5lukas.waypoints.api.sqlite

import de.md5lukas.jdbc.selectFirst
import de.md5lukas.jdbc.update
import de.md5lukas.waypoints.api.Folder
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.WaypointMeta
import de.md5lukas.waypoints.api.base.DatabaseManager
import de.md5lukas.waypoints.api.event.WaypointCustomDataChangeEvent
import de.md5lukas.waypoints.api.event.WaypointPostDeleteEvent
import de.md5lukas.waypoints.api.event.WaypointPreDeleteEvent
import de.md5lukas.waypoints.api.gui.GUIType
import de.md5lukas.waypoints.util.getUUID
import java.sql.ResultSet
import java.time.OffsetDateTime
import java.util.UUID
import kotlinx.coroutines.withContext
import org.bukkit.Location
import org.bukkit.Material

class WaypointImpl
private constructor(
    private val dm: DatabaseManager,
    override val id: UUID,
    override val createdAt: OffsetDateTime,
    override val type: Type,
    override val owner: UUID?,
    override val location: Location,
    folder: UUID?,
    name: String,
    description: String?,
    permission: String?,
    material: Material?,
    beaconColor: Material?,
) : Waypoint {

  constructor(
      dm: DatabaseManager,
      row: ResultSet
  ) : this(
      dm = dm,
      id = row.getUUID("id")!!,
      createdAt = OffsetDateTime.parse(row.getString("createdAt")),
      type = Type.valueOf(row.getString("type")),
      owner = row.getUUID("owner"),
      location =
          Location(
              dm.plugin.server.getWorld(row.getString("world")),
              row.getDouble("x"),
              row.getDouble("y"),
              row.getDouble("z"),
          ),
      folder = row.getUUID("folder"),
      name = row.getString("name"),
      description = row.getString("description"),
      permission = row.getString("permission"),
      material = row.getString("material")?.let { Material.valueOf(it) },
      beaconColor = row.getString("beaconColor")?.let { Material.valueOf(it) })

  private var folderId: UUID? = folder

  override suspend fun getFolder(): Folder? = folderId?.let { dm.api.getFolderByID(it)!! }

  override suspend fun setFolder(folder: Folder?) {
    if (folder !== null && folder.type !== type) {
      throw IllegalArgumentException(
          "The type of the folder (${folder.type}) and the type of the waypoint ($type) does not match!")
    }
    val folderId = folder?.id
    this.folderId = folderId
    set("folder", folderId)
  }

  override var name: String = name
    private set

  override suspend fun setName(name: String) {
    this.name = name
    set("name", name)
  }

  override suspend fun getFullPath(): String = getFolder()?.let { "${it.name}/$name" } ?: name

  override var description: String? = description
    private set

  override suspend fun setDescription(description: String?) {
    this.description = description
    set("description", description)
  }

  override var permission: String? = permission
    private set

  override suspend fun setPermission(permission: String) {
    if (type !== Type.PERMISSION) {
      throw IllegalArgumentException("Cannot set permission on non-permission waypoint")
    }
    this.permission = permission
    set("permission", permission)
  }

  override var material: Material? = material
    private set

  override suspend fun setMaterial(material: Material?) {
    this.material = material
    set("material", material?.name)
  }

  override var beaconColor: Material? = beaconColor
    private set

  override suspend fun setBeaconColor(beaconColor: Material?) {
    this.beaconColor = beaconColor
    set("beaconColor", beaconColor?.name)
  }

  override suspend fun getWaypointMeta(owner: UUID): WaypointMeta =
      withContext(dm.asyncDispatcher) {
        dm.connection.update(
            "INSERT OR IGNORE INTO waypoint_meta(waypointId, playerId) VALUES (?, ?);",
            id.toString(),
            owner.toString(),
        )
        dm.connection.selectFirst(
            "SELECT * FROM waypoint_meta WHERE waypointId = ? AND playerId = ?;",
            id.toString(),
            owner.toString(),
        ) {
          WaypointMetaImpl(dm, this)
        }!!
      }

  override suspend fun getCustomData(key: String): String? =
      withContext(dm.asyncDispatcher) {
        dm.connection.selectFirst(
            "SELECT data FROM waypoint_custom_data WHERE waypointId = ? AND key = ?;",
            id.toString(),
            key) {
              getString("data")
            }
      }

  override suspend fun setCustomData(key: String, data: String?): Unit =
      withContext(dm.asyncDispatcher) {
        if (data === null) {
          dm.connection.update(
              "DELETE FROM waypoint_custom_data WHERE waypointId = ? AND key = ?;",
              id.toString(),
              key,
          )
        } else {
          dm.connection.update(
              "INSERT INTO waypoint_custom_data(waypointId, key, data) VALUES (?, ?, ?) ON CONFLICT(waypointId, key) DO UPDATE SET data = ?;",
              id.toString(),
              key,
              data,
              data,
          )
        }
        WaypointCustomDataChangeEvent(!dm.testing, this@WaypointImpl, key, data).callEvent()
      }

  override suspend fun delete(): Unit =
      withContext(dm.asyncDispatcher) {
        WaypointPreDeleteEvent(!dm.testing, this@WaypointImpl).callEvent()
        dm.connection.update("DELETE FROM waypoints WHERE id = ?", id.toString())
        WaypointPostDeleteEvent(!dm.testing, this@WaypointImpl).callEvent()
      }

  private suspend fun set(column: String, value: Any?) {
    withContext(dm.asyncDispatcher) {
      dm.connection.update("UPDATE waypoints SET $column = ? WHERE id = ?;", value, id)
    }
  }

  override val guiType: GUIType = GUIType.WAYPOINT

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Waypoint

    return id == other.id
  }

  override fun hashCode(): Int {
    return id.hashCode()
  }

  override fun toString(): String {
    return "WaypointImpl(id=$id, type=$type, owner=$owner, location=$location, folder='$folderId')"
  }
}
