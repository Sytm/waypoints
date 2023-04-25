package de.md5lukas.waypoints.config.pointers

import de.md5lukas.konfig.ConfigPath
import de.md5lukas.konfig.Configurable
import de.md5lukas.waypoints.pointers.config.ActionBarConfiguration
import net.kyori.adventure.text.format.Style

@Configurable
class ActionBarConfigurationImpl : RepeatingPointerConfigurationImpl(), ActionBarConfiguration {

    override var indicatorColor: Style = Style.empty()
        private set

    override var normalColor: Style = Style.empty()
        private set

    override var section: String = ""
        private set

    @ConfigPath("arrow.left")
    override var leftArrow: String = ""
        private set

    @ConfigPath("arrow.right")
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

    @ConfigPath("showDistance.enabled")
    override var showDistanceEnabled: Boolean = false
        private set
}