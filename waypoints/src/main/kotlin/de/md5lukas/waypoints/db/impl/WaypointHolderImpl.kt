package de.md5lukas.waypoints.db.impl

import de.md5lukas.commons.MathHelper
import de.md5lukas.jdbc.select
import de.md5lukas.jdbc.selectFirst
import de.md5lukas.jdbc.update
import de.md5lukas.waypoints.api.Folder
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.WaypointHolder
import de.md5lukas.waypoints.api.gui.GUIType
import de.md5lukas.waypoints.db.DatabaseManager
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
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
            ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME),
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
        }!!
    }

    override fun createFolder(name: String): Folder {
        val id = UUID.randomUUID()
        dm.connection.update(
            "INSERT INTO folders(id, createdAt, type, owner, name) VALUES (?, ?, ?, ?, ?);",
            id.toString(),
            ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME),
            type.name,
            owner?.toString(),
            name,
        )
        return dm.connection.selectFirst("SELECT * FROM folders WHERE id = ?", id.toString()) {
            FolderImpl(dm, this)
        }!!
    }

    override val createdAt: ZonedDateTime = ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault())

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
        get() = when (type) {
            Type.PUBLIC -> dm.plugin.translations.ICON_PUBLIC.displayName
            Type.PERMISSION -> dm.plugin.translations.ICON_PERMISSION.displayName
            else -> throw IllegalStateException("A waypoint holder for a player cannot be a GUI item")
        }

    override fun getItem(player: Player): ItemStack {
        val amountVisibleToPlayer = getWaypointsVisibleForPlayer(player)

        val itemStack = when (type) {
            Type.PUBLIC -> dm.plugin.translations.ICON_PUBLIC.getItem(Collections.singletonMap("amount", amountVisibleToPlayer.toString()))
            Type.PERMISSION -> dm.plugin.translations.ICON_PERMISSION.getItem(Collections.singletonMap("amount", amountVisibleToPlayer.toString()))
            else -> throw IllegalStateException("A waypoint holder for a player cannot be a GUI item")
        }

        itemStack.amount = MathHelper.clamp(1, 64, amountVisibleToPlayer)

        return itemStack
    }
}