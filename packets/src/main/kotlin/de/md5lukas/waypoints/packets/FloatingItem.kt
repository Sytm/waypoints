package de.md5lukas.waypoints.packets

import com.comphenix.protocol.utility.MinecraftReflection
import com.comphenix.protocol.wrappers.WrappedDataValue
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class FloatingItem(
    player: Player,
    location: Location,
    private val itemStack: ItemStack,
) : ClientSideEntity(
    player,
    location,
    EntityType.DROPPED_ITEM,
) {

    override fun modifyMetadataValues(spawn: Boolean, dataValues: MutableList<WrappedDataValue>) {
        if (spawn) {
            // https://wiki.vg/Entity_metadata#Item_Entity
            dataValues += disableGravity
            dataValues += WrappedDataValue(8, slotSerializer, MinecraftReflection.getMinecraftItemStack(itemStack))
        }
    }
}