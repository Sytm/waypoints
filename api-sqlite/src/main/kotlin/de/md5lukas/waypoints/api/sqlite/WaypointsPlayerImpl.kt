package de.md5lukas.waypoints.api.sqlite

import de.md5lukas.jdbc.select
import de.md5lukas.jdbc.selectFirst
import de.md5lukas.jdbc.setValues
import de.md5lukas.jdbc.update
import de.md5lukas.waypoints.api.*
import de.md5lukas.waypoints.api.base.DatabaseManager
import org.bukkit.Location
import java.sql.ResultSet
import java.time.OffsetDateTime
import java.util.*

internal class WaypointsPlayerImpl private constructor(
    dm: DatabaseManager,
    override val id: UUID,
    showGlobals: Boolean,
    sortBy: OverviewSort,
    canBeTracked: Boolean,
) : WaypointHolderImpl(dm, Type.PRIVATE, id), WaypointsPlayer {

    constructor(dm: DatabaseManager, row: ResultSet) : this(
        dm = dm,
        id = UUID.fromString(row.getString("id")),
        showGlobals = row.getBoolean("showGlobals"),
        sortBy = OverviewSort.valueOf(row.getString("sortBy")),
        canBeTracked = row.getBoolean("canBeTracked"),
    )

    override var showGlobals: Boolean = showGlobals
        set(value) {
            field = value
            set("showGlobals", value)
        }
    override var sortBy: OverviewSort = sortBy
        set(value) {
            field = value
            set("sortBy", value.name)
        }
    override var canBeTracked: Boolean = canBeTracked
        set(value) {
            field = value
            set("canBeTracked", value)
        }

    override fun setCooldownUntil(type: Type, cooldownUntil: OffsetDateTime) {
        val until = cooldownUntil.toString()
        dm.connection.update(
            "INSERT INTO player_data_typed(playerId, type, cooldownUntil) VALUES (?, ?, ?) ON CONFLICT(playerId, type) DO UPDATE SET cooldownUntil = ?;",
            id.toString(),
            type.name,
            until,
            until,
        )
    }

    override fun getCooldownUntil(type: Type): OffsetDateTime? = dm.connection.selectFirst(
        "SELECT cooldownUntil FROM player_data_typed WHERE playerId = ? AND type = ?;",
        id.toString(),
        type.name,
    ) {
        OffsetDateTime.parse(getString("cooldownUntil"))
    }

    override fun setTeleportations(type: Type, teleportations: Int) {
        dm.connection.update(
            "INSERT INTO player_data_typed(playerId, type, teleportations) VALUES (?, ?, ?) ON CONFLICT(playerId, type) DO UPDATE SET teleportations = ?;",
            id.toString(),
            type.name,
            teleportations,
            teleportations,
        )
    }

    override fun getTeleportations(type: Type): Int = dm.connection.selectFirst(
        "SELECT teleportations FROM player_data_typed WHERE playerId = ? AND type = ?;",
        id.toString(),
        type.name,
    ) {
        getInt("teleportations")
    } ?: 0

    override fun addDeathLocation(location: Location) {
        super.createWaypointTyped("", location, Type.DEATH)
    }

    override val deathFolder: Folder = DeathFolderImpl(dm, id)
    override var compassTarget: Location?
        get() = dm.connection.selectFirst("SELECT * FROM compass_storage WHERE playerId = ?;", id.toString()) {
            Location(
                dm.plugin.server.getWorld(getString("world"))!!,
                getDouble("x"),
                getDouble("y"),
                getDouble("z"),
            )
        }
        set(value) {
            value?.let { location ->
                dm.connection.update(
                    "INSERT INTO compass_storage(playerId, world, x, y, z) VALUES (?, ?, ?, ?, ?) ON CONFLICT(playerId) DO UPDATE SET world = ?, x = ?, y = ?, z = ?;",
                    id.toString(),
                    location.world!!.name,
                    location.x,
                    location.y,
                    location.z,
                    location.world!!.name,
                    location.x,
                    location.y,
                    location.z,
                )
            }
        }

    override var selectedWaypoints: List<Waypoint>
        get() = dm.connection.select(
            "SELECT * FROM waypoints INNER JOIN selected_waypoints ON waypoints.id = selected_waypoints.waypointId WHERE selected_waypoints.playerId = ?;",
            id.toString()
        ) {
            getInt("index") to WaypointImpl(dm, this)
        }.apply { sortWith(compareBy { it.first }) }.map { it.second }
        set(value) {
            dm.connection.update("DELETE FROM selected_waypoints WHERE playerId = ?;", id.toString())
            dm.connection.prepareStatement("INSERT INTO selected_waypoints VALUES (?, ?, ?);").also {
                value.forEachIndexed { index, waypoint ->
                    it.setValues(id.toString(), waypoint.id.toString(), index)
                    it.addBatch()
                }
                it.executeBatch()
            }
        }

    private fun set(column: String, value: Any?) {
        dm.connection.update("UPDATE player_data SET $column = ? WHERE id = ?;", value, id)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WaypointsPlayer

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "WaypointsPlayerImpl(showGlobals=$showGlobals, sortBy=$sortBy, canBeTracked=$canBeTracked) ${super.toString()}"
    }
}