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
import java.util.logging.Level

class HologramPointer(
    plugin: WaypointsPlugin,
    private val config: HologramConfiguration
) : Pointer(plugin, config.interval) {

    private var protocolLibSupportsVersion = true

    private val hologramManager = HologramManager(plugin)

    private val activeHolograms: MutableMap<UUID, Hologram> = HashMap()

    override fun update(player: Player, trackable: Trackable, translatedTarget: Location?) {
        if (!protocolLibSupportsVersion)
            return
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
            try {
                if (player.location.distanceSquared(translatedTarget) < config.maxDistance) {
                    activeHolograms.computeIfAbsent(player.uniqueId) {
                        hologramManager.createHologram(translatedTarget, localHologramText)
                    }.spawn(player)
                }
            } catch (e: Exception) {
                plugin.logger.log(
                    Level.SEVERE, "An issue occurred trying to create a hologram using ProtocolLib." +
                            " Automatically disabling Holograms until the next restart.", e
                )
                protocolLibSupportsVersion = false
            }
        }
    }

    override fun hide(player: Player, trackable: Trackable, translatedTarget: Location?) {
        if (trackable !is StaticTrackable)
            return
        activeHolograms.remove(player.uniqueId)?.destroy(player)
    }
}