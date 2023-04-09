package de.md5lukas.waypoints.pointers

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

/**
 * This event is triggered when the active trackable for a player has been deselected
 */
class TrackableDeselectEvent(
    player: Player,
    /**
     * The trackable that has been deselected
     */
    val trackable: Trackable,
) : PlayerEvent(player) {

    private companion object {
        @JvmStatic // Automatically creates static getHandlerList()
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList = handlerList
}