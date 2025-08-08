package de.md5lukas.waypoints.pointers.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

class PointerConfiguration {

  var disableWhenReachedRadiusSquared: Int = 5
    get() = field * field
    private set

  var connectedWorlds: List<WorldConnection> = listOf(WorldConnection("world", "world_the_nether"))

  var actionBar: ActionBarConfiguration = ActionBarConfiguration()

  var beacon: BeaconConfiguration = BeaconConfiguration()

  var blinkingBlock: BlinkingBlockConfiguration = BlinkingBlockConfiguration()

  var compass: CompassConfiguration = CompassConfiguration()

  var particle: ParticleConfiguration = ParticleConfiguration()

  var hologram: HologramConfiguration = HologramConfiguration()

  var bossBar: BossBarConfiguration = BossBarConfiguration()

  var trail: TrailConfiguration = TrailConfiguration()

  @ConfigSerializable data class WorldConnection(val overworld: String, val underworld: String)
}
