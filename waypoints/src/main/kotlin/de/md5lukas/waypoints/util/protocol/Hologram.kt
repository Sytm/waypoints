package de.md5lukas.waypoints.util.protocol

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataValue
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.*

class Hologram(
    protocolManager: ProtocolManager,
    player: Player,
    location: Location,
    var text: String
) : ClientSideEntity(
    protocolManager,
    player,
    location,
    EntityType.ARMOR_STAND,
) {

    private val wrappedText
        get() = Optional.of(WrappedChatComponent.fromLegacyText(text).handle)

    override val initialDataValues: MutableList<WrappedDataValue>
        get() = mutableListOf(
            WrappedDataValue(0, byteSerializer, (0x20).toByte()), // Make invisible
            WrappedDataValue(2, optChatSerializer, wrappedText),
            WrappedDataValue(3, booleanSerializer, true), // Nametag always visible
            // https://wiki.vg/Entity_metadata#Armor_Stand
            WrappedDataValue(15, byteSerializer, (0x10).toByte()), // Make ArmorStand a marker
        )

    private val updateNamePacket
        get() = PacketContainer(PacketType.Play.Server.ENTITY_METADATA).also {
            it.integers.write(0, entityId)
            // https://wiki.vg/Entity_metadata#Entity
            it.dataValueCollectionModifier.write(
                0, listOf(
                    WrappedDataValue(2, optChatSerializer, wrappedText),
                )
            )
        }

    override fun update() {
        super.update()
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, updateNamePacket)
    }
}