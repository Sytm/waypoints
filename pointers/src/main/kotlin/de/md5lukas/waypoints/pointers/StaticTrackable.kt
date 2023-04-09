package de.md5lukas.waypoints.pointers

/**
 * A specific trackable whose location doesn't change.
 *
 * For a static trackable additional pointers are available (if enabled):
 * - Beacon pointer
 * - Blinking block pointer
 */
interface StaticTrackable : Trackable {

    /**
     * The color of the beacon pointer uses. If it is null the beacon beam will be the same as [BeaconColor.CLEAR]
     */
    val beaconColor: BeaconColor?
}