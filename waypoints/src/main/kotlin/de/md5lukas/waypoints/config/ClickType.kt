package de.md5lukas.waypoints.config

import org.bukkit.event.block.Action

enum class ClickType(val actions: Array<Action>) {

  LEFT(arrayOf(Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK)),
  RIGHT(arrayOf(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK))
}
