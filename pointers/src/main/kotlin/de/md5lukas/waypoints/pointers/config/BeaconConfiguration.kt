package de.md5lukas.waypoints.pointers.config

import de.md5lukas.waypoints.pointers.BeaconColor
import de.md5lukas.waypoints.pointers.Trackable
import org.bukkit.block.data.BlockData

interface BeaconConfiguration : RepeatingPointerConfiguration {

    val minDistanceSquared: Long

    val maxDistanceSquared: Long

    val baseBlock: BlockData

    fun getDefaultColor(trackable: Trackable): BeaconColor?
}