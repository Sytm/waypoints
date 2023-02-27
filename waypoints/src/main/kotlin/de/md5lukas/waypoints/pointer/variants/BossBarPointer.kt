package de.md5lukas.waypoints.pointer.variants

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Trackable
import de.md5lukas.waypoints.config.pointers.BossBarConfiguration
import de.md5lukas.waypoints.pointer.Pointer
import de.md5lukas.waypoints.util.getAngleToTarget
import de.md5lukas.waypoints.util.loopingSubstring
import de.md5lukas.waypoints.util.normalizeAngleTo360
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.roundToInt

class BossBarPointer(
    plugin: WaypointsPlugin,
    private val config: BossBarConfiguration,
) : Pointer(plugin, config.interval) {

    private val rawTitle: String
        get() = config.title

    private val bossBars: MutableMap<UUID, BarData> = mutableMapOf()

    override fun update(player: Player, trackable: Trackable, translatedTarget: Location?) {
        val target = translatedTarget ?: trackable.location

        if (player.world !== target.world) {
            hide(player, trackable, translatedTarget)
            return
        }

        val barData = bossBars.computeIfAbsent(player.uniqueId) {
            BarData(
                Bukkit.createBossBar(
                    "",
                    config.barColor,
                    config.barStyle
                ).also {
                    it.addPlayer(player)
                },
                0,
                rawTitle,
            )
        }
        // Don't calculate the position of the indicator everytime in favour to make the compass update smoother
        barData.counter = (barData.counter + 1) % config.recalculateEveryNthInterval
        if (barData.counter == 0) {

            // Subtract 90° from the returned angle because Minecraft yaw is rotated by 90°
            val angleToTarget = normalizeAngleTo360(getAngleToTarget(player.location, target) - 90)

            // Subtract 1 from the index to, because otherwise the indicator is always one too much to the right
            val replaceIndex = Math.floorMod((angleToTarget / 360 * rawTitle.length).roundToInt() - 1, rawTitle.length)

            val titleBuilder = StringBuilder(rawTitle)
            titleBuilder[replaceIndex] = config.indicator

            barData.currentTitle = titleBuilder.toString()
        }

        val playerAngle = normalizeAngleTo360(player.location.yaw)
        val offset = playerAngle / 360 * rawTitle.length
        val orientedTitle = StringBuilder(barData.currentTitle.loopingSubstring(rawTitle.length - 1, offset.roundToInt()))

        // Add colors after orienting the title because they would interfere with the substrings sizes and offsets
        val indicatorIndex = orientedTitle.indexOf(config.indicator)

        if (indicatorIndex >= 0) {
            orientedTitle.insert(indicatorIndex + 1, config.normalColor)
            orientedTitle.insert(indicatorIndex, config.indicatorColor)
        }
        // If the indicator is not the first character (or not contained at all) we need to color everything before of it
        if (indicatorIndex != 0) {
            orientedTitle.insert(0, config.normalColor)
        }

        barData.bossBar.setTitle(orientedTitle.toString())
    }

    override fun hide(player: Player, trackable: Trackable, translatedTarget: Location?) {
        bossBars.remove(player.uniqueId)?.bossBar?.removeAll()
    }

    private class BarData(val bossBar: BossBar, var counter: Int, var currentTitle: String)
}