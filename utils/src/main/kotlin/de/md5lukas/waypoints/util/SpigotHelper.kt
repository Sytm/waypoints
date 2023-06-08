package de.md5lukas.waypoints.util

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun parseLocationString(player: Player, input: String): Location? {
  val parts = input.split(' ')

  if (parts.size != 3) {
    return null
  }

  return try {
    Location(player.world, parts[0].toDouble(), parts[1].toDouble(), parts[2].toDouble())
  } catch (_: NumberFormatException) {
    null
  }
}

var ItemStack.amountClamped: Int
  get() = amount
  set(value) {
    amount = value.coerceIn(1, maxStackSize)
  }
