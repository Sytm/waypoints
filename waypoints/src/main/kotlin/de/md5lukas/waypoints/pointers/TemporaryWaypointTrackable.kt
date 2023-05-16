package de.md5lukas.waypoints.pointers

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.util.getResolvers
import org.bukkit.Location
import org.bukkit.entity.Player

class TemporaryWaypointTrackable(
    private val plugin: WaypointsPlugin,
    override val location: Location,
    override val beaconColor: BeaconColor? = null,
) : StaticTrackable {

  override fun getHologramText(player: Player) =
      plugin.apiExtensions.run {
        plugin.translations.POINTERS_HOLOGRAM_TEMPORARY.withReplacements(
            *location.getResolvers(plugin, player))
      }
}
