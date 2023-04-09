package de.md5lukas.waypoints.pointers.config

interface ActionBarConfiguration : RepeatingPointerConfiguration {

    val indicatorColor: String

    val normalColor: String

    val section: String

    val leftArrow: String

    val rightArrow: String

    val amountOfSections: Int

    val range: Int

    val showDistanceEnabled: Boolean
}