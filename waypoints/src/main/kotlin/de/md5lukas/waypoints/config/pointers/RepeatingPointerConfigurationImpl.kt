package de.md5lukas.waypoints.config.pointers

import de.md5lukas.waypoints.pointers.config.RepeatingPointerConfiguration

abstract class RepeatingPointerConfigurationImpl : RepeatingPointerConfiguration {

    final override var enabled: Boolean = false
        private set

    final override var interval: Int = 0
        private set(value) {
            if (value <= 0) {
                throw IllegalArgumentException("The interval must be greater than zero ($value)")
            }
            field = value
        }
}