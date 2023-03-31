package de.md5lukas.waypoints.packets

import com.comphenix.protocol.utility.MinecraftReflection
import com.comphenix.protocol.wrappers.WrappedDataValue
import org.bukkit.Location
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

class ItemDisplay(
    player: Player,
    location: Location,
    private val itemStack: ItemStack,
    translation: Vector,
) : DisplayEntity(
    player, location,
    EntityType.ITEM_DISPLAY,
    if (itemStack.type.isBlock) Display.Billboard.FIXED else Display.Billboard.CENTER,
    translation,
) {
    override fun modifyMetadataValues(spawn: Boolean, dataValues: MutableList<WrappedDataValue>) {
        super.modifyMetadataValues(spawn, dataValues)
        if (spawn) {
            // https://wiki.vg/Entity_metadata#Item_Display
            dataValues += WrappedDataValue(22, slotSerializer, MinecraftReflection.getMinecraftItemStack(itemStack))
            dataValues += WrappedDataValue(23, byteSerializer, ItemDisplayTransform.FIXED.ordinal.toByte())
        }
    }
}