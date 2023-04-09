package de.md5lukas.waypoints.pointers.variants

import de.md5lukas.waypoints.pointers.Pointer
import de.md5lukas.waypoints.pointers.PointerManager
import de.md5lukas.waypoints.pointers.Trackable
import de.md5lukas.waypoints.pointers.config.ParticleConfiguration
import de.md5lukas.waypoints.pointers.util.div
import org.bukkit.Location
import org.bukkit.entity.Player

internal class ParticlePointer(
    pointerManager: PointerManager,
    private val config: ParticleConfiguration,
) : Pointer(pointerManager, config.interval) {

    override fun update(player: Player, trackable: Trackable, translatedTarget: Location?) {
        if (translatedTarget !== null) {
            val pLoc = player.location
            var dir = translatedTarget.toVector().subtract(pLoc.toVector())

            if (!config.showVerticalDirection) {
                dir.y = 0.0
            }

            dir = dir.normalize().multiply(config.length)

            dir /= config.amount

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