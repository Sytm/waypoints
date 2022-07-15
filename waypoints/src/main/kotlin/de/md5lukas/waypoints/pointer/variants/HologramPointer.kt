package de.md5lukas.waypoints.pointer.variants

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.StaticTrackable
import de.md5lukas.waypoints.api.Trackable
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.config.pointers.HologramConfiguration
import de.md5lukas.waypoints.pointer.Pointer
import de.md5lukas.waypoints.pointer.TemporaryWaypointTrackable
import de.md5lukas.waypoints.util.Hologram
import de.md5lukas.waypoints.util.HologramManager
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

class HologramPointer(
    plugin: WaypointsPlugin,
    private val config: HologramConfiguration
) : Pointer(plugin, config.interval) {

    private val hologramManager = HologramManager(plugin)

    private val activeHolograms: MutableMap<UUID, Hologram> = HashMap()

    override fun update(player: Player, trackable: Trackable, translatedTarget: Location?) {
        if (trackable !is StaticTrackable)
            return
        val localHologramText = when (trackable) {
            is Waypoint -> plugin.apiExtensions.run { trackable.getHologramName() }
            is TemporaryWaypointTrackable -> plugin.translations.POINTERS_HOLOGRAM_TEMPORARY.text
            else -> trackable.hologramText
        }
        if (localHologramText === null)
            return

        if (translatedTarget !== null) {
            if (player.location.distanceSquared(translatedTarget) < config.maxDistance) {
                activeHolograms.computeIfAbsent(player.uniqueId) {
                    hologramManager.createHologram(translatedTarget, localHologramText)
                }.spawn(player)
            }
        }
    }

    override fun hide(player: Player, trackable: Trackable, translatedTarget: Location?) {
        if (trackable !is StaticTrackable)
            return
        activeHolograms.remove(player.uniqueId)?.destroy(player)
    }
}