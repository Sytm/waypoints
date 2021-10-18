package de.md5lukas.waypoints.legacy

import de.md5lukas.nbt.extended.UUIDTag
import de.md5lukas.nbt.tags.CompoundTag
import org.bukkit.Location
import org.bukkit.Material
import java.util.*

internal open class LegacyWaypoint(tag: CompoundTag) {

    val id: UUID = (tag.get("id") as UUIDTag).value()
    var name: String = tag.getString("name")
    val createdAt: Long = tag.getLong("createdAt")
    val location: Location = (tag.get("location") as LocationTag).value()
    var material: Material? = if ("material" in tag) {
        Material.valueOf(tag.getString("material"))
    } else null
    var beaconColor: LegacyBlockColor? = if ("beaconColor" in tag) {
        LegacyBlockColor.valueOf(tag.getString("beaconColor"))
    } else null

}

internal class LegacyDeathWaypoint(tag: CompoundTag) : LegacyWaypoint(tag)
internal class LegacyPrivateWaypoint(tag: CompoundTag) : LegacyWaypoint(tag)
internal class LegacyPublicWaypoint(tag: CompoundTag) : LegacyWaypoint(tag)
internal class LegacyPermissionWaypoint(tag: CompoundTag) : LegacyWaypoint(tag) {
    val permission: String = tag.getString("permission")
}