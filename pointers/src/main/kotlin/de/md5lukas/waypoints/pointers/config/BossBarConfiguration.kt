package de.md5lukas.waypoints.pointers.config

import net.kyori.adventure.bossbar.BossBar.Color
import net.kyori.adventure.bossbar.BossBar.Overlay
import net.kyori.adventure.text.format.Style

interface BossBarConfiguration : RepeatingPointerConfiguration {

  val recalculateEveryNthInterval: Int

  val barColor: Color

  val barStyle: Overlay

  val title: String

  val indicator: String

  val indicatorStyle: Style

  val normalColor: Style
}
