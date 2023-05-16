package de.md5lukas.waypoints.pointers.config

import com.google.common.collect.BiMap

interface PointerConfiguration {

  val disableWhenReachedRadiusSquared: Int

  val connectedWorlds: BiMap<String, String>

  val actionBar: ActionBarConfiguration

  val beacon: BeaconConfiguration

  val blinkingBlock: BlinkingBlockConfiguration

  val compass: CompassConfiguration

  val particle: ParticleConfiguration

  val hologram: HologramConfiguration

  val bossBar: BossBarConfiguration
}
