package de.md5lukas.waypoints.config.pointers

import de.md5lukas.waypoints.api.BeaconColor
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.util.getStringNotNull
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.configuration.ConfigurationSection

class BeaconConfiguration {

    private val viewDistance: Long = (Bukkit.getViewDistance().toLong() * 16).let { it * it }

    var enabled: Boolean = false
        private set

    var interval: Int = 0
        private set(value) {
            if (value <= 0) {
                throw IllegalArgumentException("The interval must be greater than zero ($value)")
            }
            field = value
        }
    var minDistance: Long = 0
        private set(value) {
            if (value <= 0) {
                throw IllegalArgumentException("The minDistance must be greater than zero ($value)")
            }
            field = value * value
        }

    var maxDistance: Long = 0
        private set(value) {
            if (value <= 0) {
                throw IllegalArgumentException("The maxDistance must be greater than zero ($value)")
            }
            field = value * value
        }

    var baseBlock: BlockData = Material.IRON_BLOCK.createBlockData()
        private set

    var defaultColor: Map<Type, BeaconColor> = emptyMap()
        private set

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        with (cfg) {
            enabled = getBoolean("enabled")

            interval = getInt("interval")

            minDistance = getLong("minDistance")

            maxDistance = if (isLong("maxDistance")) {
                getLong("maxDistance").let { it * it }
            } else {
                viewDistance
            }

            baseBlock = cfg.getStringNotNull("baseBlock").let {
                Material.matchMaterial(it)?.createBlockData() ?: throw IllegalArgumentException("The material $it could not be found")
            }

            val mutableDefaultColor: MutableMap<Type, BeaconColor> = HashMap(Type.values().size)
            Type.values().forEach {
                mutableDefaultColor[it] = BeaconColor.valueOf(cfg.getStringNotNull("defaultColor.${it.name.lowercase()}"))
            }
            defaultColor = mutableDefaultColor
        }
    }

}