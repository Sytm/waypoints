package de.md5lukas.waypoints.api

import de.md5lukas.waypoints.api.base.DatabaseConfiguration
import org.bukkit.Location
import org.bukkit.entity.Player
import java.time.Period
import java.util.*
import javax.naming.OperationNotSupportedException

object DummyPointerManager : PointerManager {
    override fun trackableOf(player: Player): PlayerTrackable = throw OperationNotSupportedException()
    override fun trackableOf(location: Location): StaticTrackable = throw OperationNotSupportedException()

    override fun enable(player: Player, trackable: Trackable) = throw OperationNotSupportedException()

    override fun disable(player: Player) = throw OperationNotSupportedException()
    override fun disableAll(id: UUID) = throw OperationNotSupportedException()

    override fun getCurrentTarget(player: Player): Trackable = throw OperationNotSupportedException()
}

object DummyDatabaseConfiguration : DatabaseConfiguration {
    override val deathWaypointRetentionPeriod: Period = Period.ofDays(1)
}