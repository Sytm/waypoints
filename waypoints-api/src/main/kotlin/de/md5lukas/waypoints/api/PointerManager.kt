package de.md5lukas.waypoints.api

import org.bukkit.entity.Player
import java.util.*

interface PointerManager {

    fun trackableOf(player: Player): PlayerTrackable

    fun enable(player: Player, trackable: Trackable)

    fun disable(player: Player)

    fun disableAll(id: UUID)

    fun getCurrentTarget(player: Player): Trackable?
}