package de.md5lukas.waypoints.pointers.variants

import de.md5lukas.schedulers.AbstractScheduler
import de.md5lukas.waypoints.pointers.Pointer
import de.md5lukas.waypoints.pointers.PointerManager
import de.md5lukas.waypoints.pointers.Trackable
import de.md5lukas.waypoints.pointers.config.CompassConfiguration
import java.util.concurrent.CompletableFuture
import org.bukkit.Location
import org.bukkit.entity.Player

internal class CompassPointer(
    pointerManager: PointerManager,
    player: Player,
    scheduler: AbstractScheduler,
    private val config: CompassConfiguration
) : Pointer(pointerManager, player, scheduler) {

  override val interval: Int
    get() = config.interval

  override val supportsMultipleTargets: Boolean
    get() = false

  private var isTracking = false
  private var restoreCompassTarget: CompletableFuture<Void>? = null

  override fun show(trackable: Trackable, translatedTarget: Location?) {
    val currentCompassTarget = player.compassTarget

    val localFuture = restoreCompassTarget
    if (localFuture === null || localFuture.isDone) {
      pointerManager.hooks.saveCompassTarget(player, currentCompassTarget)
    }
    restoreCompassTarget = null

    update(trackable, translatedTarget)
  }

  override fun update(trackable: Trackable, translatedTarget: Location?) {
    isTracking = true

    val location = translatedTarget ?: trackable.location
    player.compassTarget = location
  }

  override fun hide(trackable: Trackable, translatedTarget: Location?) {
    isTracking = false

    restoreCompassTarget =
        pointerManager.hooks
            .loadCompassTarget(player)
            .thenAcceptAsync(
                {
                  if (it != null && !isTracking) {
                    player.compassTarget = it
                  }
                },
                scheduler.asExecutor())
  }
}
