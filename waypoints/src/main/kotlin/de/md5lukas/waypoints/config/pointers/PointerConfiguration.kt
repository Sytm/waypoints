package de.md5lukas.waypoints.config.pointers

import org.bukkit.configuration.ConfigurationSection

class PointerConfiguration() {

    val actionBarConfiguration = ActionBarConfiguration()

    val beaconConfiguration = BeaconConfiguration()

    val blinkingBlockConfiguration = BlinkingBlockConfiguration()

    val compassConfiguration = CompassConfiguration()

    val particleConfiguration = ParticleConfiguration()

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        actionBarConfiguration.loadFromConfiguration(cfg.getConfigurationSection("actionBar")!!)
        beaconConfiguration.loadFromConfiguration(cfg.getConfigurationSection("beacon")!!)
        blinkingBlockConfiguration.loadFromConfiguration(cfg.getConfigurationSection("blinkingBlock")!!)
        compassConfiguration.loadFromConfiguration(cfg.getConfigurationSection("compass")!!)
        particleConfiguration.loadFromConfiguration(cfg.getConfigurationSection("particle")!!)
    }
}