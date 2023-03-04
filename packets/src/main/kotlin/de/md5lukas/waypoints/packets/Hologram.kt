package de.md5lukas.waypoints.packets

import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataValue
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.*

class Hologram(
    player: Player,
    location: Location,
    var text: String
) : ClientSideEntity(
    player,
    location,
    EntityType.ARMOR_STAND,
) {

    private val wrappedText
        get() = Optional.of(WrappedChatComponent.fromLegacyText(text).handle)

    override fun modifyMetadataValues(spawn: Boolean, dataValues: MutableList<WrappedDataValue>) {
        dataValues += WrappedDataValue(2, optChatSerializer, wrappedText)
        if (spawn) {
            dataValues += disableGravity
            dataValues += WrappedDataValue(0, byteSerializer, (0x20).toByte()) // Make invisible
            dataValues += WrappedDataValue(3, booleanSerializer, true) // Nametag always visible
            // https://wiki.vg/Entity_metadata#Armor_Stand
            dataValues += WrappedDataValue(15, byteSerializer, (0x10).toByte()) // Make ArmorStand a marker
        }
    }
}