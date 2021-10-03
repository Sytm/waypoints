package de.md5lukas.waypoints.config.pointers

import org.bukkit.Particle
import org.bukkit.configuration.ConfigurationSection

class ParticleConfiguration {

    var enabled: Boolean = false
        private set

    var interval: Int = 0
        private set(value) {
            if (value <= 0) {
                throw IllegalArgumentException("The interval must be greater than zero ($value)")
            }
            field = value
        }

    var heightOffset: Double = 0.0
        private set

    var showVerticalDirection: Boolean = false
        private set

    var amount: Int = 0
        private set(value) {
            if (value <= 0) {
                throw IllegalArgumentException("The amount must be greater than zero ($value)")
            }
            field = value
        }

    var length: Double = 0.0
        private set(value) {
            if (value <= 0) {
                throw IllegalArgumentException("The length must be greater than zero ($value)")
            }
            field = value
        }

    var particle: Particle = Particle.FLAME
        private set

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        with(cfg) {
            enabled = getBoolean("enabled")

            interval = getInt("interval")

            heightOffset = getDouble("heightOffset")

            showVerticalDirection = getBoolean("showVerticalDirection")

            amount = getInt("amount")

            length = getDouble("length")

            particle = getString("particle")!!.let { Particle.valueOf(it.uppercase()) }
        }
    }
}