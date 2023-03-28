package de.md5lukas.waypoints.packets

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.WrappedDataValue
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.abs

/*
 * Useful Links:
 * https://protocol.derklaro.dev/
 * https://wiki.vg
 */
open class ClientSideEntity(
    protected val player: Player,
    var location: Location,
    private val entityType: EntityType,
) {
    protected val entityId = Packets.nextEntityId

    protected val uuid: UUID = UUID.randomUUID()

    private var previousLocation = location

    protected companion object EntityMetadata {
        // https://wiki.vg/Entity_metadata#Entity
        // Must use native types of Byte and Boolean, but not the primitive types
        val byteSerializer: WrappedDataWatcher.Serializer = WrappedDataWatcher.Registry.get(java.lang.Byte::class.java)
        val intSerializer: WrappedDataWatcher.Serializer = WrappedDataWatcher.Registry.get(java.lang.Integer::class.java)
        val booleanSerializer: WrappedDataWatcher.Serializer = WrappedDataWatcher.Registry.get(java.lang.Boolean::class.java)
        val chatSerializer: WrappedDataWatcher.Serializer = WrappedDataWatcher.Registry.getChatComponentSerializer(false)
        val optChatSerializer: WrappedDataWatcher.Serializer = WrappedDataWatcher.Registry.getChatComponentSerializer(true)
        val slotSerializer: WrappedDataWatcher.Serializer = WrappedDataWatcher.Registry.getItemStackSerializer(false)

        val disableGravity = WrappedDataValue(5, booleanSerializer, true)
    }

    private val spawnPacket
        get() = PacketContainer(PacketType.Play.Server.SPAWN_ENTITY).also {
            it.integers.write(0, entityId)
            it.uuiDs.write(0, uuid)

            // Set type to armor stand
            it.entityTypeModifier.write(0, entityType)

            it.doubles
                .write(0, location.x)
                .write(1, location.y)
                .write(2, location.z)

            // Set angles to zero
            it.bytes
                .write(0, 0)
                .write(1, 0)
                .write(2, 0)

            // Set velocity to zero
            it.integers
                .write(1, 0)
                .write(2, 0)
                .write(3, 0)

        }

    protected open fun modifyMetadataValues(spawn: Boolean, dataValues: MutableList<WrappedDataValue>) {}

    private fun getMetadataPacket(spawn: Boolean): PacketContainer? {
        val values = mutableListOf<WrappedDataValue>()
        modifyMetadataValues(spawn, values)

        if (values.isEmpty())
            return null

        return PacketContainer(PacketType.Play.Server.ENTITY_METADATA).also {
            it.integers.write(0, entityId)
            it.dataValueCollectionModifier.write(
                0, values
            )
        }
    }

    private val movePacket
        get() = PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE).also {
            it.integers.write(0, entityId)

            // See: https://wiki.vg/Protocol#Update_Entity_Position
            it.shorts
                .write(0, calculateDelta(location.x, previousLocation.x))
                .write(1, calculateDelta(location.y, previousLocation.y))
                .write(2, calculateDelta(location.z, previousLocation.z))

            it.booleans.write(0, false)
        }

    private fun calculateDelta(current: Double, previous: Double): Short =
        ((current * 32 - previous * 32) * 128).toInt().toShort()

    private val teleportPacket
        get() = PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT).also {
            it.integers.write(0, entityId)

            it.doubles
                .write(0, location.x)
                .write(1, location.y)
                .write(2, location.z)

            // Set angles to zero
            it.bytes
                .write(0, 0)
                .write(1, 0)

            // Entity is not on ground
            it.booleans.write(0, false)
        }

    private val requiresTeleport
        get() = abs(location.x - previousLocation.x) > 8 || abs(location.z - previousLocation.z) > 8 || abs(location.y - previousLocation.y) > 8

    val passengers: MutableList<ClientSideEntity> = mutableListOf()
    private val passengersPacket
        get() = PacketContainer(PacketType.Play.Server.MOUNT).also {
            it.integers.write(0, entityId)

            it.integerArrays.write(0, passengers.map { it.entityId }.toIntArray())
        }

    private val destroyPacket = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY).also {
        it.intLists.write(0, listOf(entityId))
    }

    open fun spawn() {
        ProtocolLibrary.getProtocolManager().let {
            it.sendServerPacket(player, spawnPacket)
            getMetadataPacket(true)?.let { packet -> it.sendServerPacket(player, packet) }

            if (passengers.isNotEmpty()) {
                it.sendServerPacket(player, passengersPacket)
            }
        }
    }

    open fun update() {
        ProtocolLibrary.getProtocolManager().let {
            it.sendServerPacket(
                player, if (requiresTeleport) {
                    teleportPacket
                } else {
                    movePacket
                }
            )
            previousLocation = location
            getMetadataPacket(false)?.let { packet -> it.sendServerPacket(player, packet) }
        }
    }

    open fun destroy() {
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, destroyPacket)
    }
}