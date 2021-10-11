package de.md5lukas.waypoints.config.pointers

import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.configuration.ConfigurationSection

class BlinkingBlockConfiguration {

    var enabled: Boolean = false

    var interval: Int = 0
        set(value) {
            if (value <= 0) {
                throw IllegalArgumentException("The interval must be greater than zero ($value)")
            }
            field = value
        }

    var minDistance: Long = 0
        set(value) {
            if (value <= 0) {
                throw IllegalArgumentException("The minDistance must be greater than zero ($value)")
            }
            field = value * value
        }

    var maxDistance: Long = 0
        set(value) {
            if (value <= 0) {
                throw IllegalArgumentException("The maxDistance must be greater than zero ($value)")
            }
            field = value * value
        }

    var blockDataSequence: Array<BlockData> = arrayOf(Material.BEACON.createBlockData())

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        with(cfg) {
            enabled = getBoolean("enabled")

            interval = getInt("interval")

            minDistance = getLong("minDistance")

            maxDistance = getLong("maxDistance")

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