package de.md5lukas.waypoints.util.protocol

import com.comphenix.protocol.reflect.accessors.Accessors
import com.comphenix.protocol.utility.MinecraftReflection
import de.md5lukas.waypoints.WaypointsPlugin
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.atomic.AtomicInteger

class ProtocolManager(internal val plugin: WaypointsPlugin) {

    private val entityId = Accessors.getFieldAccessor(MinecraftReflection.getEntityClass(), AtomicInteger::class.java, true)

    private val nextEntityId: Int
        get() = (entityId.get(null) as AtomicInteger).incrementAndGet()

    fun createHologram(player: Player, location: Location, text: String): Hologram {
        return Hologram(nextEntityId, player, location, text)
    }

    fun createFloatingItem(player: Player, location: Location, itemStack: ItemStack): FloatingItem {
        return FloatingItem(nextEntityId, player, location, itemStack)
    }
}