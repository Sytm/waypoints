package de.md5lukas.waypoints.pointer.variants

import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.config.pointers.ParticleConfiguration
import de.md5lukas.waypoints.pointer.Pointer
import de.md5lukas.waypoints.util.divide
import org.bukkit.entity.Player

class ParticlePointer(
    private val config: ParticleConfiguration,
) : Pointer(config.interval) {

    override fun update(player: Player, waypoint: Waypoint) {
        val loc = waypoint.location
        if (player.world == loc.world) {
            val pLoc = player.location
            val dir = loc.toVector().subtract(pLoc.toVector()).normalize().multiply(config.length)

            dir.divide(config.amount)

            for (i in 0 until config.amount) {
                var y = config.heightOffset

                if (config.showVerticalDirection) {
                    y += dir.y * i
                }

                player.spawnParticle(
                    config.particle,
                    pLoc.x + (dir.x * i),
                    pLoc.y + y,
                    pLoc.z + (dir.z * i),
                    1, 0.0, 0.0, 0.0, 0.0,
                )
            }
        }
    }
}