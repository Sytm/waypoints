package de.md5lukas.waypoints.pointers.hooks

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.pointers.PlayerTrackable
import de.md5lukas.waypoints.pointers.PointerManager.Hooks
import de.md5lukas.waypoints.pointers.TemporaryWaypointTrackable
import de.md5lukas.waypoints.pointers.Trackable
import de.md5lukas.waypoints.util.placeholder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import kotlin.math.roundToLong

class PointerManagerHooks(private val plugin: WaypointsPlugin) : Hooks {

    override val actionBarHooks: Hooks.ActionBar = ActionBarPointerHooks()
    override val hologramHooks: Hooks.Hologram = HologramPointerHooks()

    override fun saveActiveTrackable(player: Player, tracked: Trackable?) {
        if (tracked === null) {
            plugin.api.getWaypointPlayer(player.uniqueId).lastSelectedWaypoint = null
        } else if (tracked is Waypoint) {
            plugin.api.getWaypointPlayer(player.uniqueId).lastSelectedWaypoint = tracked
        }
    }

    override fun loadActiveTrackable(player: Player) = plugin.api.getWaypointPlayer(player.uniqueId).lastSelectedWaypoint

    override fun saveCompassTarget(player: Player, location: Location) {
        plugin.api.getWaypointPlayer(player.uniqueId).compassTarget = location
    }

    override fun loadCompassTarget(player: Player) = plugin.api.getWaypointPlayer(player.uniqueId).compassTarget

    private inner class ActionBarPointerHooks : Hooks.ActionBar {
        override fun formatDistanceMessage(player: Player, distance3D: Double, heightDifference: Double): Component =
            plugin.translations.POINTERS_ACTION_BAR_DISTANCE.withReplacements(
                "distance_3d" placeholder distance3D,
                "height_difference" placeholder heightDifference,
            )

        override fun formatWrongWorldMessage(player: Player, current: World, correct: World): Component =
            plugin.translations.POINTERS_ACTION_BAR_WRONG_WORLD.withReplacements(
                "current" placeholder plugin.worldTranslations.getWorldName(current),
                "correct" placeholder plugin.worldTranslations.getWorldName(correct),
            )
    }

    private inner class HologramPointerHooks : Hooks.Hologram {
        override fun formatHologramText(player: Player, trackable: Trackable) =
            when (trackable) {
                is Waypoint -> plugin.apiExtensions.run { trackable.getHologramTranslations() }
                is TemporaryWaypointTrackable -> plugin.translations.POINTERS_HOLOGRAM_TEMPORARY
                is PlayerTrackable -> plugin.translations.POINTERS_HOLOGRAM_PLAYER_TRACKING
                else -> null
            }?.withReplacements(*trackable.getReplacements(player)) ?: trackable.hologramText?.let { LegacyComponentSerializer.legacySection().deserialize(it) }

        private fun Trackable.getReplacements(player: Player): Array<TagResolver> {
            val resolvers = mutableListOf(
                "world" placeholder plugin.worldTranslations.getWorldName(location.world!!),
                "distance" placeholder player.location.distance(location).roundToLong(),
                "x" placeholder location.x,
                "y" placeholder location.y,
                "z" placeholder location.z,
                "block_x" placeholder location.blockX,
                "block_y" placeholder location.blockY,
                "block_z" placeholder location.blockZ,
            )
            if (this is Waypoint) {
                resolvers += "name" placeholder name
                resolvers += "created_at" placeholder createdAt
            }
            if (this is PlayerTrackable) {
                resolvers += "name" placeholder this.player.displayName()
            }
            return resolvers.toTypedArray()
        }

        override fun getIconMaterial(trackable: Trackable) = if (trackable is Waypoint) {
            trackable.material ?: plugin.apiExtensions.run { trackable.getIconMaterial() }
        } else null
    }
}