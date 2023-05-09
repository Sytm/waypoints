package de.md5lukas.waypoints.api.sqlite

import de.md5lukas.jdbc.select
import de.md5lukas.jdbc.selectFirst
import de.md5lukas.waypoints.api.Folder
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.base.DatabaseManager
import de.md5lukas.waypoints.api.gui.GUIType
import kotlinx.coroutines.withContext
import org.bukkit.Material
import org.bukkit.permissions.Permissible
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.*

class DeathFolderImpl(
    private val dm: DatabaseManager,
    override val owner: UUID,
) : Folder {

    override val id: UUID
        get() = owner
    override val createdAt: OffsetDateTime = OffsetDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC"))
    override val type: Type
        get() = Type.DEATH
    override var name: String
        get() = guiType.name
        set(_) = throw UnsupportedOperationException("Changing the name of the death folder is not supported")
    override var description: String?
        get() = null
        set(_) = throw UnsupportedOperationException("Changing the description of the death folder is not supported")
    override var material: Material?
        get() = null
        set(_) = throw UnsupportedOperationException("Changing the name of the death folder is not supported")

    override suspend fun getAmount(): Int = withContext(dm.asyncDispatcher) {
        dm.connection.selectFirst("SELECT COUNT(*) FROM waypoints WHERE type = ? AND owner = ?;", Type.DEATH.name, owner.toString()) {
            getInt(1)
        }!!
    }

    override suspend fun getAmountVisibleForPlayer(permissible: Permissible): Int = getAmount()

    override suspend fun getFolders(): List<Folder> = emptyList()
    override suspend fun getWaypoints(): List<Waypoint> = withContext(dm.asyncDispatcher) {
        dm.connection.select("SELECT * FROM waypoints WHERE type = ? AND owner = ?;", Type.DEATH.name, owner.toString()) {
            val id = UUID.fromString(this.getString("id"))
            dm.instanceCache.waypoints.get(id) {
                WaypointImpl(dm, this)
            }
        }
    }

    override suspend fun delete() {
        throw UnsupportedOperationException("Changing the name of the death folder is not supported")
    }

    override val guiType: GUIType = GUIType.DEATH_FOLDER

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Folder

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "DeathFolderImpl(owner=$owner)"
    }
}