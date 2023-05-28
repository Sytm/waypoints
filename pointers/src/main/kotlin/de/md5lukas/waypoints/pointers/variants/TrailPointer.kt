package de.md5lukas.waypoints.pointers.variants

import de.md5lukas.schedulers.AbstractScheduler
import de.md5lukas.waypoints.pointers.Pointer
import de.md5lukas.waypoints.pointers.PointerManager
import de.md5lukas.waypoints.pointers.StaticTrackable
import de.md5lukas.waypoints.pointers.Trackable
import de.md5lukas.waypoints.pointers.config.TrailConfiguration
import de.md5lukas.waypoints.pointers.util.blockEquals
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.ceil
import org.bukkit.Location
import org.bukkit.entity.Player
import org.patheloper.api.pathing.Pathfinder
import org.patheloper.api.pathing.rules.PathingRuleSet
import org.patheloper.api.wrapper.PathPosition
import org.patheloper.mapping.PatheticMapper
import org.patheloper.mapping.bukkit.BukkitMapper

internal class TrailPointer(
    pointerManager: PointerManager,
    player: Player,
    scheduler: AbstractScheduler,
    private val config: TrailConfiguration
) : Pointer(pointerManager, player, scheduler) {

  companion object {
    private val pathfinderLock = Any()
    private var pathfinder: Pathfinder? = null

    fun getPathfinder(config: TrailConfiguration): Pathfinder {
      pathfinder?.let {
        return it
      }
      synchronized(pathfinderLock) {
        pathfinder?.let {
          return it
        }
        var ruleset =
            PathingRuleSet.createAsyncRuleSet()
                .withAllowingDiagonal(config.pathingAllowDiagonal)
                .withAllowingFallback(config.pathingAllowFallback)
                .withLoadingChunks(config.pathingAllowChunkLoading)
                .withAllowingFailFast(config.pathingAllowFastFail)
                .withStrategy(config.pathingStrategy.clazz)

        if (config.pathingMaxLength > 0) {
          ruleset = ruleset.withMaxLength(config.pathingMaxLength)
        }

        val pathfinder = PatheticMapper.newPathfinder(ruleset)
        this.pathfinder = pathfinder
        return pathfinder
      }
    }

    fun resetPathfinder() {
      synchronized(pathfinderLock) { pathfinder = null }
    }
  }

  override val interval: Int
    get() = config.interval

  override val supportsMultipleTargets: Boolean
    get() = false

  private var highlightCounter: Int = 0

  private val pathfinder = getPathfinder(config)

  private val locationTrail: CopyOnWriteArrayList<Location> = CopyOnWriteArrayList()
  private var lastFuture: CompletableFuture<*>? = null

  override fun update(trackable: Trackable, translatedTarget: Location?) {
    if (translatedTarget === null || trackable !is StaticTrackable) {
      return
    }

    if (lastFuture === null) {
      // If the player has moved too far away from any parts of the calculated trail invalidate the
      // previous trail or if the calculated trail is only one block long because the path couldn't be calculated
      if (locationTrail.all {
        player.location.distanceSquared(it) >= config.pathInvalidationDistanceSquared
      } || (locationTrail.size == 1 && !locationTrail.last().blockEquals(translatedTarget))) {
        locationTrail.clear()
      }

      if (locationTrail.isEmpty()) {
        lastFuture =
            pathfinder
                .findPath(player.location.toPathPosition(), translatedTarget.toPathPosition())
                .toCompletableFuture()
                .thenApply { result ->
                  if (result.successful()) {
                    locationTrail.addAll(result.path.positions.map { it.toCenterLocation() })
                  }
                  lastFuture = null
                }
                .exceptionally {
                  pointerManager.plugin.slF4JLogger.error("Couldn't calculate path", it)
                }
      } else {
        val last = locationTrail.last()
        if (!last.blockEquals(translatedTarget) &&
            player.location.distanceSquared(last) < config.pathCalculateAheadDistanceSquared) {
          lastFuture =
              pathfinder
                  .findPath(last.toPathPosition(), translatedTarget.toPathPosition())
                  .toCompletableFuture()
                  .thenApply { result ->
                    if (result.successful()) {
                      // Remove everything behind the player further away than X
                      val lastIndex =
                          locationTrail.indexOfLast {
                            player.location.distanceSquared(it) >=
                                config.retainMaxPlayerDistanceSquared
                          }
                      if (lastIndex > 0) {
                        locationTrail.subList(0, lastIndex).clear()
                      }
                      locationTrail.addAll(
                          result.path.positions.mapIndexedNotNull { index, pathPosition ->
                            if (index == 0) {
                              null // Skip start because it is already contained in trail
                            } else {
                              pathPosition.toCenterLocation()
                            }
                          })
                    } else {
                      // Can no longer calculate path. Invalidate entire trail
                      locationTrail.clear()
                    }
                    lastFuture = null
                  }
                  .exceptionally {
                    pointerManager.plugin.slF4JLogger.error("Couldn't calculate path", it)
                  }
        }
      }
    }

    locationTrail.forEachIndexed { index, location ->
      val isHighlight = ((index - highlightCounter) % config.highlightDistance) == 0

      player.spawnParticle(
          if (isHighlight) {
            config.particleHighlight
          } else {
            config.particleNormal
          },
          location,
          if (isHighlight) {
            ceil(config.particleAmount * 1.5).toInt()
          } else {
            config.particleAmount
          },
          config.particleSpread,
          config.particleSpread,
          config.particleSpread,
          0.0,
      )
    }

    highlightCounter = (highlightCounter + 1) % config.highlightDistance
  }

  override fun hide(trackable: Trackable, translatedTarget: Location?) {
    highlightCounter = 0
    lastFuture?.cancel(true)
  }

  private fun Location.toPathPosition() = BukkitMapper.toPathPosition(this)

  private fun PathPosition.toCenterLocation() =
      Location(
          BukkitMapper.toWorld(pathEnvironment),
          blockX + 0.5,
          blockY + 0.5,
          blockZ + 0.5,
      )
}
