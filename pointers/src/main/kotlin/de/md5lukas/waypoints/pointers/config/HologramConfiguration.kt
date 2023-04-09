package de.md5lukas.waypoints.pointers.config

interface HologramConfiguration : RepeatingPointerConfiguration {

    val distanceFromPlayer: Int

    val distanceFromPlayerSquared: Int

    val preventOcclusion: Boolean

    val hologramHeightOffset: Double

    val iconEnabled: Boolean

    val iconOffset: Double
}