package de.md5lukas.waypoints.config.general

import de.md5lukas.commons.time.DurationParser
import de.md5lukas.waypoints.util.Expression
import de.md5lukas.waypoints.util.MathParser
import de.md5lukas.waypoints.util.getConfigurationSectionNotNull
import de.md5lukas.waypoints.util.getStringNotNull
import org.bukkit.configuration.ConfigurationSection
import java.time.Duration
import java.util.concurrent.TimeUnit

class TeleportConfiguration {

    var standStillTime: Duration = Duration.ZERO
        private set

    val private = TypedTeleportConfiguration()
    val death = TypedTeleportConfiguration()
    val public = TypedTeleportConfiguration()
    val permission = TypedTeleportConfiguration()

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        standStillTime = Duration.ofMillis(DurationParser.parseDuration(cfg.getStringNotNull("standStillTime"), TimeUnit.MILLISECONDS))

        private.loadFromConfiguration(cfg.getConfigurationSectionNotNull("private"))
        death.loadFromConfiguration(cfg.getConfigurationSectionNotNull("death"))
        public.loadFromConfiguration(cfg.getConfigurationSectionNotNull("public"))
        permission.loadFromConfiguration(cfg.getConfigurationSectionNotNull("permission"))
    }
}

class TypedTeleportConfiguration {

    var cooldown: Duration = Duration.ZERO
        private set

    var paymentType: TeleportPaymentType = TeleportPaymentType.XP
        private set

    var maxCost: Long = 1
        private set

    var formula: Expression = MathParser.parse("1")
        private set

    var onlyLastWaypoint: Boolean = false
        private set

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        cooldown = Duration.ofMillis(DurationParser.parseDuration(cfg.getStringNotNull("cooldown"), TimeUnit.MILLISECONDS))

        paymentType = TeleportPaymentType.valueOf(cfg.getStringNotNull("paymentType").uppercase())

        maxCost = cfg.getLong("maxCost")

        formula = MathParser.parse(cfg.getStringNotNull("formula"), "n")

        // Only used for death waypoints
        onlyLastWaypoint = cfg.getBoolean("onlyLastWaypoint")
    }
}

enum class TeleportPaymentType {
    DISABLED, FREE, XP, VAULT
}