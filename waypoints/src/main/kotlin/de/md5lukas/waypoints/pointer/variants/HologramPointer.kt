package de.md5lukas.waypoints.pointer.variants

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Trackable
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.config.pointers.HologramConfiguration
import de.md5lukas.waypoints.packets.Hologram
import de.md5lukas.waypoints.packets.SmoothFloatingItem
import de.md5lukas.waypoints.pointer.PlayerTrackable
import de.md5lukas.waypoints.pointer.Pointer
import de.md5lukas.waypoints.pointer.TemporaryWaypointTrackable
import de.md5lukas.waypoints.util.minus
import de.md5lukas.waypoints.util.placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.math.roundToLong

class HologramPointer(
    plugin: WaypointsPlugin,
    private val config: HologramConfiguration
) : Pointer(plugin, config.interval) {

    private val activeHolograms: MutableMap<UUID, Pair<Hologram, SmoothFloatingItem?>> = HashMap()

    override fun update(player: Player, trackable: Trackable, translatedTarget: Location?) {
        if (translatedTarget === null)
            return

        val hologramText = when (trackable) {
            is Waypoint -> plugin.apiExtensions.run { trackable.getHologramTranslations() }
            is TemporaryWaypointTrackable -> plugin.translations.POINTERS_HOLOGRAM_TEMPORARY
            is PlayerTrackable -> plugin.translations.POINTERS_HOLOGRAM_PLAYER_TRACKING
            else -> null
        }?.withReplacements(*trackable.getReplacements(player)) ?: trackable.hologramText?.let { LegacyComponentSerializer.legacySection().deserialize(it) }

        if (hologramText === null)
            return

        val hologramTarget = translatedTarget.clone()

        hologramTarget.y += config.hologramHeightOffset

        val distanceSquared = player.location.distanceSquared(translatedTarget)
        val maxDistance = config.distanceFromPlayerSquared

        val location = if (distanceSquared <= maxDistance) {
            hologramTarget
        } else {
            val pVec = player.eyeLocation.toVector()
            val tVec = hologramTarget.toVector()

            val direction = (tVec - pVec).normalize()

            val rayTrace = if (config.preventOcclusion) {
                player.world.rayTraceBlocks(player.eyeLocation, direction, config.distanceFromPlayer.toDouble(), FluidCollisionMode.NEVER, true)
            } else null

            if (rayTrace !== null) {
                // Move the hologram half a block closer to the player
                rayTrace.hitPosition.add(direction.multiply(-0.5))
            } else {
                val offset = direction.multiply(config.distanceFromPlayer)

                pVec.add(offset)
            }.toLocation(player.world)
        }

        if (player.uniqueId in activeHolograms) {
            val (hologram, item) = activeHolograms[player.uniqueId]!!

            hologram.location = location
            hologram.text = hologramText
            hologram.update()

            item?.let {
                item.location = location.clone().add(0.0, config.iconOffset, 0.0)
                item.update()
            }
        } else {
            val hologram = Hologram(player, location, hologramText).also { it.spawn() }
            val item = if (config.iconEnabled && trackable is Waypoint) {
                plugin.apiExtensions.run {
                    SmoothFloatingItem(
                        player,
                        location,
                        plugin.apiExtensions.run {
                            ItemStack(trackable.getIconMaterial())
                        }
                    ).also { it.spawn() }
                }
            } else null

            activeHolograms[player.uniqueId] = hologram to item
        }
    }

    private fun Trackable.getReplacements(player: Player): Array<TagResolver> {
        val resolvers = mutableListOf(
            "world" placeholder plugin.worldTranslations.getWorldName(location.world!!),
            "distance" placeholder player.location.distance(location).roundToLong(),
            "x" placeholder location.x,
            "y" placeholder location.y,
            "z" placeholder location.z,
            "blockX" placeholder location.blockX,
            "blockY" placeholder location.blockY,
            "blockZ" placeholder location.blockZ,
        )
        if (this is Waypoint) {
            resolvers += "name" placeholder name
            resolvers += "createdAt" placeholder createdAt
        }
        if (this is PlayerTrackable) {
            resolvers += "name" placeholder this.player.displayName()
        }
        return resolvers.toTypedArray()
    }

    override fun hide(player: Player, trackable: Trackable, translatedTarget: Location?) {
        activeHolograms.remove(player.uniqueId)?.let { (hologram, item) ->
            hologram.destroy()
            item?.destroy()
        }
    }
}