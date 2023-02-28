package de.md5lukas.waypoints.util

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.reflect.accessors.Accessors
import com.comphenix.protocol.utility.MinecraftReflection
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataValue
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import de.md5lukas.waypoints.WaypointsPlugin
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs

class HologramManager(internal val plugin: WaypointsPlugin) {

    private val entityId = Accessors.getFieldAccessor(MinecraftReflection.getEntityClass(), AtomicInteger::class.java, true)

    private val nextEntityId: Int
        get() = (entityId.get(null) as AtomicInteger).incrementAndGet()

    fun createHologram(player: Player, location: Location, text: String): Hologram {
        return Hologram(player, nextEntityId, UUID.randomUUID(), location, text)
    }
}

/*
 * Useful Links:
 * https://protocol.derklaro.dev/
 * https://wiki.vg
 */
class Hologram(private val player: Player, private val entityId: Int, private val uuid: UUID, var location: Location, var text: String) {

    private var previousLocation = location

    private companion object DataWatchers {
        // https://wiki.vg/Entity_metadata#Entity
        // Must use native types of Byte and Boolean, but not the primitive types
        val byteSerializer: WrappedDataWatcher.Serializer = WrappedDataWatcher.Registry.get(java.lang.Byte::class.java)
        val booleanSerializer: WrappedDataWatcher.Serializer = WrappedDataWatcher.Registry.get(java.lang.Boolean::class.java)

        // Component serializer must be optional because the Custom name is of type OptChat
        val optChatSerializer: WrappedDataWatcher.Serializer = WrappedDataWatcher.Registry.getChatComponentSerializer(true)
    }

    private val wrappedText
        get() = Optional.of(WrappedChatComponent.fromLegacyText(text).handle)

    private val spawnPacket
        get() = PacketContainer(PacketType.Play.Server.SPAWN_ENTITY).also {
            it.integers.write(0, entityId)
            it.uuiDs.write(0, uuid)

            // Set type to armor stand
            it.entityTypeModifier.write(0, EntityType.ARMOR_STAND)

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

    private val metadataPacket
        get() = PacketContainer(PacketType.Play.Server.ENTITY_METADATA).also {
            it.integers.write(0, entityId)
            it.dataValueCollectionModifier.write(
                0, listOf(
                    // https://wiki.vg/Entity_metadata#Entity
                    WrappedDataValue(0, byteSerializer, (0x20).toByte()), // Make invisible
                    WrappedDataValue(2, optChatSerializer, wrappedText),
                    WrappedDataValue(3, booleanSerializer, true),
                    WrappedDataValue(5, booleanSerializer, true),
                    // https://wiki.vg/Entity_metadata#Armor_Stand
                    WrappedDataValue(15, byteSerializer, (0x10).toByte()) // Make ArmorStand a marker
                )
            )
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

    private val destroyPacket = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY).also {
        it.intLists.write(0, listOf(entityId))
    }

    fun spawn() {
        ProtocolLibrary.getProtocolManager().let {
            it.sendServerPacket(player, spawnPacket)
            it.sendServerPacket(player, metadataPacket)
        }
    }

    init {
        spawn()
    }

    fun update() {
        ProtocolLibrary.getProtocolManager().let {
            it.sendServerPacket(player, updateNamePacket)
            it.sendServerPacket(
                player, if (requiresTeleport) {
                    teleportPacket
                } else {
                    movePacket
                }
            )
            previousLocation = location
        }
    }

    fun destroy() {
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, destroyPacket)
    }
}