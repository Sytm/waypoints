package de.md5lukas.waypoints.config.general

import de.md5lukas.commons.time.DurationParser
import de.md5lukas.waypoints.util.Expression
import de.md5lukas.waypoints.util.MathParser
import de.md5lukas.waypoints.util.getStringNotNull
import org.bukkit.configuration.ConfigurationSection
import java.util.concurrent.TimeUnit

class TeleportConfiguration {

    var cooldown: Long = 0
        private set

    var standStillTime: Long = 0
        private set

    val private = TeleportPaymentConfiguration()
    val public = TeleportPaymentConfiguration()
    val permission = TeleportPaymentConfiguration()

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        cooldown = DurationParser.parseDuration(cfg.getStringNotNull("cooldown"), TimeUnit.MILLISECONDS)
        standStillTime = DurationParser.parseDuration(cfg.getStringNotNull("standStillTime"), TimeUnit.MILLISECONDS)

        private.loadFromConfiguration(cfg.getConfigurationSection("private")!!)
        public.loadFromConfiguration(cfg.getConfigurationSection("public")!!)
        permission.loadFromConfiguration(cfg.getConfigurationSection("permission")!!)
    }
}

class TeleportPaymentConfiguration {

    var type: TeleportPaymentType = TeleportPaymentType.XP
        private set

    var maxCost: Long = 1
        private set

    var formula: Expression = MathParser.parse("1")
        private set

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        type = TeleportPaymentType.valueOf(cfg.getStringNotNull("type").uppercase())

        maxCost = cfg.getLong("maxCost")

        formula = MathParser.parse(cfg.getStringNotNull("formula"))
    }
}

enum class TeleportPaymentType {
    DISABLED, FREE, XP, VAULT
}