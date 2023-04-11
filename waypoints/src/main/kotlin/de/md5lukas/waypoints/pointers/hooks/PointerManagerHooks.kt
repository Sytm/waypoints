package de.md5lukas.waypoints.pointers.hooks

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.pointers.PointerManager.Hooks
import de.md5lukas.waypoints.pointers.Trackable
import de.md5lukas.waypoints.pointers.WaypointTrackable
import de.md5lukas.waypoints.util.placeholder
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player

class PointerManagerHooks(private val plugin: WaypointsPlugin) : Hooks {

    override val actionBarHooks: Hooks.ActionBar = ActionBarPointerHooks()

    override fun saveActiveTrackable(player: Player, tracked: Trackable?) {
        if (tracked === null) {
            plugin.api.getWaypointPlayer(player.uniqueId).lastSelectedWaypoint = null
        } else if (tracked is WaypointTrackable) {
            plugin.api.getWaypointPlayer(player.uniqueId).lastSelectedWaypoint = tracked.waypoint
        }
    }

    override fun loadActiveTrackable(player: Player): Trackable? =
        plugin.api.getWaypointPlayer(player.uniqueId).lastSelectedWaypoint?.let { WaypointTrackable(plugin, it) }

    override fun saveCompassTarget(player: Player, location: Location) {
        plugin.api.getWaypointPlayer(player.uniqueId).compassTarget = location
    }

    override fun loadCompassTarget(player: Player) = plugin.api.getWaypointPlayer(player.uniqueId).compassTarget

    private inner class ActionBarPointerHooks : Hooks.ActionBar {
        override fun formatDistanceMessage(player: Player, distance3D: Double, heightDifference: Double): Component =
            plugin.translations.POINTERS_ACTION_BAR_DISTANCE.withReplacements(
                "distance" placeholder distance3D,
                "height_difference" placeholder heightDifference,
            )

        override fun formatWrongWorldMessage(player: Player, current: World, correct: World): Component =
            plugin.translations.POINTERS_ACTION_BAR_WRONG_WORLD.withReplacements(
                "current" placeholder plugin.worldTranslations.getWorldName(current),
                "correct" placeholder plugin.worldTranslations.getWorldName(correct),
            )
    }
}