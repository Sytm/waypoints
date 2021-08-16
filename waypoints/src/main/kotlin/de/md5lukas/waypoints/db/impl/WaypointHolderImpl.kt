package de.md5lukas.waypoints.db.impl

import de.md5lukas.jdbc.select
import de.md5lukas.jdbc.selectFirst
import de.md5lukas.jdbc.update
import de.md5lukas.waypoints.api.Folder
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.WaypointHolder
import de.md5lukas.waypoints.db.DatabaseManager
import org.bukkit.Location
import java.util.*

internal open class WaypointHolderImpl(
    internal val dm: DatabaseManager,
    private val type: Type,
    private val owner: UUID?,
) : WaypointHolder {

    override val folders: List<Folder>
        get() = if (owner == null) {
            dm.connection.select("SELECT * FROM folders WHERE type = ?;", type.name) {
                FolderImpl(dm, this)
            }
        } else {
            dm.connection.select("SELECT * FROM folders WHERE type = ? AND owner = ?;", type.name, owner.toString()) {
                FolderImpl(dm, this)
            }
        }

    override val topLevelWaypoints: List<Waypoint>
        get() = if (owner == null) {
            dm.connection.select("SELECT * FROM waypoints WHERE type = ? AND folder IS NULL;", type.name) {
                WaypointImpl(dm, this)
            }
        } else {
            dm.connection.select("SELECT * FROM waypoints WHERE type = ? AND owner = ? AND folder IS NULL;", type.name, owner.toString()) {
                WaypointImpl(dm, this)
            }
        }

    override val allWaypoints: List<Waypoint>
        get() = if (owner == null) {
            dm.connection.select("SELECT * FROM waypoints WHERE type = ?;", type.name) {
                WaypointImpl(dm, this)
            }
        } else {
            dm.connection.select("SELECT * FROM waypoints WHERE type = ? AND owner = ?;", type.name, owner.toString()) {
                WaypointImpl(dm, this)
            }
        }

    override fun createWaypoint(name: String, location: Location): Waypoint {
        return createWaypointTyped(name, location, type)
    }

    internal fun createWaypointTyped(name: String, location: Location, type: Type): Waypoint {
        val id = UUID.randomUUID()
        dm.connection.update(
            "INSERT INTO waypoints(id, type, owner, name, world, x, y, z) VALUES (?, ?, ?, ?, ?, ?, ?, ?);",
            id.toString(),
            type.name,
            owner.toString(),
            name,
            location.world!!.name,
            location.x,
            location.y,
            location.z,
        )
        return dm.connection.selectFirst("SELECT * FROM waypoints WHERE id = ?;", id.toString()) {
            WaypointImpl(dm, this)
        }!!
    }

    override fun createFolder(name: String): Folder {
        val id = UUID.randomUUID()
        dm.connection.update(
            "INSERT INTO folders(id, type, owner, name) VALUES (?, ?, ?, ?);",
            id.toString(),
            type.name,
            owner.toString(),
            name,
        )
        return dm.connection.selectFirst("SELECT * FROM folders WHERE id = ?", id.toString()) {
            FolderImpl(dm, this)
        }!!
    }
}