package de.md5lukas.waypoints.config.database

import de.md5lukas.konfig.Configurable
import de.md5lukas.konfig.TypeAdapter
import de.md5lukas.konfig.UseAdapter
import de.md5lukas.waypoints.api.base.DatabaseConfiguration
import java.time.Period
import org.bukkit.configuration.ConfigurationSection

@Configurable
class DatabaseConfigurationImpl : DatabaseConfiguration {

  @UseAdapter(PeriodAdapter::class)
  override var deathWaypointRetentionPeriod: Period = Period.ZERO
    private set

  private class PeriodAdapter : TypeAdapter<Period> {
    override fun get(section: ConfigurationSection, path: String): Period? {
      return Period.of(
              section.getInt("$path.years"),
              section.getInt("$path.months"),
              section.getInt("$path.days"))
          .also {
            if (it.isNegative) {
              throw IllegalArgumentException(
                  "The value for the death waypoint retention period must not be negative")
            }
          }
    }
  }
}
