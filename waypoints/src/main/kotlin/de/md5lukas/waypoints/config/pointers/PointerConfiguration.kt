package de.md5lukas.waypoints.config.pointers

import org.bukkit.configuration.ConfigurationSection

class PointerConfiguration() {

    var disableWhenReachedRadius = 0
        private set

    val actionBar = ActionBarConfiguration()

    val beacon = BeaconConfiguration()

    val blinkingBlock = BlinkingBlockConfiguration()

    val compass = CompassConfiguration()

    val particle = ParticleConfiguration()

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        disableWhenReachedRadius = cfg.getInt("disableWhenReachedRadius").let { it * it }

        actionBar.loadFromConfiguration(cfg.getConfigurationSection("actionBar")!!)
        beacon.loadFromConfiguration(cfg.getConfigurationSection("beacon")!!)
        blinkingBlock.loadFromConfiguration(cfg.getConfigurationSection("blinkingBlock")!!)
        compass.loadFromConfiguration(cfg.getConfigurationSection("compass")!!)
        particle.loadFromConfiguration(cfg.getConfigurationSection("particle")!!)
    }
}