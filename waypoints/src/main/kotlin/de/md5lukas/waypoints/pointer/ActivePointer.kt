package de.md5lukas.waypoints.pointer

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Trackable
import org.bukkit.Location
import org.bukkit.entity.Player
import kotlin.math.floor

data class ActivePointer(
    private val plugin: WaypointsPlugin,
    private val player: Player,
    val trackable: Trackable,
) {

    val translatedTarget: Location?
        get() {
            if (player.world === trackable.location.world) {
                return trackable.location
            }

            plugin.waypointsConfig.general.connectedWorlds.worlds.forEach {
                if (it.primary == player.world.name && it.secondary == trackable.location.world?.name
                    || it.secondary == player.world.name && it.primary == trackable.location.world?.name
                ) {
                    val target = trackable.location.clone()
                    target.world = player.world

                    if (player.world.name == it.primary) {
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
