package de.md5lukas.waypoints.util

import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.util.Vector

fun Vector.divide(d: Int): Vector {
    this.x /= d
    this.y /= d
    this.z /= d

    return this
}

fun Location.blockEquals(other: Location): Boolean =
    this.world == other.world && this.blockX == other.blockX && this.blockY == other.blockY && this.blockZ == other.blockZ


fun Location.getHighestBlock(): Block = world!!.getHighestBlockAt(this)