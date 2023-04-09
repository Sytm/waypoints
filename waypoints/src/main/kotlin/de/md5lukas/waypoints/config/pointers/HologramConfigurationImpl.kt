package de.md5lukas.waypoints.config.pointers

import de.md5lukas.waypoints.pointers.config.HologramConfiguration
import org.bukkit.configuration.ConfigurationSection

class HologramConfigurationImpl : RepeatingPointerConfigurationImpl(), HologramConfiguration {

    override var distanceFromPlayer: Int = 0
        private set

    override val distanceFromPlayerSquared: Int
        get() = distanceFromPlayer * distanceFromPlayer

    override var preventOcclusion: Boolean = false
        private set

    override var hologramHeightOffset: Double = 0.0
        private set

    override var iconEnabled: Boolean = false
        private set

    override var iconOffset: Double = 0.0
        private set

    override fun loadFromConfiguration(cfg: ConfigurationSection) {
        super.loadFromConfiguration(cfg)
        with(cfg) {
            distanceFromPlayer = getInt("distanceFromPlayer")

            preventOcclusion = getBoolean("preventOcclusion")

            hologramHeightOffset = getDouble("hologramHeightOffset")

            iconEnabled = getBoolean("icon.enabled")

            iconOffset = getDouble("icon.offset")
        }
    }
}