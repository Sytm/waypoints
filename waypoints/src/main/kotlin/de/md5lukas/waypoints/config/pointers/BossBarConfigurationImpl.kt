package de.md5lukas.waypoints.config.pointers

import de.md5lukas.waypoints.pointers.config.BossBarConfiguration
import de.md5lukas.waypoints.util.getStringNotNull
import net.kyori.adventure.bossbar.BossBar.Color
import net.kyori.adventure.bossbar.BossBar.Overlay
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

    override var indicator: Char = '#'
        private set

    override var indicatorColor: String = ""
        private set

    override var normalColor: String = ""
        private set

    override fun loadFromConfiguration(cfg: ConfigurationSection) {
        super.loadFromConfiguration(cfg)

        with(cfg) {
            recalculateEveryNthInterval = getInt("recalculateEveryNthInterval")

            barColor = getStringNotNull("barColor").let { Color.NAMES.valueOrThrow(it) }

            barStyle = getStringNotNull("barStyle").let { Overlay.NAMES.valueOrThrow(it) }

            title = getStringNotNull("title")

            indicator = getStringNotNull("indicator").first()

            if (indicator in title) {
                throw IllegalArgumentException("The indicator cannot be already contained in the title for the boss bar pointer")
            }

            indicatorColor = "&r${getStringNotNull("indicatorColor")}"

            normalColor = "&r${getStringNotNull("normalColor")}"
        }
    }
}