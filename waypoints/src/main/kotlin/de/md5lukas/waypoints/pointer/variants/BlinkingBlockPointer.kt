package de.md5lukas.waypoints.pointer.variants

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Trackable
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.config.pointers.BlinkingBlockConfiguration
import de.md5lukas.waypoints.pointer.Pointer
import de.md5lukas.waypoints.util.sendActualBlock
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

class BlinkingBlockPointer(
    plugin: WaypointsPlugin,
    private val config: BlinkingBlockConfiguration
) : Pointer(plugin, config.interval) {

    private val counters: MutableMap<UUID, Int> = HashMap()
    private val lastLocations: MutableMap<UUID, Location> = HashMap()

    override fun update(player: Player, trackable: Trackable, translatedTarget: Location?) {
        if (trackable !is Waypoint)
            return
        if (translatedTarget !== null) {
            val distance = player.location.distanceSquared(translatedTarget)
            if (distance >= config.minDistance && distance < config.maxDistance) {
                val currentCounter = counters.compute(player.uniqueId) { _, count ->
                    if (count == null) {
                        0
                    } else {
                        (count + 1) % config.blockDataSequence.size
                    }
                }!!

                lastLocations[player.uniqueId] = translatedTarget
                player.sendBlockChange(translatedTarget, config.blockDataSequence[currentCounter])
            } else {
                hide(player, trackable, translatedTarget)
            }
        } else {
            hide(player, trackable, null)
        }
    }

    override fun hide(player: Player, trackable: Trackable, translatedTarget: Location?) {
        if (trackable !is Waypoint)
            return
        val lastLocation = lastLocations.remove(player.uniqueId)
        if (counters.remove(player.uniqueId) !== null && lastLocation !== null) {
            player.sendActualBlock(lastLocation)
        }
    }
}