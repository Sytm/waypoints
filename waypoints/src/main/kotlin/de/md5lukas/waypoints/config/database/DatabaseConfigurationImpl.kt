package de.md5lukas.waypoints.config.database

import de.md5lukas.waypoints.api.base.DatabaseConfiguration
import org.bukkit.configuration.ConfigurationSection
import java.time.Period

class DatabaseConfigurationImpl : DatabaseConfiguration {

    override var deathWaypointRetentionPeriod: Period = Period.ZERO
        private set

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        deathWaypointRetentionPeriod = Period.of(
            cfg.getInt("deathWaypointRetentionPeriod.years"),
            cfg.getInt("deathWaypointRetentionPeriod.months"),
            cfg.getInt("deathWaypointRetentionPeriod.days")
        ).also {
            if (it.isNegative) {
                throw IllegalArgumentException("The value for the death waypoint retention period must not be negative")
            }
        }
    }
}