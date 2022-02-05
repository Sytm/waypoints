package de.md5lukas.waypoints.pointer.variants

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.config.pointers.ParticleConfiguration
import de.md5lukas.waypoints.pointer.Pointer
import de.md5lukas.waypoints.util.divide
import org.bukkit.Location
import org.bukkit.entity.Player

class ParticlePointer(
    plugin: WaypointsPlugin,
    private val config: ParticleConfiguration,
) : Pointer(plugin, config.interval) {

    override fun update(player: Player, waypoint: Waypoint, translatedTarget: Location?) {
        if (translatedTarget !== null) {
            val pLoc = player.location
            var dir = translatedTarget.toVector().subtract(pLoc.toVector())

            if (!config.showVerticalDirection) {
                dir.y = 0.0
            }

            dir = dir.normalize().multiply(config.length)

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