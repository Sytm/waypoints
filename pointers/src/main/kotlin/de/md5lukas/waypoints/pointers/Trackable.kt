package de.md5lukas.waypoints.pointers

import org.bukkit.Location
import java.util.*

/**
 * Describes a general trackable location for the pointers to guide towards.
 */
interface Trackable {

    /**
     * The UUID of this instance to uniquely identify this trackable
     */
    val id: UUID

    /**
     * The location the Trackable is located at. This location may change.
     */
    val location: Location

    /**
     * The text the hologram pointer should use. Strings formatted with [org.bukkit.ChatColor.translateAlternateColorCodes] can be used for coloring.
     *
     * If this value is null the hologram pointer will not be available.
     */
    val hologramText: String?
}