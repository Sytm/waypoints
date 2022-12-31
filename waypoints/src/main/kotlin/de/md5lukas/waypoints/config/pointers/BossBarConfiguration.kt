package de.md5lukas.waypoints.config.pointers

import de.md5lukas.waypoints.util.getStringNotNull
import de.md5lukas.waypoints.util.translateColorCodes
import net.md_5.bungee.api.ChatColor
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.configuration.ConfigurationSection

class BossBarConfiguration {

    var enabled: Boolean = false
        private set

    var interval: Int = 0
        private set

    var recalculateEveryNthInterval: Int = 0
        private set

    var barColor: BarColor = BarColor.PINK
        private set

    var barStyle: BarStyle = BarStyle.SOLID
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

        barColor = cfg.getStringNotNull("barColor").let { BarColor.valueOf(it) }

        barStyle = cfg.getStringNotNull("barStyle").let { BarStyle.valueOf(it) }

        title = cfg.getStringNotNull("title")

        indicator = cfg.getStringNotNull("indicator").first()

        if (indicator in title) {
            throw IllegalArgumentException("The indicator cannot be already contained in the title for the boss bar pointer")
        }

        indicatorColor = "${ChatColor.RESET}${cfg.getStringNotNull("indicatorColor").translateColorCodes()}"

        normalColor = "${ChatColor.RESET}${cfg.getStringNotNull("normalColor").translateColorCodes()}"
    }
}