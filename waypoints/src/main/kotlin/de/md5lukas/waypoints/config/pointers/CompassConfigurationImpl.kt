package de.md5lukas.waypoints.config.pointers

import de.md5lukas.konfig.Configurable
import de.md5lukas.waypoints.pointers.config.CompassConfiguration

@Configurable
class CompassConfigurationImpl : RepeatingPointerConfigurationImpl(), CompassConfiguration {
    override var netherSupport: Boolean = false
        private set
}