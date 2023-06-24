package de.md5lukas.waypoints.pointers.variants

import de.md5lukas.schedulers.AbstractScheduler
import de.md5lukas.waypoints.pointers.Pointer
import de.md5lukas.waypoints.pointers.PointerManager
import de.md5lukas.waypoints.pointers.Trackable
import de.md5lukas.waypoints.pointers.packets.ItemDisplay
import de.md5lukas.waypoints.pointers.packets.SmoothEntity
import de.md5lukas.waypoints.pointers.packets.TextDisplay
import de.md5lukas.waypoints.pointers.util.minus
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector

internal class HologramPointer(
    pointerManager: PointerManager,
    player: Player,
    scheduler: AbstractScheduler,
) : Pointer(pointerManager, player, scheduler) {

  private val config = pointerManager.configuration.hologram

  override val interval: Int
    get() = config.interval

  override val supportsMultipleTargets: Boolean
    get() = true

  override val async
    get() = true

  private val activeHolograms:
      MutableMap<Trackable, Pair<SmoothEntity<TextDisplay>, ItemDisplay?>> =
      HashMap()

  private lateinit var playerEyes: Location

  override fun preUpdates() {
    playerEyes = player.eyeLocation.clone()
  }

  override fun update(trackable: Trackable, translatedTarget: Location?) {
    if (translatedTarget === null) return

    val hologramText = trackable.getHologramText(player)

    if (hologramText === null) return
    val hologramTarget = translatedTarget.clone()

    hologramTarget.y += config.hologramHeightOffset

    val distanceSquared = playerEyes.distanceSquared(translatedTarget)

    val location =
        if (distanceSquared <= config.distanceFromPlayerSquared) {
          hologramTarget
        } else {
          val pVec = playerEyes.toVector()
          val tVec = hologramTarget.toVector()

          val direction = (tVec - pVec).normalize()

          pVec.add(direction.multiply(config.distanceFromPlayer)).toLocation(playerEyes.world!!)
        }

    if (trackable in activeHolograms) {
      val (hologram) = activeHolograms[trackable]!!

      hologram.location = location
      hologram.wrapped.text = hologramText
      hologram.update()
    } else {
      val item =
          if (config.iconEnabled) {
            trackable.hologramItem?.let { itemStack ->
              ItemDisplay(player, location, itemStack, Vector(0.0, config.iconOffset, 0.0)).also {
                it.spawn()
              }
            }
          } else null

      val hologram =
          SmoothEntity(player, location, TextDisplay(player, location, hologramText)).also {
            if (item !== null) {
              it.passengers += item
            }
            it.spawn()
          }

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
