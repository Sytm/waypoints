package de.md5lukas.waypoints.pointer.variants

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Trackable
import de.md5lukas.waypoints.config.pointers.CompassConfiguration
import de.md5lukas.waypoints.pointer.Pointer
import de.md5lukas.waypoints.util.runTaskAsync
import org.bukkit.Location
import org.bukkit.entity.Player

class CompassPointer(
    plugin: WaypointsPlugin,
    config: CompassConfiguration
) : Pointer(plugin, config.interval) {

    override fun show(player: Player, trackable: Trackable, translatedTarget: Location?) {
        val currentCompassTarget = player.compassTarget
        plugin.runTaskAsync {
            plugin.api.getWaypointPlayer(player.uniqueId).setCompassTarget(currentCompassTarget)
        }
        update(player, trackable, translatedTarget)
    }

    override fun update(player: Player, trackable: Trackable, translatedTarget: Location?) {
        player.compassTarget = translatedTarget ?: trackable.location
    }

    override fun hide(player: Player, trackable: Trackable, translatedTarget: Location?) {
        plugin.runTaskAsync {
            plugin.api.getWaypointPlayer(player.uniqueId).getCompassTarget()?.let {
                player.compassTarget = it
            }
        }
    }
}