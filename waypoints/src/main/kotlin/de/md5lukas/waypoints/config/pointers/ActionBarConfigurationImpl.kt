package de.md5lukas.waypoints.config.pointers

import de.md5lukas.waypoints.pointers.config.ActionBarConfiguration
import de.md5lukas.waypoints.util.getStringNotNull
import org.bukkit.configuration.ConfigurationSection

class ActionBarConfigurationImpl : RepeatingPointerConfigurationImpl(), ActionBarConfiguration {

    override var indicatorColor: String = ""

    override var normalColor: String = ""

    override var section: String = ""
        private set

    override var leftArrow: String = ""
        private set

    override var rightArrow: String = ""
        private set

    override var amountOfSections: Int = 0
        private set(value) {
            if (value <= 0) {
                throw IllegalArgumentException("The amountOfSections must be greater than zero ($value)")
            }
            field = if (value % 2 == 0) {
                value + 1
            } else {
                value
            }
        }

    override var range: Int = 0
        private set(value) {
            if (value <= 0) {
                throw IllegalArgumentException("The range must be greater than zero ($value)")
            }
            field = value
        }

    override var showDistanceEnabled: Boolean = false
        private set

    override fun loadFromConfiguration(cfg: ConfigurationSection) {
        super.loadFromConfiguration(cfg)
        with(cfg) {

            indicatorColor = getStringNotNull("indicatorColor")

            normalColor = getStringNotNull("normalColor")

            section = getStringNotNull("section")

            leftArrow = getStringNotNull("arrow.left")

            rightArrow = getStringNotNull("arrow.right")

            amountOfSections = getInt("amountOfSections")

            range = getInt("range")

            showDistanceEnabled = getBoolean("showDistance.enabled")
        }
    }
}