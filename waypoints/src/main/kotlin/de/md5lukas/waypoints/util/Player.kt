package de.md5lukas.waypoints.util

import org.bukkit.Location
import org.bukkit.entity.Player

fun Player.sendActualBlock(location: Location) = this.sendBlockChange(location, location.block.blockData)