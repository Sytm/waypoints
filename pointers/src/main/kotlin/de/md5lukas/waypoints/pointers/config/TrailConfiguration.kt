package de.md5lukas.waypoints.pointers.config

import de.md5lukas.configurate.Positive
import org.bukkit.Particle
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
class TrailConfiguration : RepeatingPointerConfiguration {

  override var enabled = true
    private set

  @Positive
  override var interval = 10
    private set

  @Comment("Settings for the pathing engine")
  var pathing = Pathing()
    private set

  @ConfigSerializable
  class Pathing {
    @Comment(
        """
      The maximum distance that is calculated ahead each time
      Longer ranges take more time to calculate at a time but might provide better paths.
      If the pathfinder encounters unloaded chunks and loading is disabled it will stop there
    """)
    @Positive
    var maxLength: Int = 200
      private set

    @Comment(
        """
      The maximum amount of iterations the pathfinding algorithm may perform.
      Needs to be set higher if distances increase or the environment is complex
    """)
    @Positive
    var maxIterations: Int = 10_000
      private set

    @Comment("Allow the pathfinder to load chunks in advance to calculate the path")
    var allowChunkLoading: Boolean = false
      private set

    @Comment("Allow the pathfinder to generate chunks when attempting to load them")
    var allowChunkGeneration: Boolean = false
      private set

    @Comment(
        """
      Penalty to apply to possible moves across water
      Increased values make the pathfinder try to find a (possible) longer path around water.
      Set to zero to disable water movement
    """)
    @Positive(true)
    var swimPenalty: Double = 5.0
      private set

    @Comment(
        """
      The weight to apply to the pathfinder heuristic.
      Higher values make the pathfinder prefer to go more to the waypoint at the cost of not 100% optimal paths
    """)
    @Positive
    var heuristicWeight: Double = 2.0
      private set
  }

  @Comment(
      """
    Max distance between player and any part of the path.
    When exceeded the path is recalculated from the players current location
  """)
  @Positive
  var pathInvalidationDistance: Int = 15
    get() = field * field
    private set

  @Comment(
      """
    When the player comes into this distance of the end of the last calculated trail
    An attempt is made to calculate a continuation
  """)
  @Positive
  var pathCalculateAheadDistance: Int = 50
    get() = field * field
    private set

  @Comment(
      """
    When the path has been extended, every past part of the trail further
    away from the player than this distance is discarded
  """)
  @Positive
  var retainMaxPlayerDistance: Int = 30
    get() = field * field
    private set

  var particles = Particles()
    private set

  @ConfigSerializable
  class Particles {
    @Comment("The spread of the particles")
    @Positive(true)
    var spread: Double = 0.3
      private set

    @Comment(
        """
      The amount of particles spawned per block
      Highlights spawn 1.5 times the given amount
    """)
    @Positive
    var amount: Int = 4
      private set

    @Comment(
        """
      To view all available Particles see here: https://jd.papermc.io/paper/1.21.8/org/bukkit/Particle.html
      The normal particle to spawn
    """)
    var normal: Particle = Particle.WAX_ON
      private set

    @Comment("The highlight particle to spawn")
    var highlight: Particle = Particle.WAX_OFF
      private set
  }

  @Comment("The distance of blocks between each highlight")
  @Positive
  var highlightDistance: Int = 7
    private set
}
