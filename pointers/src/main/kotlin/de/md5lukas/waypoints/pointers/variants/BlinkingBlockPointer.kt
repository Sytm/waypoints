package de.md5lukas.waypoints.pointers.variants

import de.md5lukas.waypoints.pointers.Pointer
import de.md5lukas.waypoints.pointers.PointerManager
import de.md5lukas.waypoints.pointers.StaticTrackable
import de.md5lukas.waypoints.pointers.Trackable
import de.md5lukas.waypoints.pointers.config.BlinkingBlockConfiguration
import de.md5lukas.waypoints.pointers.util.sendActualBlock
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

internal class BlinkingBlockPointer(
    pointerManager: PointerManager,
    private val config: BlinkingBlockConfiguration
) : Pointer(pointerManager, config.interval) {

    private val counters: MutableMap<UUID, Int> = HashMap()
    private val lastLocations: MutableMap<UUID, Location> = HashMap()

    override fun update(player: Player, trackable: Trackable, translatedTarget: Location?) {
        if (trackable !is StaticTrackable)
            return
        if (translatedTarget !== null) {
            val distance = player.location.distanceSquared(translatedTarget)
            if (distance >= config.minDistanceSquared && distance < config.maxDistanceSquared) {
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
        if (trackable !is StaticTrackable)
            return
        val lastLocation = lastLocations.remove(player.uniqueId)
        if (counters.remove(player.uniqueId) !== null && lastLocation !== null) {
            player.sendActualBlock(lastLocation)
        }
    }
}