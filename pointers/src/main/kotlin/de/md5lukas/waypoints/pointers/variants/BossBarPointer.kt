package de.md5lukas.waypoints.pointers.variants

import de.md5lukas.waypoints.pointers.Pointer
import de.md5lukas.waypoints.pointers.PointerManager
import de.md5lukas.waypoints.pointers.Trackable
import de.md5lukas.waypoints.pointers.config.BossBarConfiguration
import de.md5lukas.waypoints.util.getAngleToTarget
import de.md5lukas.waypoints.util.loopingSubstring
import de.md5lukas.waypoints.util.normalizeAngleTo360
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.roundToInt

internal class BossBarPointer(
    pointerManager: PointerManager,
    private val config: BossBarConfiguration,
) : Pointer(pointerManager, config.interval) {

    private val rawTitle: String
        get() = config.title

    private val bossBars: MutableMap<UUID, BarData> = mutableMapOf()

    override fun update(player: Player, trackable: Trackable, translatedTarget: Location?) {
        if (translatedTarget === null) {
            hide(player, trackable, null)
            return
        }

        // TODO properly convert to adventure

        val barData = bossBars.computeIfAbsent(player.uniqueId) {
            BarData(
                BossBar.bossBar(
                    Component.empty(),
                    1f,
                    config.barColor,
                    config.barStyle
                ).also {
                    player.showBossBar(it)
                },
                0,
                rawTitle,
            )
        }
        // Don't calculate the position of the indicator everytime in favour to make the compass update smoother
        barData.counter = (barData.counter + 1) % config.recalculateEveryNthInterval
        if (barData.counter == 0) {

            // Subtract 90° from the returned angle because Minecraft yaw is rotated by 90°
            val angleToTarget = normalizeAngleTo360(getAngleToTarget(player.location, translatedTarget) - 90)

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

        barData.bossBar.name(LegacyComponentSerializer.legacyAmpersand().deserialize(orientedTitle.toString()))
    }

    override fun hide(player: Player, trackable: Trackable, translatedTarget: Location?) {
        bossBars.remove(player.uniqueId)?.bossBar?.let {
            player.hideBossBar(it)
        }
    }

    private class BarData(val bossBar: BossBar, var counter: Int, var currentTitle: String)
}