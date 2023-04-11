package de.md5lukas.waypoints.config.pointers

import de.md5lukas.waypoints.pointers.config.BossBarConfiguration
import de.md5lukas.waypoints.util.getStringNotNull
import net.kyori.adventure.bossbar.BossBar.Color
import net.kyori.adventure.bossbar.BossBar.Overlay
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.configuration.ConfigurationSection

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

    override fun loadFromConfiguration(cfg: ConfigurationSection) {
        super.loadFromConfiguration(cfg)

        with(cfg) {
            recalculateEveryNthInterval = getInt("recalculateEveryNthInterval")

            barColor = getStringNotNull("barColor").let { Color.NAMES.valueOrThrow(it) }

            barStyle = getStringNotNull("barStyle").let { Overlay.NAMES.valueOrThrow(it) }

            title = getStringNotNull("title")

            indicator = getStringNotNull("indicator").first().toString()

            indicatorColor = MiniMessage.miniMessage().deserialize(getStringNotNull("indicatorColor")).style()

            normalColor = MiniMessage.miniMessage().deserialize(getStringNotNull("normalColor")).style()
        }
    }
}