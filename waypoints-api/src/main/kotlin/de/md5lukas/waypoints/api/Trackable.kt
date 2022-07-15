package de.md5lukas.waypoints.api

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
}