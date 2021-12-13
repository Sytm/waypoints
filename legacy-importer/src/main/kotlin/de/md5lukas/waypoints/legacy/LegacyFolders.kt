package de.md5lukas.waypoints.legacy

import de.md5lukas.nbt.extended.UUIDTag
import de.md5lukas.nbt.tags.CompoundTag
import org.bukkit.Material
import java.util.*

internal open class LegacyFolder(private val tag: CompoundTag, mapper: (tag: CompoundTag) -> LegacyWaypoint) {

    val id: UUID = (tag.get("id") as UUIDTag).value()
    var name: String = tag.getString("name")
    val createdAt: Long = tag.getLong("createdAt")
    var material: Material? = if ("material" in tag) {
        Material.valueOf(tag.getString("material"))
    } else null

    val waypoints: List<LegacyWaypoint> = tag.getList("waypoints").values().map { mapper(it as CompoundTag) }

}

internal class LegacyPrivateFolder(tag: CompoundTag) : LegacyFolder(tag, ::LegacyPrivateWaypoint)
internal class LegacyPublicFolder(tag: CompoundTag) : LegacyFolder(tag, ::LegacyPublicWaypoint)
internal class LegacyPermissionFolder(tag: CompoundTag) : LegacyFolder(tag, ::LegacyPermissionWaypoint)