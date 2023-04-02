package de.md5lukas.waypoints.api.sqlite

import de.md5lukas.jdbc.selectFirst
import de.md5lukas.jdbc.update
import de.md5lukas.waypoints.api.*
import de.md5lukas.waypoints.api.base.DatabaseManager
import de.md5lukas.waypoints.api.event.WaypointCustomDataChangeEvent
import de.md5lukas.waypoints.api.event.WaypointPostDeleteEvent
import de.md5lukas.waypoints.api.event.WaypointPreDeleteEvent
import de.md5lukas.waypoints.api.gui.GUIType
import de.md5lukas.waypoints.util.callEvent
import org.bukkit.Location
import org.bukkit.Material
import java.sql.ResultSet
import java.time.OffsetDateTime
import java.util.*

class WaypointImpl private constructor(
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
    beaconColor: BeaconColor?,
) : Waypoint {

    constructor(dm: DatabaseManager, row: ResultSet) : this(
        dm = dm,
        id = UUID.fromString(row.getString("id")),
        createdAt = OffsetDateTime.parse(row.getString("createdAt")),
        type = Type.valueOf(row.getString("type")),
        owner = row.getString("owner")?.let(UUID::fromString),
        location = Location(
            dm.plugin.server.getWorld(
                row.getString("world")
            ),
            row.getDouble("x"),
            row.getDouble("y"),
            row.getDouble("z"),
        ),
        folder = row.getString("folder")?.let(UUID::fromString),
        name = row.getString("name"),
        description = row.getString("description"),
        permission = row.getString("permission"),
        material = row.getString("material")?.let { Material.valueOf(it) },
        beaconColor = row.getString("beaconColor")?.let { BeaconColor.valueOf(it) }
    )

    private var folderId: UUID? = folder
        set(value) {
            field = value
            set("folder", value)
        }

    override var folder: Folder?
        get() = folderId?.let {
            dm.instanceCache.folders.get(it) {
                dm.connection.selectFirst("SELECT * FROM folders WHERE id = ?;", it) {
                    FolderImpl(dm, this)
                }
            }
        }
        set(value) {
            if (value !== null && value.type !== type) {
                throw IllegalArgumentException("The type of the folder (${value.type}) and the type of the waypoint ($type) does not match!")
            }
            folderId = value?.id
        }
    override var name: String = name
        set(value) {
            field = value
            set("name", value)
        }
    override val fullPath: String
        get() = folder?.let {
            "${it.name}/$name"
        } ?: name
    override var description: String? = description
        set(value) {
            field = value
            set("description", value)
        }
    override var permission: String? = permission
        set(value) {
            field = value
            set("permission", value)
        }
    override var material: Material? = material
        set(value) {
            field = value
            set("material", value?.name)
        }
    override var beaconColor: BeaconColor? = beaconColor
        set(value) {
            field = value
            set("beaconColor", value?.name)
        }

    override fun getWaypointMeta(owner: UUID): WaypointMeta {
        dm.connection.update(
            "INSERT OR IGNORE INTO waypoint_meta(waypointId, playerId) VALUES (?, ?);",
            id.toString(),
            owner.toString(),
        )
        return dm.connection.selectFirst(
            "SELECT * FROM waypoint_meta WHERE waypointId = ? AND playerId = ?;",
            id.toString(),
            owner.toString(),
        ) {
            WaypointMetaImpl(dm, this)
        }!!
    }

    override fun setCustomData(key: String, data: String?) {
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
        dm.plugin.callEvent(WaypointCustomDataChangeEvent(this, key, data))
    }

    override fun getCustomData(key: String): String? =
        dm.connection.selectFirst("SELECT data FROM waypoint_custom_data WHERE waypointId = ? AND key = ?;", id.toString(), key) {
            getString("data")
        }

    override fun delete() {
        dm.plugin.callEvent(WaypointPreDeleteEvent(this))
        dm.connection.update(
            "DELETE FROM waypoints WHERE id = ?",
            id.toString()
        )
        dm.plugin.callEvent(WaypointPostDeleteEvent(this))
    }

    private fun set(column: String, value: Any?) {
        dm.connection.update("UPDATE waypoints SET $column = ? WHERE id = ?;", value, id)
    }

    override val guiType: GUIType = GUIType.WAYPOINT

    override val hologramText: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Waypoint

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "WaypointImpl(id=$id, type=$type, owner=$owner, location=$location, fullPath='$fullPath')"
    }
}