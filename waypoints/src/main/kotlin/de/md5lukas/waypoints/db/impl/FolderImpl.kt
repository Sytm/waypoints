package de.md5lukas.waypoints.db.impl

import de.md5lukas.commons.MathHelper
import de.md5lukas.jdbc.select
import de.md5lukas.jdbc.selectFirst
import de.md5lukas.jdbc.update
import de.md5lukas.waypoints.api.Folder
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.gui.GUIType
import de.md5lukas.waypoints.db.DatabaseManager
import de.md5lukas.waypoints.util.formatTimestampToDate
import de.md5lukas.waypoints.util.runTaskAsync
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.sql.ResultSet
import java.util.*

internal class FolderImpl private constructor(
    private val dm: DatabaseManager,
    override val id: UUID,
    override val createdAt: Long,
    override val type: Type,
    override val owner: UUID?,
    name: String,
    description: String?,
    material: Material?,
) : Folder {

    constructor(dm: DatabaseManager, row: ResultSet) : this(
        dm = dm,
        id = UUID.fromString(row.getString("id")),
        createdAt = row.getLong("createdAt"),
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
        if (type == Type.PERMISSION) {
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
            WaypointImpl(dm, this)
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

    private val itemTranslations = dm.plugin.translations

    override fun getItem(player: Player): ItemStack {
        val fetchedAmount = getAmountVisibleForPlayer(player)
        val stack = when (type) {
            Type.PRIVATE -> itemTranslations.FOLDER_ICON_PRIVATE
            Type.PUBLIC -> itemTranslations.FOLDER_ICON_PUBLIC
            Type.PERMISSION -> itemTranslations.FOLDER_ICON_PERMISSION
            else -> throw IllegalStateException("An folder with the type $type should not exist")
        }.getItem(
            mapOf(
                "name" to name,
                "description" to (description ?: ""),
                "createdAt" to createdAt.formatTimestampToDate(),
                "amount" to amount.toString()
            )
        )

        stack.amount = MathHelper.clamp(1, 64, fetchedAmount)

        material?.also {
            stack.type = it
        }

        return stack
    }
}