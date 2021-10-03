package de.md5lukas.waypoints.config.pointers

import org.bukkit.configuration.ConfigurationSection

class PointerConfiguration() {

    val actionBar = ActionBarConfiguration()

    val beacon = BeaconConfiguration()

    val blinkingBlock = BlinkingBlockConfiguration()

    val compass = CompassConfiguration()

    val particle = ParticleConfiguration()

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        actionBar.loadFromConfiguration(cfg.getConfigurationSection("actionBar")!!)
        beacon.loadFromConfiguration(cfg.getConfigurationSection("beacon")!!)
        blinkingBlock.loadFromConfiguration(cfg.getConfigurationSection("blinkingBlock")!!)
        compass.loadFromConfiguration(cfg.getConfigurationSection("compass")!!)
        particle.loadFromConfiguration(cfg.getConfigurationSection("particle")!!)
    }
}