package de.md5lukas.waypoints.config.pointers

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import de.md5lukas.waypoints.pointers.config.PointerConfiguration
import de.md5lukas.waypoints.util.getConfigurationSectionNotNull
import org.bukkit.configuration.ConfigurationSection

class PointerConfigurationImpl : PointerConfiguration {

    override var disableWhenReachedRadiusSquared = 0
        private set

    override var connectedWorlds: BiMap<String, String> = HashBiMap.create(0)
        private set

    override val actionBar = ActionBarConfigurationImpl()

    override val beacon = BeaconConfigurationImpl()

    override val blinkingBlock = BlinkingBlockConfigurationImpl()

    override val compass = CompassConfigurationImpl()

    override val particle = ParticleConfigurationImpl()

    override val hologram = HologramConfigurationImpl()

    override val bossBar = BossBarConfigurationImpl()

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        disableWhenReachedRadiusSquared = cfg.getInt("disableWhenReachedRadius").let { it * it }

        connectedWorlds = cfg.getConfigurationSectionNotNull("connectedWorlds").let { subSection ->
            val map = HashBiMap.create<String, String>()
            subSection.getKeys(false).forEach {
                map[it] = subSection.getString(it)
            }
            map
        }

        actionBar.loadFromConfiguration(cfg.getConfigurationSectionNotNull("actionBar"))
        beacon.loadFromConfiguration(cfg.getConfigurationSectionNotNull("beacon"))
        blinkingBlock.loadFromConfiguration(cfg.getConfigurationSectionNotNull("blinkingBlock"))
        compass.loadFromConfiguration(cfg.getConfigurationSectionNotNull("compass"))
        particle.loadFromConfiguration(cfg.getConfigurationSectionNotNull("particle"))
        hologram.loadFromConfiguration(cfg.getConfigurationSectionNotNull("hologram"))
        bossBar.loadFromConfiguration(cfg.getConfigurationSectionNotNull("bossBar"))
    }
}