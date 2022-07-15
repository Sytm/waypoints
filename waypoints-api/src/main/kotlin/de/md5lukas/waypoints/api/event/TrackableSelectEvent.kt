package de.md5lukas.waypoints.api.event

import de.md5lukas.waypoints.api.Trackable
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

/**
 * This event is triggered when a trackable has been selected for a player
 */
class TrackableSelectEvent(
    player: Player,
    /**
     * The trackable that has been selected
     */
    val trackable: Trackable,
) : PlayerEvent(player) {

    private companion object {
        @JvmStatic // Automatically creates static getHandlerList()
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList = handlerList
}