package de.md5lukas.waypoints.packets

import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataValue
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

class TextDisplay(
    player: Player,
    location: Location,
    var text: String,
    private val backgroundColor: Color? = null
) : DisplayEntity(
    player,
    location,
    EntityType.TEXT_DISPLAY,
    Display.Billboard.CENTER,
) {
    override fun modifyMetadataValues(spawn: Boolean, dataValues: MutableList<WrappedDataValue>) {
        super.modifyMetadataValues(spawn, dataValues)
        dataValues += WrappedDataValue(22, chatSerializer, WrappedChatComponent.fromLegacyText(text).handle)
        if (spawn) {
            // https://wiki.vg/Entity_metadata#Text_Display
            if (backgroundColor !== null) {
                dataValues += WrappedDataValue(24, intSerializer, backgroundColor.asARGB())
            }
            dataValues += WrappedDataValue(
                26, byteSerializer, if (backgroundColor === null) {
                    0x03 // If background color is null also set flag to use defaul background color
                } else {
                    0x02 // Set flag that text can be seen through blocks
                }.toByte()
            )
        }
    }
}