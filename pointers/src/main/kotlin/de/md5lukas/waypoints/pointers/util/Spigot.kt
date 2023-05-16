package de.md5lukas.waypoints.pointers.util

import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.util.Vector

internal operator fun Vector.div(d: Int) =
    Vector(
        this.x / d,
        this.y / d,
        this.z / d,
    )

operator fun Vector.div(d: Double) =
    Vector(
        this.x / d,
        this.y / d,
        this.z / d,
    )

internal operator fun Vector.minus(other: Vector) = subtract(other)

internal fun Location.blockEquals(other: Location): Boolean =
    this.world == other.world &&
        this.blockX == other.blockX &&
        this.blockY == other.blockY &&
        this.blockZ == other.blockZ

internal val Location.highestBlock: Block
  get() = world!!.getHighestBlockAt(this)

internal fun Player.sendActualBlock(location: Location) =
    this.sendBlockChange(location, location.block.blockData)
