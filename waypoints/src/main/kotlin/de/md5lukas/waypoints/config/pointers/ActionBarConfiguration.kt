package de.md5lukas.waypoints.config.pointers

import de.md5lukas.waypoints.util.getStringNotNull
import de.md5lukas.waypoints.util.translateColorCodes
import org.bukkit.configuration.ConfigurationSection

class ActionBarConfiguration {

    var enabled: Boolean = false
        private set

    var interval: Int = 0
        private set(value) {
            if (value <= 0) {
                throw IllegalArgumentException("The interval must be greater than zero ($value)")
            }
            field = value
        }

    var indicatorColor: String = ""
        private set(value) {
            field = value.translateColorCodes()
        }

    var normalColor: String = ""
        private set(value) {
            field = value.translateColorCodes()
        }

    var section: String = ""
        private set

    var leftArrow: String = ""
        private set

    var rightArrow: String = ""
        private set

    var amountOfSections: Int = 0
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

    var range: Int = 0
        private set(value) {
            if (value <= 0) {
                throw IllegalArgumentException("The range must be greater than zero ($value)")
            }
            field = value
        }

    var showDistanceEnabled: Boolean = false
        private set

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        with(cfg) {
            enabled = getBoolean("enabled")

            interval = getInt("interval")

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