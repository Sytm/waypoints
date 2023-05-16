package de.md5lukas.waypoints.pointers.config

import net.kyori.adventure.text.format.Style

interface ActionBarConfiguration : RepeatingPointerConfiguration {

  val indicatorColor: Style

  val normalColor: Style

  val section: String

  val leftArrow: String

  val rightArrow: String

  val amountOfSections: Int

  val range: Int

  val showDistanceEnabled: Boolean
}
