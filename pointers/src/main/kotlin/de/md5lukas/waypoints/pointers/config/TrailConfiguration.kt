package de.md5lukas.waypoints.pointers.config

import org.bukkit.Particle
import org.patheloper.api.pathing.strategy.PathfinderStrategy
import org.patheloper.api.pathing.strategy.strategies.DirectPathfinderStrategy
import org.patheloper.api.pathing.strategy.strategies.PlayerPathfinderStrategy
import org.patheloper.api.pathing.strategy.strategies.WalkablePathfinderStrategy

interface TrailConfiguration : RepeatingPointerConfiguration {

  val pathingMaxLength: Int

  val pathingAllowDiagonal: Boolean

  val pathingAllowFallback: Boolean

  val pathingAllowChunkLoading: Boolean

  val pathingStrategy: PathingStrategy

  val pathInvalidationDistanceSquared: Int
  val pathCalculateAheadDistanceSquared: Int

  val retainMaxPlayerDistanceSquared: Int

  val particleSpread: Double

  val particleAmount: Int

  val particleNormal: Particle

  val particleHighlight: Particle

  val highlightDistance: Int

  enum class PathingStrategy(internal val clazz: Class<out PathfinderStrategy>) {
    DIRECT(DirectPathfinderStrategy::class.java),
    WALKABLE(WalkablePathfinderStrategy::class.java),
    PLAYER(PlayerPathfinderStrategy::class.java),
  }
}
