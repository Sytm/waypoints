package de.md5lukas.waypoints.pointers.config

import de.md5lukas.configurate.NonEmptyString
import de.md5lukas.configurate.Positive
import net.kyori.adventure.bossbar.BossBar.Color
import net.kyori.adventure.bossbar.BossBar.Overlay
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
class BossBarConfiguration : RepeatingPointerConfiguration {

  override var enabled = true
    private set

  @Comment(
      "The tick interval where the boss bar is updated to account for the rotation of the player")
  @Positive
  override var interval = 4
    private set

  @Comment(
      "Only every n intervals the angle between the player and the waypoints is calculated to save computational power")
  @Positive
  var recalculateEveryNthInterval: Int = 5
    private set

  @Comment(
      """
    Color of the boss bar
    Available values: pink, blue, red, green, yellow, purple, white
  """)
  var barColor: Color = Color.BLUE
    private set

  @Comment(
      """
    Style of the boss bar
    Available values: progress, notched_6, notched_10, notched_12, notched_20
  """)
  var barStyle: Overlay = Overlay.PROGRESS
    private set

  @NonEmptyString
  var title: String = " · · ◈ · · ◈ · · E · · ◈ · · ◈ · · S · · ◈ · · ◈ · · W · · ◈ · · ◈ · · N"
    private set

  @NonEmptyString
  var indicator: String = "⬛"
    private set

  @Comment("For example add <bold> if the indicator should be bold. The colors are randomized")
  var indicatorStyle: Style = Style.empty()
    private set

  var normalColor: Style = Style.style(NamedTextColor.WHITE)
    private set
}
