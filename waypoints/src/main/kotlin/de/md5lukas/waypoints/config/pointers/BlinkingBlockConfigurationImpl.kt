package de.md5lukas.waypoints.config.pointers

import de.md5lukas.waypoints.pointers.config.BlinkingBlockConfiguration
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.configuration.ConfigurationSection

class BlinkingBlockConfigurationImpl : RepeatingPointerConfigurationImpl(), BlinkingBlockConfiguration {

    override var minDistanceSquared: Long = 0
        set(value) {
            if (value <= 0) {
                throw IllegalArgumentException("The minDistance must be greater than zero ($value)")
            }
            field = value * value
        }

    override var maxDistanceSquared: Long = 0
        set(value) {
            if (value <= 0) {
                throw IllegalArgumentException("The maxDistance must be greater than zero ($value)")
            }
            field = value * value
        }

    override var blockDataSequence: Array<BlockData> = arrayOf(Material.BEACON.createBlockData())

    override fun loadFromConfiguration(cfg: ConfigurationSection) {
        super.loadFromConfiguration(cfg)
        with(cfg) {
            minDistanceSquared = getLong("minDistance")

            maxDistanceSquared = getLong("maxDistance")

            blockDataSequence = getStringList("blockSequence").map {
                val material = Material.matchMaterial(it) ?: throw IllegalArgumentException("The material $it could not be found")
                if (!material.isBlock) {
                    throw IllegalArgumentException("The material $it is not a block")
                }
                material.createBlockData()
            }.toTypedArray()
        }
    }
}