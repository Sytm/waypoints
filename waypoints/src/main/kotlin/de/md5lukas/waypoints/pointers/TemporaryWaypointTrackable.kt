package de.md5lukas.waypoints.pointers

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.util.getResolvers
import kotlin.random.Random
import org.bukkit.Location
import org.bukkit.entity.Player

class TemporaryWaypointTrackable(
    private val plugin: WaypointsPlugin,
    override val location: Location,
    override val beaconColor: BeaconColor? = null,
) : StaticTrackable {

  override val seed: Long = Random.nextLong()

  override fun getHologramText(player: Player, translatedTarget: Location) =
      plugin.apiExtensions.run {
        plugin.translations.POINTERS_HOLOGRAM_TEMPORARY.withReplacements(
            *location.getResolvers(plugin, player, translatedTarget))
      }
}
