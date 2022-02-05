package de.md5lukas.waypoints.api

import de.md5lukas.waypoints.api.base.DatabaseConfiguration
import org.bukkit.entity.Player
import java.time.Period
import javax.naming.OperationNotSupportedException

object DummyPointerManager : PointerManager {
    override fun enable(player: Player, waypoint: Waypoint) = throw OperationNotSupportedException()

    override fun disable(player: Player) = throw OperationNotSupportedException()
}

object DummyDatabaseConfiguration : DatabaseConfiguration {
    override val deathWaypointRetentionPeriod: Period = Period.ofDays(1)
}