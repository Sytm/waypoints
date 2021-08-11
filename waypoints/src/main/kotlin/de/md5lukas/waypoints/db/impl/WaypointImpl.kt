package de.md5lukas.waypoints.db.impl

import de.md5lukas.waypoints.jdbc.selectFirst
import de.md5lukas.waypoints.jdbc.update
import de.md5lukas.waypoints.api.*
import de.md5lukas.waypoints.db.DatabaseManager
import de.md5lukas.waypoints.util.runTaskAsync
import org.bukkit.Location
import org.bukkit.Material
import java.sql.ResultSet
import java.util.*

class WaypointImpl private constructor(
    private val dm: DatabaseManager,
    override val id: UUID,
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
            dm.connection.selectFirst("SELECT * FROM folders WHERE id = ?;", it) {
                FolderImpl(dm, this)
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
            set("permission", value)
        }
    override var beaconColor: BeaconColor? = beaconColor
        set(value) {
            field = value
            set("permission", value)
        }

    override fun getWaypointMeta(owner: UUID): WaypointMeta =
        dm.connection.selectFirst(
            "INSERT OR IGNORE INTO waypoint_meta(waypointId, playerId) VALUES (?, ?); SELECT * FROM waypoint_meta WHERE waypointId = ? AND playerId = ?;",
            id.toString(),
            owner.toString(),
            id.toString(),
            owner.toString(),
        ) {
            WaypointMetaImpl(dm, this)
        }!!

    private fun set(column: String, value: Any?) {
        dm.plugin.runTaskAsync {
            dm.connection.update("UPDATE waypoints SET $column = ? WHERE id = ?;", value, id)
        }
    }
}