package de.md5lukas.waypoints.config.pointers

import de.md5lukas.konfig.ConfigPath
import de.md5lukas.konfig.Configurable
import de.md5lukas.konfig.SkipConfig
import de.md5lukas.waypoints.pointers.config.HologramConfiguration

@Configurable
class HologramConfigurationImpl : RepeatingPointerConfigurationImpl(), HologramConfiguration {

  override var distanceFromPlayer: Int = 0
    private set

  @SkipConfig
  override val distanceFromPlayerSquared: Int
    get() = distanceFromPlayer * distanceFromPlayer

  override var hologramHeightOffset: Double = 0.0
    private set

  @ConfigPath("icon.enabled")
  override var iconEnabled: Boolean = false
    private set

  @ConfigPath("icon.offset")
  override var iconOffset: Float = 0.0f
    private set
}
