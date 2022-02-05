package de.md5lukas.waypoints.config.pointers

import de.md5lukas.waypoints.util.getConfigurationSectionNotNull
import org.bukkit.configuration.ConfigurationSection

class PointerConfiguration() {

    var disableWhenReachedRadius = 0
        private set

    val actionBar = ActionBarConfiguration()

    val beacon = BeaconConfiguration()

    val blinkingBlock = BlinkingBlockConfiguration()

    val compass = CompassConfiguration()

    val particle = ParticleConfiguration()

    val hologram = HologramConfiguration()

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        disableWhenReachedRadius = cfg.getInt("disableWhenReachedRadius").let { it * it }

        actionBar.loadFromConfiguration(cfg.getConfigurationSectionNotNull("actionBar"))
        beacon.loadFromConfiguration(cfg.getConfigurationSectionNotNull("beacon"))
        blinkingBlock.loadFromConfiguration(cfg.getConfigurationSectionNotNull("blinkingBlock"))
        compass.loadFromConfiguration(cfg.getConfigurationSectionNotNull("compass"))
        particle.loadFromConfiguration(cfg.getConfigurationSectionNotNull("particle"))
        hologram.loadFromConfiguration(cfg.getConfigurationSectionNotNull("hologram"))
    }
}