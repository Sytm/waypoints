package de.md5lukas.waypoints.api

import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

interface PointerManager {

    fun trackableOf(player: Player): PlayerTrackable

    fun trackableOf(location: Location): StaticTrackable

    fun enable(player: Player, trackable: Trackable)

    fun disable(player: Player)

    fun disableAll(id: UUID)

    fun getCurrentTarget(player: Player): Trackable?
}