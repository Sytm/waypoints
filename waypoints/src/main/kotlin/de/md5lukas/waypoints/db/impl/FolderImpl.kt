package de.md5lukas.waypoints.db.impl

import de.md5lukas.commons.MathHelper
import de.md5lukas.jdbc.select
import de.md5lukas.jdbc.selectFirst
import de.md5lukas.jdbc.update
import de.md5lukas.waypoints.WaypointsPermissions
import de.md5lukas.waypoints.api.Folder
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.gui.GUIType
import de.md5lukas.waypoints.db.DatabaseManager
import de.md5lukas.waypoints.util.Formatters
import de.md5lukas.waypoints.util.runTaskAsync
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.sql.ResultSet
import java.time.OffsetDateTime
import java.util.*

internal class FolderImpl private constructor(
    private val dm: DatabaseManager,
    override val id: UUID,
    override val createdAt: OffsetDateTime,
    override val type: Type,
    override val owner: UUID?,
    name: String,
    description: String?,
    material: Material?,
) : Folder {

    constructor(dm: DatabaseManager, row: ResultSet) : this(
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
        set(value) {
            field = value
            set("name", value)
        }
    override var description: String? = description
        set(value) {
            field = value
            set("description", value)
        }
    override var material: Material? = material
        set(value) {
            field = value
            set("material", value?.name)
        }

    override val amount: Int
        get() = dm.connection.selectFirst("SELECT COUNT(*) FROM waypoints WHERE folder = ?;", id.toString()) {
            getInt(1)
        }!!

    override fun getAmountVisibleForPlayer(player: Player): Int =
        if (type == Type.PERMISSION && !player.hasPermission(WaypointsPermissions.MODIFY_PERMISSION)) {
            dm.connection.select("SELECT permission FROM main.waypoints WHERE folder = ?;", id.toString()) {
                getString("permission")
            }.count { player.hasPermission(it) }
        } else {
            amount
        }

    override val folders: List<Folder>
        get() = emptyList()

    override val waypoints: List<Waypoint>
        get() = dm.connection.select("SELECT * FROM waypoints WHERE folder = ?;", id.toString()) {
            val id = UUID.fromString(this.getString("id"))
            dm.instanceCache.waypoints.get(id) {
                WaypointImpl(dm, this)
            }
        }

    private fun set(column: String, value: Any?) {
        dm.plugin.runTaskAsync {
            dm.connection.update("UPDATE folders SET $column = ? WHERE id = ?;", value, id.toString())
        }
    }

    override fun delete() {
        dm.connection.update(
            "DELETE FROM folders WHERE id = ?",
            id.toString()
        )
    }

    override val guiType: GUIType = GUIType.FOLDER

    private val translations = dm.plugin.translations

    override fun getItem(player: Player): ItemStack {
        val fetchedAmount = getAmountVisibleForPlayer(player)
        val stack = when (type) {
            Type.PRIVATE -> translations.FOLDER_ICON_PRIVATE
            Type.PUBLIC -> translations.FOLDER_ICON_PUBLIC
            Type.PERMISSION -> translations.FOLDER_ICON_PERMISSION
            else -> throw IllegalStateException("An folder with the type $type should not exist")
        }.getItem(
            mapOf(
                "name" to name,
                "description" to (description ?: ""),
                "createdAt" to createdAt.format(Formatters.SHORT_DATE_TIME_FORMATTER),
                "amount" to fetchedAmount.toString()
            )
        )

        stack.amount = MathHelper.clamp(1, 64, fetchedAmount)

        material?.also {
            stack.type = it
        }

        return stack
    }
}