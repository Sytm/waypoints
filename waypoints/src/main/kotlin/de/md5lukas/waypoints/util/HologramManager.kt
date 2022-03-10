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
        val allRegistryFields = registry.getFieldListByType(registryClass)

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
        val entityFlags = WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(java.lang.Byte::class.java))

        // Component serializer must be optional because the Custom name is of type OptChat
        val entityCustomName = WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true))
        val entityCustomNameVisibility = WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(java.lang.Boolean::class.java))
        val entityDisableGravity = WrappedDataWatcher.WrappedDataWatcherObject(5, WrappedDataWatcher.Registry.get(java.lang.Boolean::class.java))
    }

    private val wrappedText = Optional.of(WrappedChatComponent.fromLegacyText(text).handle)

    private val spawnPacket = PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING).also {
        it.integers.write(0, entityId)
        it.uuiDs.write(0, uuid)
        // Set type to armor stand
        it.integers.write(1, hologramManager.armorStandEntityTypeId)
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
            .write(2, 0)
            .write(3, 0)
            .write(4, 0)
    }

    private val metadataPacket = PacketContainer(PacketType.Play.Server.ENTITY_METADATA).also {
        it.integers.write(0, entityId)

        val metadata = WrappedDataWatcher()
        // https://wiki.vg/Entity_metadata#Entity
        metadata.setObject(entityFlags, (0x20).toByte()) // Make invisible
        metadata.setObject(entityCustomName, wrappedText)
        metadata.setObject(entityCustomNameVisibility, true)
        metadata.setObject(entityDisableGravity, true)

        it.watchableCollectionModifier.write(0, metadata.watchableObjects)
    }

    private val destroyPacket = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY).also {
        // TODO If Minecraft 1.19 comes out remove integer arrays
        if (isMinecraftVersionEqualOrLaterThan(hologramManager.plugin, 17)) {
            it.intLists.write(0, listOf(entityId))
        } else {
            it.integerArrays.write(0, intArrayOf(entityId))
        }
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