package de.md5lukas.waypoints.pointers

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Waypoint
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class WaypointTrackable(private val plugin: WaypointsPlugin, val waypoint: Waypoint) : StaticTrackable {
    override val location: Location
        get() = waypoint.location

    override val beaconColor: BeaconColor?
        get() = waypoint.beaconColor

    override fun getHologramText(player: Player) =
        plugin.apiExtensions.run { waypoint.getHologramTranslations().withReplacements(*waypoint.getResolvers(player)) }


    override val hologramItem = ItemStack(waypoint.material ?: plugin.apiExtensions.run { waypoint.getIconMaterial() })

}