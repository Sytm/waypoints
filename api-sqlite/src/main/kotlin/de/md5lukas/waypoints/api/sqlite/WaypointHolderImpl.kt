package de.md5lukas.waypoints.api.sqlite

import de.md5lukas.jdbc.select
import de.md5lukas.jdbc.selectFirst
import de.md5lukas.jdbc.selectNotNull
import de.md5lukas.jdbc.update
import de.md5lukas.waypoints.api.*
import de.md5lukas.waypoints.api.base.DatabaseManager
import de.md5lukas.waypoints.api.event.FolderCreateEvent
import de.md5lukas.waypoints.api.event.WaypointCreateEvent
import de.md5lukas.waypoints.api.gui.GUIType
import de.md5lukas.waypoints.util.asSingletonList
import de.md5lukas.waypoints.util.callEvent
import org.bukkit.Location
import org.bukkit.permissions.Permissible
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

    override fun getWaypointsVisibleForPlayer(permissible: Permissible): Int =
        if (type == Type.PERMISSION) {
            dm.connection.select("SELECT permission FROM waypoints WHERE type = ?;", type.name) {
                getString("permission")
            }.count { permissible.hasPermission(it) }
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

    override fun searchFolders(query: String, permissible: Permissible?): List<SearchResult<out Folder>> {
        val (reducedQuery, taggedIndex) = prepareQuery(query)

        return dm.connection.selectNotNull<Folder>(
            "SELECT * FROM folders WHERE type = ? AND owner = ? AND name LIKE ? ESCAPE '!';",
            type.name,
            owner?.toString(),
            "$reducedQuery%",
        ) {
            FolderImpl(dm, this).also {
                if (permissible !== null && this@WaypointHolderImpl.type === Type.PERMISSION && it.getAmountVisibleForPlayer(permissible) == 0)
                    return@selectNotNull null
            }
        }.tagDuplicateSearchResults(Folder::name, reducedQuery, taggedIndex)
    }

    override fun searchWaypoints(query: String, permissible: Permissible?): List<SearchResult<out Waypoint>> {
        val typeString = type.name
        val ownerString = owner?.toString()

        val (reducedQuery, taggedIndex) = prepareQuery(query)

        return if ('/' in reducedQuery) {
            val result = reducedQuery.split('/', limit = 2)
            val folder = "${result[0]}%"
            val waypoint = "${result[1]}%"

            dm.connection.selectNotNull<Waypoint>(
                "SELECT * FROM waypoints WHERE type = ? AND owner IS ? AND name LIKE ? ESCAPE '!' AND folder IN (SELECT id FROM folders WHERE type = ? AND owner IS ? AND name LIKE ? ESCAPE '!');",
                typeString,
                ownerString,
                waypoint,
                typeString,
                ownerString,
                folder,
            ) {
                if (permissible !== null && this@WaypointHolderImpl.type === Type.PERMISSION && !permissible.hasPermission(getString("permission")))
                    return@selectNotNull null

                WaypointImpl(dm, this)
            }
        } else {
            dm.connection.selectNotNull<Waypoint>(
                "SELECT * FROM waypoints WHERE type = ? AND owner IS ? AND name LIKE ? ESCAPE '!';",
                typeString,
                ownerString,
                "$reducedQuery%",
            ) {
                if (permissible !== null && this@WaypointHolderImpl.type === Type.PERMISSION && !permissible.hasPermission(getString("permission")))
                    return@selectNotNull null

                WaypointImpl(dm, this)
            }
        }.tagDuplicateSearchResults(Waypoint::fullPath, reducedQuery, taggedIndex)
    }

    private fun prepareQuery(query: String): Pair<String, Int?> {
        val taggedIndex = query.substringAfterLast('#').toIntOrNull()

        val reducedQuery = if (taggedIndex === null) {
            query
        } else {
            query.substringBeforeLast('#')
        }.replace("!", "!!").replace("%", "!%").replace("_", "!_")

        return reducedQuery to taggedIndex
    }

    private fun <T> List<T>.tagDuplicateSearchResults(nameSelector: (T) -> String, reducedQuery: String, taggedIndex: Int?): List<SearchResult<out T>> {
        return this.groupBy(nameSelector).flatMap { (name, tList) ->
            if (taggedIndex !== null) {
                if (name != reducedQuery) {
                    return@flatMap emptyList()
                }
                tList.getOrNull(taggedIndex)?.let { t ->
                    return@flatMap SearchResult(t, "$name#$taggedIndex").asSingletonList()
                }
            }
            if (tList.size == 1) {
                SearchResult(tList[0], name).asSingletonList()
            } else {
                tList.mapIndexed { index, t ->
                    SearchResult(t, "$name#$index")
                }
            }
        }
    }

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

        return type == other.type
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }

    override fun toString(): String {
        return "WaypointHolderImpl(type=$type, owner=$owner)"
    }
}