package de.md5lukas.waypoints.config.pointers

import de.md5lukas.waypoints.pointers.config.ActionBarConfiguration
import de.md5lukas.waypoints.util.getStringNotNull
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.configuration.ConfigurationSection

class ActionBarConfigurationImpl : RepeatingPointerConfigurationImpl(), ActionBarConfiguration {

    override var indicatorColor: Style = Style.empty()
        private set

    override var normalColor: Style = Style.empty()
        private set

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
            indicatorColor = MiniMessage.miniMessage().deserialize(getStringNotNull("indicatorColor")).style()

            normalColor = MiniMessage.miniMessage().deserialize(getStringNotNull("normalColor")).style()

            section = getStringNotNull("section")

            leftArrow = getStringNotNull("arrow.left")

            rightArrow = getStringNotNull("arrow.right")

            amountOfSections = getInt("amountOfSections")

            range = getInt("range")

            showDistanceEnabled = getBoolean("showDistance.enabled")
        }
    }
}