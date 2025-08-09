package de.md5lukas.waypoints.pointers.config

import de.md5lukas.configurate.Positive
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
class BlinkingBlockConfiguration : RepeatingPointerConfiguration {

  override var enabled = true
    private set

  @Comment("The interval in game ticks when the block should be changed")
  @Positive
  override var interval = 20
    private set

  @Comment(
      """
    The minimum and maximum distance for the blinking block to be visible
    Keep in mind that if you set the min distance to low it could be possible for the player to stand on the client-side block and get kicked for flying
  """)
  @Positive
  var minDistance: Long = 10
    get() = field * field
    private set

  @Positive
  var maxDistance: Long = 50
    get() = field * field
    private set

  @Comment("The blocks that will be cycled through")
  var blockDataSequence: Array<BlockData> =
      arrayOf(Material.GLASS.createBlockData(), Material.GLOWSTONE.createBlockData())
    private set
}
