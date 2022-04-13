package de.md5lukas.waypoints.api.sqlite

import de.md5lukas.jdbc.selectFirst
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
    lastSelectedWaypoint: UUID?
) : WaypointHolderImpl(dm, Type.PRIVATE, id), WaypointsPlayer {

    constructor(dm: DatabaseManager, row: ResultSet) : this(
        dm = dm,
        id = UUID.fromString(row.getString("id")),
        showGlobals = row.getBoolean("showGlobals"),
        sortBy = OverviewSort.valueOf(row.getString("sortBy")),
        canBeTracked = row.getBoolean("canBeTracked"),
        lastSelectedWaypoint = row.getString("lastSelectedWaypoint")?.let(UUID::fromString),
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
            "INSERT INTO player_cooldown(playerId, type, cooldownUntil) VALUES (?, ?, ?) ON CONFLICT(playerId, type) DO UPDATE SET cooldownUntil = ?;",
            id.toString(),
            type.name,
            until,
            until,
        )
    }

    override fun getCooldownUntil(type: Type): OffsetDateTime? = dm.connection.selectFirst(
        "SELECT cooldownUntil FROM player_cooldown WHERE playerId = ? AND type = ?;",
        id.toString(),
        type.name,
    ) {
        OffsetDateTime.parse(getString("cooldownUntil"))
    }

    override fun addDeathLocation(location: Location) {
        super.createWaypointTyped("", location, Type.DEATH)
    }

    override val deathFolder: Folder = DeathFolderImpl(dm, id)

    override fun setCompassTarget(location: Location) {
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

    override fun getCompassTarget(): Location? = dm.connection.selectFirst("SELECT * FROM compass_storage WHERE playerId = ?;", id.toString()) {
        Location(
            dm.plugin.server.getWorld(getString("world"))!!,
            getDouble("x"),
            getDouble("y"),
            getDouble("z"),
        )
    }

    private var lastSelectedWaypointID = lastSelectedWaypoint
        set(value) {
            field = value
            set("lastSelectedWaypoint", value?.toString())
        }

    override var lastSelectedWaypoint: Waypoint?
        get() = lastSelectedWaypointID?.let { uuid ->
            dm.connection.selectFirst("SELECT * FROM waypoints WHERE id = ?;", uuid.toString()) {
                WaypointImpl(dm, this)
            }
        }
        set(value) {
            lastSelectedWaypointID = value?.id
        }

    private fun set(column: String, value: Any?) {
        dm.connection.update("UPDATE player_data SET $column = ? WHERE id = ?;", value, id)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WaypointsPlayer

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}