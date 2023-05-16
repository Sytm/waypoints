package de.md5lukas.waypoints.pointers.config

import org.bukkit.Particle

interface ParticleConfiguration : RepeatingPointerConfiguration {

  val heightOffset: Double

  val showVerticalDirection: Boolean

  val amount: Int

  val length: Double

  val particle: Particle
}
