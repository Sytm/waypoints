package de.md5lukas.waypoints.pointers.config

import de.md5lukas.configurate.Positive
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
class PointerConfiguration {

  @Comment(
      "Automatically deselects the waypoint when the player gets into the defined radius. Set to zero to disable")
  @Positive(true)
  var disableWhenReachedRadius: Int = 5
    get() = field * field
    private set

  @Comment(
      "Connected worlds in this list allow the translation of the coordinates 1:8, so you can for example navigate in the nether to a waypoint in the overworld")
  var connectedWorlds: List<WorldConnection> = listOf(WorldConnection("world", "world_nether"))
    private set

  var actionBar: ActionBarConfiguration = ActionBarConfiguration()
    private set

  var beacon: BeaconConfiguration = BeaconConfiguration()
    private set

  var blinkingBlock: BlinkingBlockConfiguration = BlinkingBlockConfiguration()
    private set

  var compass: CompassConfiguration = CompassConfiguration()
    private set

  var particle: ParticleConfiguration = ParticleConfiguration()
    private set

  var hologram: HologramConfiguration = HologramConfiguration()
    private set

  var bossBar: BossBarConfiguration = BossBarConfiguration()
    private set

  var trail: TrailConfiguration = TrailConfiguration()
    private set

  @ConfigSerializable
  class WorldConnection() {

    var overworld: String = ""
    var underworld: String = ""

    constructor(overworld: String, underworld: String) : this() {
      this.overworld = overworld
      this.underworld = underworld
    }
  }
}
