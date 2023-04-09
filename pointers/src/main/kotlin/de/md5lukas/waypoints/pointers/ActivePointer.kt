package de.md5lukas.waypoints.pointers

import org.bukkit.Location
import org.bukkit.entity.Player
import kotlin.math.floor

internal data class ActivePointer(
    private val pointerManager: PointerManager,
    private val player: Player,
    val trackable: Trackable,
) {

    val translatedTarget: Location?
        get() {
            if (player.world === trackable.location.world) {
                return trackable.location
            }

            pointerManager.configuration.connectedWorlds.entries.forEach {
                if (it.key == player.world.name && it.value == trackable.location.world?.name
                    || it.value == player.world.name && it.key == trackable.location.world?.name
                ) {
                    val target = trackable.location.clone()
                    target.world = player.world

                    if (player.world.name == it.key) {
                        target.x *= 8
                        target.z *= 8
                    } else {
                        target.x = floor(target.x / 8)
                        target.z = floor(target.z / 8)
                    }

                    return target
                }
            }

            return null
        }
}
