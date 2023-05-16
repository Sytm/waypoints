package de.md5lukas.waypoints.pointers.variants

import de.md5lukas.schedulers.AbstractScheduler
import de.md5lukas.waypoints.pointers.Pointer
import de.md5lukas.waypoints.pointers.PointerManager
import de.md5lukas.waypoints.pointers.Trackable
import de.md5lukas.waypoints.pointers.config.BossBarConfiguration
import de.md5lukas.waypoints.pointers.util.getAngleToTarget
import de.md5lukas.waypoints.pointers.util.normalizeAngleTo360
import de.md5lukas.waypoints.pointers.util.textComponent
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.entity.Player

internal class BossBarPointer(
    pointerManager: PointerManager,
    player: Player,
    scheduler: AbstractScheduler,
    private val config: BossBarConfiguration,
) : Pointer(pointerManager, player, scheduler) {

  override val interval: Int
    get() = config.interval

  override val supportsMultipleTargets: Boolean
    get() = false

  private val rawTitle: String
    get() = config.title

  private var bossBar: BarData? = null

  override fun update(trackable: Trackable, translatedTarget: Location?) {
    if (translatedTarget === null) {
      hide(trackable, null)
      return
    }

    val barData =
        bossBar
            ?: BarData(
                BossBar.bossBar(Component.empty(), 1f, config.barColor, config.barStyle).also {
                  player.showBossBar(it)
                },
                0,
                0f,
            )

    bossBar = barData

    // Don't calculate the position of the indicator everytime in favour to make the compass update
    // smoother
    barData.counter = (barData.counter + 1) % config.recalculateEveryNthInterval
    if (barData.counter == 0) {
      // Subtract 90° from the returned angle because Minecraft yaw is rotated by 90°
      barData.angle = normalizeAngleTo360(getAngleToTarget(player.location, translatedTarget) - 90)
    }

    val playerAngle = normalizeAngleTo360(player.location.yaw)
    val offset = (playerAngle / 360 * rawTitle.length).roundToInt()
    val orientedTitle = config.title.loopingSubstring(rawTitle.length - 1, offset)

    // Subtract 1 from the index to, because otherwise the indicator is always one too much to the
    // right
    val indicatorIndex = (barData.angle / 360 * rawTitle.length).roundToInt() - 1
    val offsetIndicatorIndex = Math.floorMod(indicatorIndex - offset, rawTitle.length)

    barData.bossBar.name(
        textComponent {
          style(config.normalColor)
          content(orientedTitle.substring(0, offsetIndicatorIndex))
          append(
              textComponent {
                style(config.indicatorColor)
                content(config.indicator)
              })
          append(
              Component.text(
                  orientedTitle.substring(min(orientedTitle.length, offsetIndicatorIndex + 1))))
        })
  }

  override fun hide(trackable: Trackable, translatedTarget: Location?) {
    bossBar?.bossBar?.let { player.hideBossBar(it) }
    bossBar = null
  }

  private data class BarData(val bossBar: BossBar, var counter: Int, var angle: Float)

  private fun String.loopingSubstring(size: Int, offset: Int): String {
    val modOffset = Math.floorMod(offset, length)

    val overhang = max(0, (size + modOffset) - length)

    return substring(modOffset, min(length, modOffset + size)) + substring(0, overhang)
  }
}
