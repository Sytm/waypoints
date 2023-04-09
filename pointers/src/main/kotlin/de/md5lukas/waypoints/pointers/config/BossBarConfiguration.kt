package de.md5lukas.waypoints.pointers.config

import net.kyori.adventure.bossbar.BossBar.Color
import net.kyori.adventure.bossbar.BossBar.Overlay

interface BossBarConfiguration : RepeatingPointerConfiguration {

    val recalculateEveryNthInterval: Int

    val barColor: Color

    val barStyle: Overlay

    val title: String

    val indicator: Char

    val indicatorColor: String

    val normalColor: String
}