package de.md5lukas.waypoints.pointers.variants

import de.md5lukas.schedulers.AbstractScheduler
import de.md5lukas.waypoints.pointers.Pointer
import de.md5lukas.waypoints.pointers.PointerManager
import de.md5lukas.waypoints.pointers.Trackable
import de.md5lukas.waypoints.pointers.util.minus
import de.md5lukas.waypoints.pointers.util.safeRemove
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.entity.Display
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.util.Transformation
import org.bukkit.util.Vector
import org.joml.AxisAngle4f
import org.joml.Vector3f

internal class HologramPointer(
    pointerManager: PointerManager,
    player: Player,
    scheduler: AbstractScheduler,
) : Pointer(pointerManager, player, scheduler, PointerVariant.HOLOGRAM) {

  private val config = pointerManager.configuration.hologram

  override val interval: Int
    get() = config.interval

  override val supportsMultipleTargets: Boolean
    get() = true

  override val async
    get() = true

  private val activeHolograms: MutableMap<Trackable, Hologram> = ConcurrentHashMap()

  private lateinit var playerEyes: Location
  private lateinit var velocityOffset: Vector

  override fun preUpdates() {
    playerEyes = player.eyeLocation
    velocityOffset = player.velocity.setY(0).multiply(interval)
  }

  override fun update(trackable: Trackable, translatedTarget: Location?) {
    if (translatedTarget === null) {
      hide(trackable, translatedTarget)
      return
    }

    val hologramText = trackable.getHologramText(player, translatedTarget)

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

          pVec
              .add(direction.multiply(config.distanceFromPlayer))
              .add(velocityOffset)
              .toLocation(playerEyes.world!!)
        }

    activeHolograms.compute(trackable) { _, hologram ->
      if (hologram == null) {
        Hologram(trackable, location, hologramText)
      } else {
        hologram.location = location
        hologram.text = hologramText
        hologram
      }
    }
  }

  override fun postUpdates() {
    scheduler.schedule { activeHolograms.values.forEach(Hologram::update) }
  }

  override fun hide(trackable: Trackable, translatedTarget: Location?) {
    activeHolograms.remove(trackable)?.remove()
  }

  override fun immediateCleanup(trackable: Trackable, translatedTarget: Location?) {
    activeHolograms.remove(trackable)?.remove()
  }

  private inner class Hologram(
      private val trackable: Trackable,
      var location: Location,
      var text: Component
  ) {

    private var textDisplay: TextDisplay? = null
    private var itemDisplay: ItemDisplay? = null

    fun update() {
      val textCapture = textDisplay
      val itemCapture = itemDisplay

      // When the display entities are no longer owned by the player region, immediately discard
      // them, as isValid checks no longer works from this thread
      if ((textCapture != null && !server.isOwnedByCurrentRegion(textCapture)) ||
          (itemCapture != null && !server.isOwnedByCurrentRegion(itemCapture))) {
        textCapture?.safeRemove(pointerManager.plugin)
        itemCapture?.safeRemove(pointerManager.plugin)
        textDisplay = null
        itemDisplay = null
      } else if (textCapture != null && textCapture.isValid && itemCapture?.isValid != false) {
        textCapture.text(text)

        val textTeleport = textCapture.teleportAsync(location)
        val itemTeleport = itemCapture?.teleportAsync(location)

        // Workaround for PaperMC/Paper#12599
        val combinedFuture =
            if (itemTeleport == null) {
              textTeleport
            } else {
              CompletableFuture.allOf(textTeleport, itemTeleport)
            }

        combinedFuture.thenRunAsync(
            {
              // On Folia canSee needs to be called in the region of the entity to check because
              // canSee calls isVisibleByDefault on that entity
              if (!player.canSee(textCapture)) {
                player.showEntity(pointerManager.plugin, textCapture)
              }
              if (itemCapture != null && !player.canSee(itemCapture)) {
                player.showEntity(pointerManager.plugin, itemCapture)
              }
            },
            syncExecutor)
        return
      }
      val world = player.world

      // Clean up possible remains if somehow one display disappeared but the other one remained
      textCapture?.safeRemove(pointerManager.plugin)
      itemCapture?.safeRemove(pointerManager.plugin)

      textDisplay =
          world
              .spawn(location, TextDisplay::class.java) {
                it.isPersistent = false
                it.isVisibleByDefault = false

                it.teleportDuration = interval

                it.billboard = Display.Billboard.CENTER

                it.text(text)
                it.isDefaultBackground = true
                it.isSeeThrough = true
              }
              .also { player.showEntity(pointerManager.plugin, it) }

      if (config.icon.enabled) {
        itemDisplay =
            trackable.hologramItem?.let { itemStack ->
              world
                  .spawn(location, ItemDisplay::class.java) {
                    it.isPersistent = false
                    it.isVisibleByDefault = false

                    it.teleportDuration = interval

                    it.setItemStack(itemStack)

                    val isBlock = itemStack.type.isBlock
                    it.billboard =
                        if (isBlock) {
                          Display.Billboard.FIXED
                        } else {
                          Display.Billboard.CENTER
                        }
                    it.transformation =
                        Transformation(
                            Vector3f(0.0f, config.icon.offset, 0.0f),
                            AxisAngle4f(),
                            Vector3f(if (isBlock) 1.0f else 0.6f),
                            AxisAngle4f(),
                        )
                    it.itemDisplayTransform = ItemDisplayTransform.FIXED
                  }
                  .also { player.showEntity(pointerManager.plugin, it) }
            }
      }
    }

    fun remove() {
      textDisplay?.safeRemove(pointerManager.plugin)
      itemDisplay?.safeRemove(pointerManager.plugin)
    }
  }
}
