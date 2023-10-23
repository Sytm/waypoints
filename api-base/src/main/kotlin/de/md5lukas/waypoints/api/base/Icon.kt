package de.md5lukas.waypoints.api.base

import de.md5lukas.waypoints.api.Icon
import org.bukkit.Material

private const val SEPARATOR = '|'

fun Icon.asString() =
    if (customModelData !== null) {
      "${material.name}${SEPARATOR}$customModelData"
    } else material.name

fun String.parseIcon(): Icon {
  val index = indexOf(SEPARATOR)

  return if (index >= 0) {
    Icon(Material.valueOf(substring(0, index)), substring(index + 1).toInt())
  } else {
    Icon(Material.valueOf(this), null)
  }
}
