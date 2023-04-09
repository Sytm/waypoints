package de.md5lukas.waypoints.pointers.variants

import de.md5lukas.waypoints.pointers.*
import de.md5lukas.waypoints.pointers.config.BeaconConfiguration
import de.md5lukas.waypoints.util.blockEquals
import de.md5lukas.waypoints.util.highestBlock
import de.md5lukas.waypoints.util.sendActualBlock
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.max

internal class BeaconPointer(
    pointerManager: PointerManager,
    private val config: BeaconConfiguration,
) : Pointer(pointerManager, config.interval) {

    private val BEACON = Material.BEACON.createBlockData()

    private val activeBeacons: MutableMap<UUID, Location> = HashMap()

    override fun update(player: Player, trackable: Trackable, translatedTarget: Location?) {
        if (trackable !is StaticTrackable)
            return
        if (translatedTarget !== null) {
            val distance = player.location.distanceSquared(translatedTarget)

            if (distance >= config.minDistanceSquared && distance < config.maxDistanceSquared) {
                val beaconBase = translatedTarget.highestBlock.location

                val lastBeaconPosition = activeBeacons[player.uniqueId]
                if (lastBeaconPosition != null && !lastBeaconPosition.blockEquals(beaconBase)) {
                    hide0(player, trackable, lastBeaconPosition)
                }

                activeBeacons[player.uniqueId] = beaconBase

                sendBeacon(player, beaconBase, trackable, true)
            } else {
                hide(player, trackable, translatedTarget)
            }
        }
    }

    override fun hide(player: Player, trackable: Trackable, translatedTarget: Location?) {
        if (trackable !is StaticTrackable)
            return
        hide0(player, trackable, activeBeacons.remove(player.uniqueId))
    }

    private fun hide0(player: Player, trackable: Trackable, lastBeaconPosition: Location?) {
        if (lastBeaconPosition != null && player.world == lastBeaconPosition.world) {
            sendBeacon(player, lastBeaconPosition, trackable, false)
        }
    }

    private fun sendBeacon(player: Player, beaconBase: Location, trackable: Trackable, create: Boolean) {
        val loc = beaconBase.clone()
        val world = loc.world!!

        loc.y = max(loc.blockY, world.minHeight + 2).toDouble()

        if (create) {
            player.sendBlockChange(
                loc,
                (if (trackable is StaticTrackable) {
                    trackable.beaconColor
                } else {
                    null
                } ?: config.getDefaultColor(trackable) ?: BeaconColor.CLEAR).blockData
            )
        } else {
            player.sendActualBlock(loc)
        }
        loc.y--
        if (create) {
            player.sendBlockChange(loc, BEACON)
        } else {
            player.sendActualBlock(loc)
        }
        loc.y--

        // Create or remove 3x3 base platform
        for (x in -1..1) {
            for (z in -1..1) {
                loc.add(x.toDouble(), 0.0, z.toDouble())
                if (create) {
                    player.sendBlockChange(loc, config.baseBlock)
                } else {
                    player.sendActualBlock(loc)
                }
                loc.subtract(x.toDouble(), 0.0, z.toDouble())
            }
        }
    }
}