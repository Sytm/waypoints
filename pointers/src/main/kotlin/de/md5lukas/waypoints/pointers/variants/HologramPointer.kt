package de.md5lukas.waypoints.pointers.variants

import de.md5lukas.schedulers.AbstractScheduler
import de.md5lukas.waypoints.pointers.Pointer
import de.md5lukas.waypoints.pointers.PointerManager
import de.md5lukas.waypoints.pointers.Trackable
import de.md5lukas.waypoints.pointers.config.HologramConfiguration
import de.md5lukas.waypoints.pointers.packets.FloatingItem
import de.md5lukas.waypoints.pointers.packets.Hologram
import de.md5lukas.waypoints.pointers.packets.SmoothEntity
import de.md5lukas.waypoints.pointers.util.minus
import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.entity.Player

internal class HologramPointer(
    pointerManager: PointerManager,
    player: Player,
    scheduler: AbstractScheduler,
    private val config: HologramConfiguration
) : Pointer(pointerManager, player, scheduler) {

  override val interval: Int
    get() = config.interval

  override val supportsMultipleTargets: Boolean
    get() = true

  private val activeHolograms: MutableMap<Trackable, Pair<Hologram, SmoothEntity<FloatingItem>?>> =
      HashMap()

  override fun update(trackable: Trackable, translatedTarget: Location?) {
    if (translatedTarget === null) return

    val hologramText = trackable.getHologramText(player)

    if (hologramText === null) return

    val hologramTarget = translatedTarget.clone()

    hologramTarget.y += config.hologramHeightOffset

    val distanceSquared = player.location.distanceSquared(translatedTarget)
    val maxDistance = config.distanceFromPlayerSquared

    val location =
        if (distanceSquared <= maxDistance) {
          hologramTarget
        } else {
          val pVec = player.eyeLocation.toVector()
          val tVec = hologramTarget.toVector()

          val direction = (tVec - pVec).normalize()

          val rayTrace =
              if (config.preventOcclusion) {
                player.world.rayTraceBlocks(
                    player.eyeLocation,
                    direction,
                    config.distanceFromPlayer.toDouble(),
                    FluidCollisionMode.NEVER,
                    true)
              } else null

          if (rayTrace !== null) {
                // Move the hologram half a block closer to the player
                rayTrace.hitPosition.add(direction.multiply(-0.5))
              } else {
                val offset = direction.multiply(config.distanceFromPlayer)

                pVec.add(offset)
              }
              .toLocation(player.world)
        }

    if (trackable in activeHolograms) {
      val (hologram, item) = activeHolograms[trackable]!!

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
            trackable.hologramItem?.let { itemStack ->
              SmoothEntity(
                      player,
                      location,
                      FloatingItem(
                          player,
                          location,
                          itemStack,
                      ))
                  .also { it.spawn() }
            }
          } else null

      activeHolograms[trackable] = hologram to item
    }
  }

  override fun hide(trackable: Trackable, translatedTarget: Location?) {
    activeHolograms.remove(trackable)?.let { (hologram, item) ->
      hologram.destroy()
      item?.destroy()
    }
  }
}
