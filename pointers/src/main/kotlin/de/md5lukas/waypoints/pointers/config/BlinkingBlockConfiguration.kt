package de.md5lukas.waypoints.pointers.config

import org.bukkit.block.data.BlockData

interface BlinkingBlockConfiguration : RepeatingPointerConfiguration {

  val minDistanceSquared: Long

  val maxDistanceSquared: Long

  val blockDataSequence: Array<BlockData>
}
