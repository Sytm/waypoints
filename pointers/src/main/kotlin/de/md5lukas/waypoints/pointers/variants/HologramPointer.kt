package de.md5lukas.waypoints.pointers.variants

import de.md5lukas.waypoints.pointers.Pointer
import de.md5lukas.waypoints.pointers.PointerManager
import de.md5lukas.waypoints.pointers.Trackable
import de.md5lukas.waypoints.pointers.config.HologramConfiguration
import de.md5lukas.waypoints.pointers.packets.Hologram
import de.md5lukas.waypoints.pointers.packets.SmoothFloatingItem
import de.md5lukas.waypoints.pointers.util.minus
import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

internal class HologramPointer(
    pointerManager: PointerManager,
    private val config: HologramConfiguration
) : Pointer(pointerManager, config.interval) {

    private val activeHolograms: MutableMap<UUID, Pair<Hologram, SmoothFloatingItem?>> = HashMap()

    override fun update(player: Player, trackable: Trackable, translatedTarget: Location?) {
        if (translatedTarget === null)
            return

        val hologramText = pointerManager.hooks.hologramHooks.formatHologramText(player, trackable)

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
            val item =
                if (config.iconEnabled) {
                    pointerManager.hooks.hologramHooks.getIconMaterial(trackable)?.let { material ->
                        SmoothFloatingItem(
                            player,
                            location,
                            ItemStack(material)
                        ).also { it.spawn() }
                    }
                } else null

            activeHolograms[player.uniqueId] = hologram to item
        }
    }


    override fun hide(player: Player, trackable: Trackable, translatedTarget: Location?) {
        activeHolograms.remove(player.uniqueId)?.let { (hologram, item) ->
            hologram.destroy()
            item?.destroy()
        }
    }
}