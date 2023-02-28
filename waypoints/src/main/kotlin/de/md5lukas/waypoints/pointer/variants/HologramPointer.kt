package de.md5lukas.waypoints.pointer.variants

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Trackable
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.config.pointers.HologramConfiguration
import de.md5lukas.waypoints.pointer.PlayerTrackable
import de.md5lukas.waypoints.pointer.Pointer
import de.md5lukas.waypoints.pointer.TemporaryWaypointTrackable
import de.md5lukas.waypoints.util.*
import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.roundToLong

class HologramPointer(
    plugin: WaypointsPlugin,
    private val config: HologramConfiguration
) : Pointer(plugin, config.interval) {

    private val hologramManager = HologramManager(plugin)

    private val activeHolograms: MutableMap<UUID, Hologram> = HashMap()

    override fun update(player: Player, trackable: Trackable, translatedTarget: Location?) {
        if (translatedTarget === null)
            return

        var hologramText = when (trackable) {
            is Waypoint -> plugin.apiExtensions.run { trackable.getHologramTranslations().text }
            is TemporaryWaypointTrackable -> plugin.translations.POINTERS_HOLOGRAM_TEMPORARY.text
            is PlayerTrackable -> plugin.translations.POINTERS_HOLOGRAM_PLAYER_TRACKING.text
            else -> trackable.hologramText
        }
        if (hologramText === null)
            return

        val hologramTarget = translatedTarget.clone()

        hologramTarget.y += config.hologramHeightOffset

        val distanceSquared = player.location.distanceSquared(translatedTarget)
        val maxDistance = config.distanceFromPlayerSquared

        val location = if (distanceSquared <= maxDistance) {
            hologramTarget
        } else {
            val pVec = player.eyeLocation.toVector()
            val tVec = hologramTarget.toVector()

            val direction = (tVec - pVec).normalize()

            val rayTrace = if (config.preventOcclusion) {
                player.world.rayTraceBlocks(player.eyeLocation, direction, config.distanceFromPlayer.toDouble(), FluidCollisionMode.NEVER, true)
            } else null

            if (rayTrace !== null) {
                // Move the hologram half a block closer to the player
                rayTrace.hitPosition.add(direction.multiply(-0.5))
            } else {
                val offset = direction.multiply(config.distanceFromPlayer)

                pVec.add(offset)
            }.toLocation(player.world)
        }

        hologramText = hologramText.runtimeReplace(trackable.getReplacements(player))

        if (player.uniqueId in activeHolograms) {
            val hologram = activeHolograms[player.uniqueId]!!

            hologram.location = location
            hologram.text = hologramText
            hologram.update()
        } else {
            activeHolograms[player.uniqueId] = hologramManager.createHologram(player, location, hologramText)
        }
    }

    private fun Trackable.getReplacements(player: Player): Map<String, String> {
        val map = mutableMapOf(
            "world" to plugin.worldTranslations.getWorldName(location.world!!),
            "distance" to player.location.distance(location).roundToLong().toString(),
            "x" to location.x.format(),
            "y" to location.y.format(),
            "z" to location.z.format(),
            "blockX" to location.blockX.toString(),
            "blockY" to location.blockY.toString(),
            "blockZ" to location.blockZ.toString(),
        )
        if (this is Waypoint) {
            map["name"] = name
            map["createdAt"] = createdAt.format(Formatters.SHORT_DATE_TIME_FORMATTER)
        }
        if (this is PlayerTrackable) {
            map["name"] = this.player.displayName
        }
        return map
    }

    override fun hide(player: Player, trackable: Trackable, translatedTarget: Location?) {
        activeHolograms.remove(player.uniqueId)?.destroy()
    }
}