package de.md5lukas.waypoints.packets

import com.comphenix.protocol.wrappers.WrappedDataValue
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class SmoothFloatingItem(
    player: Player,
    location: Location,
    itemStack: ItemStack
) : ClientSideEntity(
    player,
    location,
    EntityType.ARMOR_STAND,
) {

    private val itemEntity = FloatingItem(player, location, itemStack)

    override fun modifyMetadataValues(spawn: Boolean, dataValues: MutableList<WrappedDataValue>) {
        if (spawn) {
            dataValues += disableGravity
            dataValues += WrappedDataValue(0, byteSerializer, (0x20).toByte()) // Make invisible
            // https://wiki.vg/Entity_metadata#Armor_Stand
            dataValues += WrappedDataValue(15, byteSerializer, (0x10).toByte()) // Make ArmorStand a marker
        }
    }

    init {
        passengers.add(itemEntity)
    }

    override fun spawn() {
        itemEntity.spawn()
        super.spawn()
    }

    override fun update() {
        super.update()
        itemEntity.location = location
    }

    override fun destroy() {
        itemEntity.destroy()
        super.destroy()
    }
}