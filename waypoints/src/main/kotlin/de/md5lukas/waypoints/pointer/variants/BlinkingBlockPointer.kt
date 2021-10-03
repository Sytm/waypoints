package de.md5lukas.waypoints.pointer.variants

import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.config.pointers.BlinkingBlockConfiguration
import de.md5lukas.waypoints.pointer.Pointer
import de.md5lukas.waypoints.util.sendActualBlock
import org.bukkit.entity.Player
import java.util.*

class BlinkingBlockPointer(
    private val config: BlinkingBlockConfiguration
) : Pointer(config.interval) {

    private val counters: MutableMap<UUID, Int> = HashMap()

    override fun update(player: Player, waypoint: Waypoint) {
        val loc = waypoint.location
        if (player.world == loc.world) {
            val distance = player.location.distanceSquared(loc)
            if (distance >= config.minDistance && distance < config.maxDistance) {
                val currentCounter = counters.compute(player.uniqueId) { _, count ->
                    if (count == null) {
                        0
                    } else {
                        (count + 1) % config.blockDataSequence.size
                    }
                }!!

                player.sendBlockChange(loc, config.blockDataSequence[currentCounter])
            } else {
                hide(player, waypoint)
            }
        }
    }

    override fun hide(player: Player, waypoint: Waypoint) {
        counters.remove(player.uniqueId)

        if (player.world == waypoint.location.world) {
            player.sendActualBlock(waypoint.location)
        }
    }
}