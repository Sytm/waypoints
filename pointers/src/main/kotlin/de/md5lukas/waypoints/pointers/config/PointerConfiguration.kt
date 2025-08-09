package de.md5lukas.waypoints.pointers.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

class PointerConfiguration {

  var disableWhenReachedRadius: Int = 5
    get() = field * field
    private set

  var connectedWorlds: List<WorldConnection> = listOf(WorldConnection("world", "world_the_nether"))
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

  @ConfigSerializable data class WorldConnection(val overworld: String, val underworld: String)
}
