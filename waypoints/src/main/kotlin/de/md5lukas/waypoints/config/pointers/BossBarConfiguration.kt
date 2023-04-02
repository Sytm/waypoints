package de.md5lukas.waypoints.config.pointers

import de.md5lukas.waypoints.util.getStringNotNull
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.configuration.ConfigurationSection

class BossBarConfiguration {

    var enabled: Boolean = false
        private set

    var interval: Int = 0
        private set

    var recalculateEveryNthInterval: Int = 0
        private set

    var barColor: BossBar.Color = BossBar.Color.PINK
        private set

    var barStyle: BossBar.Overlay = BossBar.Overlay.PROGRESS
        private set

    var title: String = ""
        private set

    var indicator: Char = '#'
        private set

    var indicatorColor: String = ""
        private set

    var normalColor: String = ""
        private set

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        enabled = cfg.getBoolean("enabled")

        interval = cfg.getInt("interval")

        recalculateEveryNthInterval = cfg.getInt("recalculateEveryNthInterval")


        barColor = cfg.getStringNotNull("barColor").let { BossBar.Color.NAMES.valueOrThrow(it) }

        barStyle = cfg.getStringNotNull("barStyle").let { BossBar.Overlay.NAMES.valueOrThrow(it) }

        title = cfg.getStringNotNull("title")

        indicator = cfg.getStringNotNull("indicator").first()

        if (indicator in title) {
            throw IllegalArgumentException("The indicator cannot be already contained in the title for the boss bar pointer")
        }

        indicatorColor = "&r${cfg.getStringNotNull("indicatorColor")}"

        normalColor = "&r${cfg.getStringNotNull("normalColor")}"
    }
}