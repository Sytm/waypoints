package de.md5lukas.waypoints.pointers.config

import de.md5lukas.configurate.Positive
import org.bukkit.Particle
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
class ParticleConfiguration : RepeatingPointerConfiguration {

  override var enabled = false
    private set

  @Positive
  override var interval = 30
    private set

  @Comment("The height offset from the ground where the particles should spawn")
  var heightOffset: Double = 0.0
    private set

  @Comment(
      "If set to true, the particles will also point into the direction of the waypoint vertically")
  var showVerticalDirection: Boolean = false
    private set

  @Comment("The amount of particles")
  @Positive
  var amount: Int = 10
    private set

  @Comment("The total length of the particles in blocks")
  var length: Double = 1.0
    private set

  @Comment("https://jd.papermc.io/paper/1.21.8/org/bukkit/Particle.html")
  var particle: Particle = Particle.FLAME
    private set
}
