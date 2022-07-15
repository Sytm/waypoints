package de.md5lukas.waypoints.api.sqlite

import de.md5lukas.jdbc.select
import de.md5lukas.jdbc.selectFirst
import de.md5lukas.jdbc.update
import de.md5lukas.waypoints.api.Folder
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.WaypointHolder
import de.md5lukas.waypoints.api.base.DatabaseManager
import de.md5lukas.waypoints.api.event.FolderCreateEvent
import de.md5lukas.waypoints.api.event.WaypointCreateEvent
import de.md5lukas.waypoints.api.gui.GUIType
import de.md5lukas.waypoints.util.callEvent
import org.bukkit.Location
import org.bukkit.entity.Player
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.*

internal open class WaypointHolderImpl(
    internal val dm: DatabaseManager,
    override val type: Type,
    private val owner: UUID?,
) : WaypointHolder {

    override val folders: List<Folder>
        get() = dm.connection.select("SELECT * FROM folders WHERE type = ? AND owner IS ?;", type.name, owner?.toString()) {
            val id = UUID.fromString(this.getString("id"))
            dm.instanceCache.folders.get(id) {
                FolderImpl(dm, this)
            }
        }

    override val waypoints: List<Waypoint>
        get() = dm.connection.select("SELECT * FROM waypoints WHERE type = ? AND owner IS ? AND folder IS NULL;", type.name, owner?.toString()) {
            val id = UUID.fromString(this.getString("id"))
            dm.instanceCache.waypoints.get(id) {
                WaypointImpl(dm, this)
            }
        }

    override val allWaypoints: List<Waypoint>
        get() = dm.connection.select("SELECT * FROM waypoints WHERE type = ? AND owner IS ?;", type.name, owner?.toString()) {
            val id = UUID.fromString(this.getString("id"))
            dm.instanceCache.waypoints.get(id) {
                WaypointImpl(dm, this)
            }
        }

    override val waypointsAmount: Int
        get() = dm.connection.selectFirst("SELECT COUNT(*) FROM waypoints WHERE type = ? AND owner IS ?;", type.name, owner?.toString()) {
            getInt(1)
        }!!

    override val foldersAmount: Int
        get() = dm.connection.selectFirst("SELECT COUNT(*) FROM folders WHERE type = ? AND owner IS ?;", type.name, owner?.toString()) {
            getInt(1)
        }!!

    override fun getWaypointsVisibleForPlayer(player: Player): Int =
        if (type == Type.PERMISSION) {
            dm.connection.select("SELECT permission FROM waypoints WHERE type = ?;", type.name) {
                getString("permission")
            }.count { player.hasPermission(it) }
        } else {
            waypointsAmount
        }

    override fun createWaypoint(name: String, location: Location): Waypoint {
        return createWaypointTyped(name, location, type)
    }

    internal fun createWaypointTyped(name: String, location: Location, type: Type): Waypoint {
        val id = UUID.randomUUID()
        dm.connection.update(
            "INSERT INTO waypoints(id, createdAt, type, owner, name, world, x, y, z) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);",
            id.toString(),
            OffsetDateTime.now().toString(),
            type.name,
            owner?.toString(),
            name,
            location.world!!.name,
            location.x,
            location.y,
            location.z,
        )
        return dm.connection.selectFirst("SELECT * FROM waypoints WHERE id = ?;", id.toString()) {
            WaypointImpl(dm, this)
        }!!.also {
            dm.plugin.callEvent(WaypointCreateEvent(it))
        }
    }

    override fun createFolder(name: String): Folder {
        val id = UUID.randomUUID()
        dm.connection.update(
            "INSERT INTO folders(id, createdAt, type, owner, name) VALUES (?, ?, ?, ?, ?);",
            id.toString(),
            OffsetDateTime.now().toString(),
            type.name,
            owner?.toString(),
            name,
        )
        return dm.connection.selectFirst("SELECT * FROM folders WHERE id = ?", id.toString()) {
            FolderImpl(dm, this)
        }!!.also {
            dm.plugin.callEvent(FolderCreateEvent(it))
        }
    }

    override val createdAt: OffsetDateTime = OffsetDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC"))

    override fun isDuplicateWaypointName(name: String): Boolean = dm.connection.selectFirst(
        "SELECT EXISTS(SELECT 1 FROM waypoints WHERE type = ? AND owner IS ? AND name = ? COLLATE NOCASE);",
        type.name,
        owner?.toString(),
        name,
    ) {
        getInt(1) == 1
    } ?: false

    override fun isDuplicateFolderName(name: String): Boolean = dm.connection.selectFirst(
        "SELECT EXISTS(SELECT 1 FROM folders WHERE type = ? AND owner IS ? AND name = ? COLLATE NOCASE);",
        type.name,
        owner?.toString(),
        name,
    ) {
        getInt(1) == 1
    } ?: false

    override val guiType: GUIType
        get() = when (type) {
            Type.PUBLIC -> GUIType.PUBLIC_HOLDER
            Type.PERMISSION -> GUIType.PERMISSION_HOLDER
            else -> throw IllegalStateException("A waypoint holder for a player cannot be a GUI item")
        }

    override val name: String
        get() = guiType.name

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WaypointHolder

        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }
}