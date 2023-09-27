package de.md5lukas.waypoints.pointers.packets

import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataValue
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

internal class TextDisplay(
    player: Player,
    location: Location,
    var text: Component,
    private val backgroundColor: Color? = null
) :
    DisplayEntity(
        player,
        location,
        EntityType.TEXT_DISPLAY,
        Display.Billboard.CENTER,
    ) {
  override fun modifyMetadataValues(spawn: Boolean, dataValues: MutableList<WrappedDataValue>) {
    super.modifyMetadataValues(spawn, dataValues)
    dataValues +=
        WrappedDataValue(
            23,
            chatSerializer,
            WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(text)).handle)
    if (spawn) {
      // https://wiki.vg/Entity_metadata#Text_Display
      if (backgroundColor !== null) {
        dataValues += WrappedDataValue(25, intSerializer, backgroundColor.asARGB())
      }
      dataValues +=
          WrappedDataValue(
              27,
              byteSerializer,
              if (backgroundColor === null) {
                    0x02 or
                        0x04 // If background color is null also set flag to use default background
                    // color
                  } else {
                    0x02 // Set flag that text can be seen through blocks
                  }
                  .toByte())
    }
  }
}
