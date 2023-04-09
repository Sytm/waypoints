package de.md5lukas.waypoints.config.pointers

import de.md5lukas.waypoints.pointers.config.CompassConfiguration
import org.bukkit.configuration.ConfigurationSection

class CompassConfigurationImpl : RepeatingPointerConfigurationImpl(), CompassConfiguration {
    override var netherSupport: Boolean = false
        private set

    override fun loadFromConfiguration(cfg: ConfigurationSection) {
        super.loadFromConfiguration(cfg)

        netherSupport = cfg.getBoolean("netherSupport")
    }
}