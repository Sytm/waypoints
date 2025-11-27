package de.md5lukas.waypoints.pointers.config

import de.md5lukas.configurate.Positive
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
class HologramConfiguration : RepeatingPointerConfiguration {

  override var enabled = true
    private set

  @Positive
  override var interval = 5
    private set

  @Comment("The distance of the hologram from the player")
  @Positive
  var distanceFromPlayer: Int = 4
    private set

  val distanceFromPlayerSquared: Int
    get() = distanceFromPlayer * distanceFromPlayer

  @Comment(
      """
    The height offset of the hologram.
    If set to zero it will be at the exact location of the waypoint and close to the ground, set to higher numbers to move it up
  """)
  var hologramHeightOffset: Double = 1.0
    private set

  @Comment("Displays the icon of the waypoint as a floating item below the text")
  var icon = Icon()
    private set

  @ConfigSerializable
  class Icon {

    var enabled: Boolean = true
      private set

    @Comment("The vertical offset of the hovering item")
    var offset: Float = -0.3f
      private set
  }
}
