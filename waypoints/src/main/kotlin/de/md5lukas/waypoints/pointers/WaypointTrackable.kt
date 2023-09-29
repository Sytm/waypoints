package de.md5lukas.waypoints.pointers

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Waypoint
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class WaypointTrackable(private val plugin: WaypointsPlugin, val waypoint: Waypoint) :
    StaticTrackable {

  object Extract : (Trackable) -> Waypoint? {
    override fun invoke(trackable: Trackable): Waypoint? {
      return (trackable as? WaypointTrackable)?.waypoint
    }
  }

  override val location: Location
    get() = waypoint.location

  override val beaconColor: BeaconColor?
    get() = waypoint.beaconColor?.let { BeaconColor.byMaterial(it) }

  override fun getHologramText(player: Player, translatedTarget: Location) =
      plugin.apiExtensions.run {
        waypoint
            .getHologramTranslations()
            .withReplacements(*waypoint.getResolvers(player, translatedTarget))
      }

  override val hologramItem =
      ItemStack(waypoint.material ?: plugin.apiExtensions.run { waypoint.getIconMaterial() })

  override fun equals(other: Any?): Boolean {
    return waypoint == (other as? WaypointTrackable)?.waypoint
  }

  override fun hashCode(): Int {
    return waypoint.hashCode()
  }
}
