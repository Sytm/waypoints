package de.md5lukas.waypoints.pointers.config

import org.bukkit.Particle

interface TrailConfiguration : RepeatingPointerConfiguration {

  val pathingMaxIterations: Int

  val pathingMaxLength: Int

  val pathingAllowChunkLoading: Boolean

  val pathingAllowChunkGeneration: Boolean

  val pathingSwimPenalty: Double

  val pathingHeuristicWeight: Double

  val pathInvalidationDistanceSquared: Int

  val pathCalculateAheadDistanceSquared: Int

  val retainMaxPlayerDistanceSquared: Int

  val particleSpread: Double

  val particleAmount: Int

  val particleNormal: Particle

  val particleHighlight: Particle

  val highlightDistance: Int
}
