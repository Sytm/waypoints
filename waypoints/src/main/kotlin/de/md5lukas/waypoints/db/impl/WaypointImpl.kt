package de.md5lukas.waypoints.db.impl

import de.md5lukas.commons.MathHelper
import de.md5lukas.jdbc.selectFirst
import de.md5lukas.jdbc.update
import de.md5lukas.waypoints.api.*
import de.md5lukas.waypoints.api.gui.GUIType
import de.md5lukas.waypoints.db.DatabaseManager
import de.md5lukas.waypoints.util.Formatters
import de.md5lukas.waypoints.util.format
import de.md5lukas.waypoints.util.runTaskAsync
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.sql.ResultSet
import java.time.ZonedDateTime
import java.util.*

class WaypointImpl private constructor(
    private val dm: DatabaseManager,
    override val id: UUID,
    override val createdAt: ZonedDateTime,
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
        createdAt = ZonedDateTime.parse(row.getString("createdAt")),
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
        dm.connection.update(
            "DELETE FROM waypoints WHERE id = ?",
            id.toString()
        )
    }

    private fun set(column: String, value: Any?) {
        dm.plugin.runTaskAsync {
            dm.connection.update("UPDATE waypoints SET $column = ? WHERE id = ?;", value, id)
        }
    }

    private val itemTranslations = dm.plugin.translations

    override val guiType: GUIType = GUIType.WAYPOINT

    override fun getItem(player: Player): ItemStack {
        val stack = when (type) {
            Type.DEATH -> itemTranslations.WAYPOINT_ICON_DEATH
            Type.PRIVATE -> itemTranslations.WAYPOINT_ICON_PRIVATE
            Type.PUBLIC -> itemTranslations.WAYPOINT_ICON_PUBLIC
            Type.PERMISSION -> itemTranslations.WAYPOINT_ICON_PERMISSION
            else -> throw IllegalStateException("An waypoint with the type $type should not exist")
        }.getItem(
            mapOf(
                "name" to name,
                "description" to (description ?: ""),
                "createdAt" to createdAt.format(Formatters.SHORT_DATE_TIME_FORMATTER),
                "world" to dm.plugin.worldTranslations.getWorldName(location.world!!),
                "x" to location.x.format(),
                "y" to location.y.format(),
                "z" to location.z.format(),
                "blockX" to location.blockX.toString(),
                "blockY" to location.blockY.toString(),
                "blockZ" to location.blockZ.toString(),
                "distance" to if (player.world == location.world) {
                    MathHelper.distance2D(player.location, location).format()
                } else {
                    dm.plugin.translations.TEXT_DISTANCE_OTHER_WORLD.text
                },
            )
        )

        material?.also {
            stack.type = it
        }

        return stack
    }
}