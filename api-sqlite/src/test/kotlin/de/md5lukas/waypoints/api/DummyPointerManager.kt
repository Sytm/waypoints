package de.md5lukas.waypoints.api

import org.bukkit.entity.Player
import javax.naming.OperationNotSupportedException

class DummyPointerManager : PointerManager {
    override fun enable(player: Player, waypoint: Waypoint) = throw OperationNotSupportedException()

    override fun disable(player: Player) = throw OperationNotSupportedException()
}