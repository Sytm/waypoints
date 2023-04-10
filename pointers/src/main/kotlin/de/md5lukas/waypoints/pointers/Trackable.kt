package de.md5lukas.waypoints.pointers

import net.kyori.adventure.text.Component
import org.bukkit.Location

/**
 * Describes a general trackable location for the pointers to guide towards.
 */
interface Trackable {

    /**
     * The location the Trackable is located at. This location may change.
     */
    val location: Location

    /**
     * The text the hologram pointer should use.
     *
     * If this value is null the hologram pointer will not be available.
     */
    val hologramText: Component?
        get() = null
}