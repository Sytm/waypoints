package de.md5lukas.waypoints.pointers

import de.md5lukas.schedulers.AbstractScheduler
import de.md5lukas.waypoints.pointers.config.PointerConfiguration
import de.md5lukas.waypoints.pointers.variants.ActionBarPointer
import de.md5lukas.waypoints.pointers.variants.BeaconPointer
import de.md5lukas.waypoints.pointers.variants.BlinkingBlockPointer
import de.md5lukas.waypoints.pointers.variants.BossBarPointer
import de.md5lukas.waypoints.pointers.variants.CompassPointer
import de.md5lukas.waypoints.pointers.variants.HologramPointer
import de.md5lukas.waypoints.pointers.variants.ParticlePointer
import de.md5lukas.waypoints.pointers.variants.TrailPointer
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.patheloper.mapping.PatheticMapper

/**
 * The PointerManager handles the creation of the selected PointerTypes and manages their tasks
 *
 * @constructor Creates a new PointerManager
 * @property plugin The plugin to register the tasks for
 * @property hooks The callbacks this library requires to be implemented by the caller
 * @property configuration The configuration for the pointers
 */
class PointerManager(
    internal val plugin: Plugin,
    internal val hooks: Hooks,
    internal var configuration: PointerConfiguration,
) : Listener {

  init {
    plugin.server.pluginManager.registerEvents(this, plugin)
    PatheticMapper.initialize(plugin as JavaPlugin)
  }

  internal val availablePointers:
      List<(PointerConfiguration, Player, AbstractScheduler) -> Pointer?> =
      listOf(
          { config, player, scheduler ->
            with(config.actionBar) {
              if (enabled) {
                ActionBarPointer(this@PointerManager, player, scheduler, this)
              } else {
                null
              }
            }
          },
          { config, player, scheduler ->
            with(config.beacon) {
              if (enabled) {
                BeaconPointer(this@PointerManager, player, scheduler, this)
              } else {
                null
              }
            }
          },
          { config, player, scheduler ->
            with(config.blinkingBlock) {
              if (enabled) {
                BlinkingBlockPointer(this@PointerManager, player, scheduler, this)
              } else {
                null
              }
            }
          },
          { config, player, scheduler ->
            with(config.compass) {
              if (enabled) {
                CompassPointer(this@PointerManager, player, scheduler, this)
              } else {
                null
              }
            }
          },
          { config, player, scheduler ->
            with(config.particle) {
              if (enabled) {
                ParticlePointer(this@PointerManager, player, scheduler, this)
              } else {
                null
              }
            }
          },
          { config, player, scheduler ->
            with(config.hologram) {
              if (enabled && plugin.server.pluginManager.isPluginEnabled("ProtocolLib")) {
                HologramPointer(this@PointerManager, player, scheduler, this)
              } else {
                null
              }
            }
          },
          { config, player, scheduler ->
            with(config.bossBar) {
              if (enabled) {
                BossBarPointer(this@PointerManager, player, scheduler, this)
              } else {
                null
              }
            }
          },
          { config, player, scheduler ->
            with(config.trail) {
              if (enabled) {
                TrailPointer(this@PointerManager, player, scheduler, this)
              } else {
                null
              }
            }
          },
      )

  private val players = ConcurrentHashMap<Player, ManagedPlayer>()

  /**
   * Safely shuts down all pointers, recreates them based on the new configuration and restarts them
   *
   * @param newConfiguration The new configuration to use
   */
  fun applyNewConfiguration(newConfiguration: PointerConfiguration) {
    configuration = newConfiguration
    TrailPointer.resetPathfinder()
    players.values.forEach { it.reapplyConfiguration() }
  }

  /**
   * Enables the pointer for a player towards the provided trackable.
   *
   * This will call [Hooks.saveActiveTrackables] to save this new active trackable
   */
  fun enable(player: Player, trackable: Trackable): Unit = enable(player, trackable, true)

  private fun enable(player: Player, trackable: Trackable, save: Boolean) {
    val managedPlayer = players.computeIfAbsent(player) { ManagedPlayer(this, it) }
    managedPlayer.show(trackable)
    if (save) {
      hooks.saveActiveTrackables(player, managedPlayer.readOnlyTracked)
    }
  }

  /**
   * Disables the pointer for the given player.
   *
   * This will call [Hooks.saveActiveTrackables]
   */
  fun disable(player: Player, predicate: TrackablePredicate): Unit =
      disable(player, predicate, true)

  private fun disable(player: Player, predicate: TrackablePredicate, save: Boolean) {
    players[player]?.let { managedPlayer ->
      managedPlayer.readOnlyTracked.filter(predicate).forEach { managedPlayer.hide(it) }
      if (save) {
        hooks.saveActiveTrackables(player, managedPlayer.readOnlyTracked)
      }
      if (managedPlayer.canBeDiscarded) {
        players -= player
      }
    }
  }

  /**
   * Disables all pointers where the trackable matches the [predicate].
   *
   * This will call [Hooks.saveActiveTrackables] for every player.
   */
  fun disableAll(predicate: TrackablePredicate) {
    players.keys.forEach { disable(it, predicate) }
  }

  /** Gets the current trackables for the player */
  fun getCurrentTargets(player: Player): Collection<Trackable> =
      players[player]?.readOnlyTracked ?: emptyList()

  @EventHandler
  internal fun onPlayerJoin(e: PlayerJoinEvent) {
    hooks.loadActiveTrackables(e.player).thenAccept { trackables ->
      trackables.forEach { enable(e.player, it, false) }
    }
  }

  @EventHandler
  internal fun onQuit(e: PlayerQuitEvent) {
    players.remove(e.player)?.immediateCleanup()
  }

  @EventHandler
  internal fun onMove(e: PlayerMoveEvent) {
    val trackables = getCurrentTargets(e.player)

    val disableWhenReachedRadius = configuration.disableWhenReachedRadiusSquared

    if (trackables.isEmpty() || disableWhenReachedRadius == 0) {
      return
    }

    trackables.forEach {
      if (e.player.world === it.location.world) {
        val distance = e.player.location.distanceSquared(it.location)

        if (distance <= disableWhenReachedRadius) {
          disable(e.player, it.asPredicate())
        }
      }
    }
  }

  @EventHandler
  internal fun onPluginDisable(e: PluginDisableEvent) {
    if (e.plugin !== plugin) return

    players.keys.forEach { disable(it, { true }, false) }
  }

  /** Hooks that get called by the [PointerManager] and some pointers */
  interface Hooks {
    /** Hooks required by the action bar pointer */
    val actionBarHooks: ActionBar

    /**
     * Save the provided trackables, if possible, to a non-volatile storage.
     *
     * The ordering of the trackables must be preserved.
     *
     * @param player The player that had the trackable enabled
     * @param tracked The new trackable or <code>null</code> if it has been disabled
     */
    fun saveActiveTrackables(player: Player, tracked: Collection<Trackable>)

    /**
     * Load the last active trackables from non-volatile storage.
     *
     * This is called when a player joins the server.
     *
     * @param player The player that had the trackable enabled
     * @return The last trackable or <code>null</code> if it has been disabled
     */
    fun loadActiveTrackables(player: Player): CompletableFuture<Collection<Trackable>>

    /**
     * Save the last active compass target of a player to non-volatile storage.
     *
     * @param player The player to store the compass location for
     * @param location The compass location the player previously had set
     */
    fun saveCompassTarget(player: Player, location: Location)

    /**
     * Load the last compass target from non-volatile storage.
     *
     * @param player The player to load the compass location for
     * @return The previous compass target or <code>null</code> if there is none
     */
    fun loadCompassTarget(player: Player): CompletableFuture<Location?>

    interface ActionBar {
      /**
       * Format a message for the player to show him the distance to his target. Only called if
       * [de.md5lukas.waypoints.pointers.config.ActionBarConfiguration.showDistanceEnabled] is set
       * to true.
       *
       * @param player The player that will see this message
       * @param distance3D The distance between the player and the target taking every axis into
       *   account
       * @param heightDifference The height difference between the player and the target. Positive
       *   if the player is higher up
       * @return The formatted message
       */
      fun formatDistanceMessage(
          player: Player,
          distance3D: Double,
          heightDifference: Double
      ): Component

      /**
       * Format a message for the player to show him if he is in an incorrect world.
       *
       * @param player The player that will see this message
       * @param current The world the player is in at the moment
       * @param correct The world the player must travel to
       */
      fun formatWrongWorldMessage(player: Player, current: World, correct: World): Component
    }
  }
}
