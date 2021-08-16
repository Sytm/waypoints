package de.md5lukas.waypoints.pointer.variants

import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.config.pointers.BeaconConfiguration
import de.md5lukas.waypoints.pointer.Pointer
import de.md5lukas.waypoints.util.blockEquals
import de.md5lukas.waypoints.util.getHighestBlock
import de.md5lukas.waypoints.util.sendActualBlock
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*

class BeaconPointer(
    private val config: BeaconConfiguration,
) : Pointer(config.interval) {

    private val BEACON = Material.BEACON.createBlockData()

    private val activeBeacons: MutableMap<UUID, Location> = HashMap()

    override fun update(player: Player, waypoint: Waypoint) {
        val loc = waypoint.location

        if (player.world == loc.world) {
            val distance = player.location.distanceSquared(loc)

            if (distance >= config.minDistance && distance < config.maxDistance) {
                val beaconBase = loc.getHighestBlock().location

                val lastBeaconPosition = activeBeacons[player.uniqueId]
                if (lastBeaconPosition != null && !lastBeaconPosition.blockEquals(beaconBase))  {
                    hide(player, waypoint, lastBeaconPosition)
                }

                activeBeacons[player.uniqueId] = beaconBase

                sendBeacon(player, beaconBase, waypoint, true)
            } else {
                hide(player, waypoint)
            }
        }
    }

    override fun hide(player: Player, waypoint: Waypoint) {
        hide(player, waypoint, activeBeacons.remove(player.uniqueId))
    }

    private fun hide(player: Player, waypoint: Waypoint, lastBeaconPosition: Location?) {
        if (lastBeaconPosition != null && player.world == lastBeaconPosition.world) {
            sendBeacon(player, lastBeaconPosition, waypoint, false)
        }
    }

    private fun sendBeacon(player: Player, beaconBase: Location, waypoint: Waypoint, create: Boolean) {
        val loc = beaconBase.clone()

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

        loc.add(0.0, 1.0, 0.0)
        if (create) {
            player.sendBlockChange(loc, BEACON)

            loc.add(0.0, 1.0, 0.0)

            player.sendBlockChange(loc, (waypoint.beaconColor ?: config.defaultColor[waypoint.type]!!).blockData)
        } else {
            player.sendActualBlock(loc)

            loc.add(0.0, 1.0, 0.0)

            player.sendActualBlock(loc)
        }
    }
}