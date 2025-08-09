package de.md5lukas.waypoints.pointers.config

import de.md5lukas.configurate.Positive
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
class CompassConfiguration : RepeatingPointerConfiguration {

  override var enabled = true
    private set

  @Comment(
      "The interval in game ticks when the compass target should be updated. Only relevant when tracking players")
  @Positive
  override var interval = 20
    private set
}
