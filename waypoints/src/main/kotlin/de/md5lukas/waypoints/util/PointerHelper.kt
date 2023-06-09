package de.md5lukas.waypoints.util

import de.md5lukas.commons.paper.placeholder
import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.pointers.PlayerTrackable
import de.md5lukas.waypoints.pointers.TemporaryWaypointTrackable
import de.md5lukas.waypoints.pointers.Trackable
import de.md5lukas.waypoints.pointers.WaypointTrackable
import net.kyori.adventure.text.Component

suspend fun formatCurrentTargets(
    plugin: WaypointsPlugin,
    trackables: Collection<Trackable>
): List<Component> {
  return trackables.map { trackable ->
    when (trackable) {
      is WaypointTrackable ->
          trackable.waypoint.let {
            plugin.apiExtensions.run {
              when (it.type) {
                Type.PRIVATE -> plugin.translations.OVERVIEW_DESELECT_NAMES_WAYPOINT_PRIVATE
                Type.DEATH -> plugin.translations.OVERVIEW_DESELECT_NAMES_WAYPOINT_DEATH
                Type.PUBLIC -> plugin.translations.OVERVIEW_DESELECT_NAMES_WAYPOINT_PUBLIC
                Type.PERMISSION -> plugin.translations.OVERVIEW_DESELECT_NAMES_WAYPOINT_PERMISSION
              }.withReplacements(
                  "path" placeholder it.getFullPath(),
                  "created_at" placeholder it.createdAt,
              )
            }
          }
      is PlayerTrackable ->
          trackable.player.let {
            plugin.translations.OVERVIEW_DESELECT_NAMES_PLAYER_TRACKING.withReplacements(
                "name" placeholder it.displayName())
          }
      is TemporaryWaypointTrackable ->
          plugin.translations.OVERVIEW_DESELECT_NAMES_WAYPOINT_TEMPORARY.text
      else -> plugin.translations.OVERVIEW_DESELECT_NAMES_UNKNOWN.text
    }
  }
}
