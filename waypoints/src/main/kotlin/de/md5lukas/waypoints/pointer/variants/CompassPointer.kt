package de.md5lukas.waypoints.pointer.variants

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Trackable
import de.md5lukas.waypoints.config.pointers.CompassConfiguration
import de.md5lukas.waypoints.pointer.Pointer
import de.md5lukas.waypoints.util.runTaskAsync
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.CompassMeta

class CompassPointer(
    plugin: WaypointsPlugin,
    private val config: CompassConfiguration
) : Pointer(plugin, config.interval) {

    override fun show(player: Player, trackable: Trackable, translatedTarget: Location?) {
        val currentCompassTarget = player.compassTarget
        plugin.runTaskAsync {
            plugin.api.getWaypointPlayer(player.uniqueId).setCompassTarget(currentCompassTarget)
        }
        update(player, trackable, translatedTarget)
    }

    override fun update(player: Player, trackable: Trackable, translatedTarget: Location?) {
        val location = translatedTarget ?: trackable.location
        player.compassTarget = location

        val world = location.world!!
        if (config.netherSupport && world.environment === World.Environment.NETHER) {
            player.inventory.filter { it?.type === Material.COMPASS }.forEach {
                val meta = it.itemMeta as CompassMeta
                meta.lodestone = location
                it.itemMeta = meta
            }
        }
    }

    override fun hide(player: Player, trackable: Trackable, translatedTarget: Location?) {
        plugin.runTaskAsync {
            plugin.api.getWaypointPlayer(player.uniqueId).getCompassTarget()?.let {
                player.compassTarget = it
            }
        }
        if (config.netherSupport && (translatedTarget ?: trackable.location).world?.environment === World.Environment.NETHER) {
            player.inventory.filter { it?.type === Material.COMPASS }.forEach {
                val meta = it.itemMeta as CompassMeta
                meta.lodestone = null
                it.itemMeta = meta
            }
        }
    }
}