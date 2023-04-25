package de.md5lukas.waypoints.config.pointers

import de.md5lukas.konfig.Configurable
import de.md5lukas.waypoints.pointers.config.BossBarConfiguration
import net.kyori.adventure.bossbar.BossBar.Color
import net.kyori.adventure.bossbar.BossBar.Overlay
import net.kyori.adventure.text.format.Style

@Configurable
class BossBarConfigurationImpl : RepeatingPointerConfigurationImpl(), BossBarConfiguration {

    override var recalculateEveryNthInterval: Int = 0
        private set

    override var barColor: Color = Color.PINK
        private set

    override var barStyle: Overlay = Overlay.PROGRESS
        private set

    override var title: String = ""
        private set

    override var indicator: String = "#"
        private set

    override var indicatorColor: Style = Style.empty()
        private set

    override var normalColor: Style = Style.empty()
        private set
}