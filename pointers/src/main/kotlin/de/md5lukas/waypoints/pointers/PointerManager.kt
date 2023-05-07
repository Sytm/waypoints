package de.md5lukas.waypoints.pointers

import de.md5lukas.waypoints.pointers.config.PointerConfiguration
import de.md5lukas.waypoints.pointers.variants.*
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
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.CompletableFuture

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
    }

    private val availablePointers: List<(PointerConfiguration) -> Pointer?> = listOf(
        {
            with(it.actionBar) {
                if (enabled) {
                    ActionBarPointer(this@PointerManager, this)
                } else {
                    null
                }
            }
        }, {
            with(it.beacon) {
                if (enabled) {
                    BeaconPointer(this@PointerManager, this)
                } else {
                    null
                }
            }
        }, {
            with(it.blinkingBlock) {
                if (enabled) {
                    BlinkingBlockPointer(this@PointerManager, this)
                } else {
                    null
                }
            }
        }, {
            with(it.compass) {
                if (enabled) {
                    CompassPointer(this@PointerManager, this)
                } else {
                    null
                }
            }
        }, {
            with(it.particle) {
                if (enabled) {
                    ParticlePointer(this@PointerManager, this)
                } else {
                    null
                }
            }
        }, {
            with(it.hologram) {
                if (enabled && plugin.server.pluginManager.isPluginEnabled("ProtocolLib")) {
                    HologramPointer(this@PointerManager, this)
                } else {
                    null
                }
            }
        }, {
            with(it.bossBar) {
                if (enabled) {
                    BossBarPointer(this@PointerManager, this)
                } else {
                    null
                }
            }
        }
    )

    private val enabledPointers: MutableList<Pointer> = ArrayList()
    private val enabledPointerTasks: MutableList<BukkitTask> = ArrayList()

    private val activePointers: MutableMap<Player, ActivePointer> = HashMap()

    private fun setupPointers() {
        availablePointers.forEach { supplier ->
            supplier(configuration)?.let { pointer ->
                enabledPointers.add(pointer)

                if (pointer.interval > 0) {
                    enabledPointerTasks.add(plugin.server.scheduler.runTaskTimer(plugin, PointerTask(pointer, activePointers), 0, pointer.interval.toLong()))
                }
            }
        }
    }

    /**
     * Safely shuts down all pointers, recreates them based on the new configuration and restarts them
     *
     * @param newConfiguration The new configuration to use
     */
    fun applyNewConfiguration(newConfiguration: PointerConfiguration) {
        enabledPointerTasks.forEach(BukkitTask::cancel)
        enabledPointerTasks.clear()

        val activePointersCopy = HashMap(activePointers)
        activePointersCopy.keys.forEach {
            disable(it, false)
        }

        enabledPointers.clear()

        configuration = newConfiguration

        setupPointers()
        activePointersCopy.forEach { (player, activePointer) ->
            enable(player, activePointer.trackable)
        }
    }

    /**
     * Enables the pointer for a player towards the provided trackable.
     *
     * This will call [Hooks.saveActiveTrackable] to save this new active trackable
     */
    fun enable(player: Player, trackable: Trackable) = enable(player, trackable, true)

    private fun enable(player: Player, trackable: Trackable, save: Boolean) {
        if (trackable.location.world === null) {
            throw IllegalStateException("The waypoint to activate the pointers to has no world available")
        }

        val newPointer = ActivePointer(this, player, trackable)
        activePointers.put(player, newPointer)?.let { oldPointer ->
            hide(player, oldPointer)
        }
        show(player, newPointer)
        if (save) {
            hooks.saveActiveTrackable(player, trackable)
        }
    }

    /**
     * Disables the pointer for the given player.
     *
     * This will call [Hooks.saveActiveTrackable]
     */
    fun disable(player: Player) = disable(player, true)

    private fun disable(player: Player, save: Boolean) {
        activePointers.remove(player)?.let {
            hide(player, it)
        }
        if (save) {
            hooks.saveActiveTrackable(player, null)
        }
    }

    /**
     * Disables all pointers where the trackable matches the [predicate].
     *
     * This will call [Hooks.saveActiveTrackable] for every player.
     */
    fun disableAll(predicate: (Trackable) -> Boolean) {
        activePointers.filter { predicate(it.value.trackable) }.forEach {
            disable(it.key)
        }
    }

    /**
     * Gets the current trackable for the player or <code>null</code> if there is none
     */
    fun getCurrentTarget(player: Player): Trackable? = activePointers[player]?.trackable

    private fun show(player: Player, pointer: ActivePointer) {
        val trackable = pointer.trackable
        plugin.server.pluginManager.callEvent(TrackableSelectEvent(player, trackable))
        enabledPointers.forEach {
            it.show(player, pointer.trackable, pointer.translatedTarget)
        }
    }

    private fun hide(player: Player, pointer: ActivePointer) {
        val trackable = pointer.trackable
        plugin.server.pluginManager.callEvent(TrackableDeselectEvent(player, trackable))
        enabledPointers.forEach {
            it.hide(player, pointer.trackable, pointer.translatedTarget)
        }
    }

    @EventHandler
    private fun onPlayerJoin(e: PlayerJoinEvent) {
        hooks.loadActiveTrackable(e.player).thenAccept {
            if (it !== null) {
                plugin.server.scheduler.runTask(
                    plugin,
                    Runnable {
                        // Run this in the next tick, because otherwise the compass pointer errors
                        // because the player doesn't have a current compass target yet
                        enable(e.player, it, false)
                    }
                )
            }
        }
    }

    @EventHandler
    private fun onQuit(e: PlayerQuitEvent) {
        disable(e.player, false)
    }

    @EventHandler
    private fun onMove(e: PlayerMoveEvent) {
        val pointer = getCurrentTarget(e.player) ?: return

        val disableWhenReachedRadius = configuration.disableWhenReachedRadiusSquared

        if (disableWhenReachedRadius == 0) {
            return
        }

        if (e.player.world === pointer.location.world) {
            val distance = e.player.location.distanceSquared(pointer.location)

            if (distance <= disableWhenReachedRadius) {
                disable(e.player)
            }
        }
    }

    @EventHandler
    private fun onPluginDisable(e: PluginDisableEvent) {
        if (e.plugin !== plugin)
            return

        plugin.server.onlinePlayers.forEach {
            disable(it, false)
        }
    }

    init {
        setupPointers()
    }

    /**
     * Hooks that get called by the [PointerManager] and some pointers
     */
    interface Hooks {
        /**
         * Hooks required by the action bar pointer
         */
        val actionBarHooks: ActionBar

        /**
         * Save the provided trackable, if possible, to a non-volatile storage.
         *
         * @param player The player that had the trackable enabled
         * @param tracked The new trackable or <code>null</code> if it has been disabled
         */
        fun saveActiveTrackable(player: Player, tracked: Trackable?)

        /**
         * Load the last active trackable from non-volatile storage.
         *
         * This is called when a player joins the server and on server startup
         *
         * @param player The player that had the trackable enabled
         * @return The last trackable or <code>null</code> if it has been disabled
         */
        fun loadActiveTrackable(player: Player): CompletableFuture<Trackable?>

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
             * Format a message for the player to show him the distance to his target.
             * Only called if [de.md5lukas.waypoints.pointers.config.ActionBarConfiguration.showDistanceEnabled] is set to true.
             *
             * @param player The player that will see this message
             * @param distance3D The distance between the player and the target taking every axis into account
             * @param heightDifference The height difference between the player and the target. Positive if the player is higher up
             * @return The formatted message
             */
            fun formatDistanceMessage(player: Player, distance3D: Double, heightDifference: Double): Component

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