package de.md5lukas.waypoints.api

/**
 * A specific trackable whose location doesn't change.
 *
 * For a static trackable additional pointers are available (if enabled):
 * - Beacon pointer
 * - Blinking block pointer
 * - Hologram pointer (if the hologram text is not null)
 */
interface StaticTrackable : Trackable {

    /**
     * The color of the beacon pointer uses. If it is null the beacon beam will be the same as [BeaconColor.CLEAR]
     */
    val beaconColor: BeaconColor?

    /**
     * The text the hologram pointer should use. Strings formatted with [org.bukkit.ChatColor.translateAlternateColorCodes] can be used for coloring.
     *
     * If this value is null the hologram pointer will not be available.
     */
    val hologramText: String?
}