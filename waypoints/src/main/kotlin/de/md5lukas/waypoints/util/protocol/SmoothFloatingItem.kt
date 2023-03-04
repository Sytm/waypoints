package de.md5lukas.waypoints.util.protocol

import com.comphenix.protocol.wrappers.WrappedDataValue
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class SmoothFloatingItem(
    protocolManager: ProtocolManager,
    player: Player,
    location: Location,
    itemStack: ItemStack
) : ClientSideEntity(
    protocolManager,
    player,
    location,
    EntityType.ARMOR_STAND,
) {

    private val itemEntity = FloatingItem(protocolManager, player, location, itemStack)

    override val initialDataValues: MutableList<WrappedDataValue>
        get() = mutableListOf(
            WrappedDataValue(0, byteSerializer, (0x20).toByte()), // Make invisible
            // https://wiki.vg/Entity_metadata#Armor_Stand
            WrappedDataValue(15, byteSerializer, (0x10).toByte()), // Make ArmorStand a marker
        )

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