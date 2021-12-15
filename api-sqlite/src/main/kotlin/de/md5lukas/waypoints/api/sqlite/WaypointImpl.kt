package de.md5lukas.waypoints.api.sqlite

import de.md5lukas.jdbc.selectFirst
import de.md5lukas.jdbc.update
import de.md5lukas.waypoints.api.*
import de.md5lukas.waypoints.api.base.DatabaseManager
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
            folderId = value?.id
        }
    override var name: String = name
        set(value) {
            field = value
            set("name", value)
        }
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
}