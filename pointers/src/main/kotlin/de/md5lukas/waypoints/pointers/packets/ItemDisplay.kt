package de.md5lukas.waypoints.pointers.packets

import com.comphenix.protocol.utility.MinecraftReflection
import com.comphenix.protocol.wrappers.WrappedDataValue
import org.bukkit.Location
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

internal class ItemDisplay(
    player: Player,
    location: Location,
    private val itemStack: ItemStack,
    translation: Vector,
) :
    DisplayEntity(
        player,
        location,
        EntityType.ITEM_DISPLAY,
        if (itemStack.type.isBlock) Display.Billboard.FIXED else Display.Billboard.CENTER,
        translation,
        if (itemStack.type.isBlock) null else Vector(0.6, 0.6, 0.6)) {

  override fun modifyMetadataValues(spawn: Boolean, dataValues: MutableList<WrappedDataValue>) {
    super.modifyMetadataValues(spawn, dataValues)
    if (spawn) {
      // https://wiki.vg/Entity_metadata#Item_Display
      dataValues +=
          WrappedDataValue(23, slotSerializer, MinecraftReflection.getMinecraftItemStack(itemStack))
      dataValues +=
          WrappedDataValue(24, byteSerializer, ItemDisplayTransform.FIXED.ordinal.toByte())
    }
  }
}
