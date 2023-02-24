package de.md5lukas.waypoints.util

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.reflect.FuzzyReflection
import com.comphenix.protocol.reflect.accessors.Accessors
import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract
import com.comphenix.protocol.utility.MinecraftReflection
import com.comphenix.protocol.wrappers.BukkitConverters
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataValue
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import de.md5lukas.waypoints.WaypointsPlugin
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class HologramManager(internal val plugin: WaypointsPlugin) {

    private val entityId = Accessors.getFieldAccessor(MinecraftReflection.getEntityClass(), AtomicInteger::class.java, true)

    private val nextEntityId: Int = (entityId.get(null) as AtomicInteger).incrementAndGet()

    fun createHologram(location: Location, text: String): Hologram {
        return Hologram(this, nextEntityId, UUID.randomUUID(), location, text)
    }

    internal val armorStandEntityTypeId: Int by lazy {
        val registryClass = MinecraftReflection.getIRegistry()
        val resourceKeyClass = MinecraftReflection.getResourceKey()
        val minecraftKeyClass = MinecraftReflection.getMinecraftKeyClass()

        val registry = FuzzyReflection.fromClass(registryClass)


        // Get the fields of every available registry type
        val allRegistryFields = if (isMinecraftVersionEqualOrLaterThan(plugin, 19)) {
            // This is technically unnecessary for MC 1.19+ because the EntityType finally works when used directly in the packet, I am going to leave
            // this here in case anyone stumbles upon it and needs it for newer versions
            FuzzyReflection.fromClass(MinecraftReflection.getBuiltInRegistries())
                .getFieldListByType(registryClass)
        } else {
            registry.getFieldListByType(registryClass)
        }

        // Accessor to get the resource key of a registry
        val registryGetResourceKey = Accessors.getMethodAccessor(
            registry.getMethod(
                FuzzyMethodContract.newBuilder()
                    .returnTypeExact(resourceKeyClass)
                    .parameterCount(0)
                    .build()
            )
        )

        // Accessor to get the minecraft key / namespaced key of the resource key
        val resourceKeyGetMinecraftKey = FuzzyReflection.fromClass(resourceKeyClass).getMethodList(
            FuzzyMethodContract.newBuilder()
                .returnTypeExact(minecraftKeyClass)
                .parameterCount(0)
                .build()
        ).map {
            Accessors.getMethodAccessor(it)
        }

        val entityTypeRegistry = Accessors.getFieldAccessor(allRegistryFields.first { registryInstanceField ->
            val accessor = Accessors.getFieldAccessor(registryInstanceField)
            val registryInstance = accessor.get(null)

            val resourceKey = registryGetResourceKey.invoke(registryInstance)

            val registryNames = resourceKeyGetMinecraftKey.map { it.invoke(resourceKey).toString() }

            "minecraft:entity_type" in registryNames
        }).get(null)

        val registryGetId = Accessors.getMethodAccessor(
            registry.getMethod(
                FuzzyMethodContract.newBuilder()
                    .parameterCount(1)
                    .returnTypeExact(Int::class.java /* primitive int type */)
                    .build()
            )
        )

        registryGetId.invoke(entityTypeRegistry, BukkitConverters.getEntityTypeConverter().getGeneric(EntityType.ARMOR_STAND)) as Int
    }
}

class Hologram(private val hologramManager: HologramManager, private val entityId: Int, private val uuid: UUID, val location: Location, val text: String) {

    private companion object DataWatchers {
        // https://wiki.vg/Entity_metadata#Entity
        // Must use native types of Byte and Boolean, but not the primitive types
        val byteSerializer: WrappedDataWatcher.Serializer = WrappedDataWatcher.Registry.get(java.lang.Byte::class.java)
        val booleanSerializer: WrappedDataWatcher.Serializer = WrappedDataWatcher.Registry.get(java.lang.Boolean::class.java)

        // Component serializer must be optional because the Custom name is of type OptChat
        val optChatSerializer: WrappedDataWatcher.Serializer = WrappedDataWatcher.Registry.getChatComponentSerializer(true)

        val entityFlags = WrappedDataWatcher.WrappedDataWatcherObject(0, byteSerializer)
        val entityCustomName = WrappedDataWatcher.WrappedDataWatcherObject(2, optChatSerializer)
        val entityCustomNameVisibility = WrappedDataWatcher.WrappedDataWatcherObject(3, booleanSerializer)
        val entityDisableGravity = WrappedDataWatcher.WrappedDataWatcherObject(5, booleanSerializer)
    }

    private val wrappedText = Optional.of(WrappedChatComponent.fromLegacyText(text).handle)

    private val spawnPacket =
        PacketContainer(
            // TODO remove with Minecraft 1.21
            if (isMinecraftVersionEqualOrLaterThan(hologramManager.plugin, 19)) {
                PacketType.Play.Server.SPAWN_ENTITY
            } else {
                @Suppress("DEPRECATION")
                PacketType.Play.Server.SPAWN_ENTITY_LIVING
            }
        ).also {
            it.integers.write(0, entityId)
            it.uuiDs.write(0, uuid)

            // I know this is horrible but it can be reverted again with MC 1.21
            var intFieldIndex = 1

            // Set type to armor stand
            if (isMinecraftVersionEqualOrLaterThan(hologramManager.plugin, 19)) {
                it.entityTypeModifier.write(0, EntityType.ARMOR_STAND)
            } else {
                it.integers.write(intFieldIndex++, hologramManager.armorStandEntityTypeId)
            }
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
                .write(intFieldIndex++, 0)
                .write(intFieldIndex++, 0)
                .write(intFieldIndex, 0)

        }

    private val metadataPacket = PacketContainer(PacketType.Play.Server.ENTITY_METADATA).also {
        it.integers.write(0, entityId)
        // https://wiki.vg/Entity_metadata#Entity
        if (isMinecraftVersionEqualOrLaterThan(hologramManager.plugin, 19)) {
            it.dataValueCollectionModifier.write(
                0, listOf(
                    WrappedDataValue(0, byteSerializer, (0x20).toByte()),
                    WrappedDataValue(2, optChatSerializer, wrappedText),
                    WrappedDataValue(3, booleanSerializer, true),
                    WrappedDataValue(5, booleanSerializer, true),
                )
            )
        } else {
            val metadata = WrappedDataWatcher()

            metadata.setObject(entityFlags, (0x20).toByte()) // Make invisible
            metadata.setObject(entityCustomName, wrappedText)
            metadata.setObject(entityCustomNameVisibility, true)
            metadata.setObject(entityDisableGravity, true)

            it.watchableCollectionModifier.write(0, metadata.watchableObjects)
        }
    }

    private val destroyPacket = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY).also {
        it.intLists.write(0, listOf(entityId))
    }

    fun spawn(player: Player) {
        ProtocolLibrary.getProtocolManager().let {
            it.sendServerPacket(player, spawnPacket)
            it.sendServerPacket(player, metadataPacket)
        }
    }

    fun destroy(player: Player) {
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, destroyPacket)
    }
}