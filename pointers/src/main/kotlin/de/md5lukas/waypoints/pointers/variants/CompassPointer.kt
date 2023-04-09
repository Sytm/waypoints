package de.md5lukas.waypoints.pointers.variants

import de.md5lukas.waypoints.pointers.Pointer
import de.md5lukas.waypoints.pointers.PointerManager
import de.md5lukas.waypoints.pointers.Trackable
import de.md5lukas.waypoints.pointers.config.CompassConfiguration
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.CompassMeta

internal class CompassPointer(
    pointerManager: PointerManager,
    private val config: CompassConfiguration
) : Pointer(pointerManager, config.interval) {

    override fun show(player: Player, trackable: Trackable, translatedTarget: Location?) {
        val currentCompassTarget = player.compassTarget
        pointerManager.plugin.server.scheduler.runTaskAsynchronously(pointerManager.plugin, Runnable {
            pointerManager.hooks.saveCompassTarget(player, currentCompassTarget)
        })
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
        pointerManager.plugin.server.scheduler.runTaskAsynchronously(pointerManager.plugin, Runnable {
            pointerManager.hooks.loadCompassTarget(player)?.let {
                player.compassTarget = it
            }
        })
        if (config.netherSupport && (translatedTarget ?: trackable.location).world?.environment === World.Environment.NETHER) {
            player.inventory.filter { it?.type === Material.COMPASS }.forEach {
                val meta = it.itemMeta as CompassMeta
                meta.lodestone = null
                it.itemMeta = meta
            }
        }
    }
}