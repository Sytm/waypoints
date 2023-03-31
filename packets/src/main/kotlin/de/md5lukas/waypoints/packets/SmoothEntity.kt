package de.md5lukas.waypoints.packets

import com.comphenix.protocol.wrappers.WrappedDataValue
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

class SmoothEntity<T : ClientSideEntity>(
    player: Player,
    location: Location,
    val wrapped: T
) : ClientSideEntity(
    player,
    location,
    EntityType.ARMOR_STAND,
) {

    override fun modifyMetadataValues(spawn: Boolean, dataValues: MutableList<WrappedDataValue>) {
        if (spawn) {
            dataValues += disableGravity
            dataValues += WrappedDataValue(0, byteSerializer, (0x20).toByte()) // Make invisible
            // https://wiki.vg/Entity_metadata#Armor_Stand
            dataValues += WrappedDataValue(15, byteSerializer, (0x10).toByte()) // Make ArmorStand a marker
        }
    }

    init {
        passengers.add(wrapped)
    }

    override fun spawn() {
        wrapped.spawn()
        super.spawn()
    }

    override fun update() {
        super.update()
        wrapped.location = location
        wrapped.updateMetadata()
    }

    override fun destroy() {
        wrapped.destroy()
        super.destroy()
    }
}