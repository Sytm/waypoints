package de.md5lukas.waypoints.util.protocol

import com.comphenix.protocol.utility.MinecraftReflection
import com.comphenix.protocol.wrappers.WrappedDataValue
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class FloatingItem(
    protocolManager: ProtocolManager,
    player: Player,
    location: Location,
    private val itemStack: ItemStack,
) : ClientSideEntity(
    protocolManager,
    player,
    location,
    EntityType.DROPPED_ITEM,
) {
    override val initialDataValues: MutableList<WrappedDataValue>
        get() = mutableListOf(
            // https://wiki.vg/Entity_metadata#Item_Entity
            WrappedDataValue(8, slotSerializer, MinecraftReflection.getMinecraftItemStack(itemStack))
        )
}