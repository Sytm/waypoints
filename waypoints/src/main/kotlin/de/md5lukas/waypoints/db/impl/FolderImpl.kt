package de.md5lukas.waypoints.db.impl

import de.md5lukas.jdbc.select
import de.md5lukas.jdbc.update
import de.md5lukas.waypoints.api.Folder
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.db.DatabaseManager
import de.md5lukas.waypoints.util.runTaskAsync
import org.bukkit.Material
import java.sql.ResultSet
import java.util.*

internal class FolderImpl private constructor(
    private val dm: DatabaseManager,
    override val id: UUID,
    override val type: Type,
    override val owner: UUID?,
    name: String,
    description: String?,
    material: Material?,
) : Folder {

    constructor(dm: DatabaseManager, row: ResultSet) : this(
        dm = dm,
        id = UUID.fromString(row.getString("id")),
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

    override val waypoints: List<Waypoint>
        get() = dm.connection.select("SELECT * FROM waypoints WHERE folder = ?;", id) {
            WaypointImpl(dm, this)
        }

    private fun set(column: String, value: Any?) {
        dm.plugin.runTaskAsync {
            dm.connection.update("UPDATE folders SET $column = ? WHERE id = ?;", value, id)
        }
    }
}