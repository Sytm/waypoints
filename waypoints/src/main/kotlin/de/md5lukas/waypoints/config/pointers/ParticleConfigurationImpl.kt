package de.md5lukas.waypoints.config.pointers

import de.md5lukas.waypoints.pointers.config.ParticleConfiguration
import org.bukkit.Particle
import org.bukkit.configuration.ConfigurationSection

class ParticleConfigurationImpl : RepeatingPointerConfigurationImpl(), ParticleConfiguration {

    override var heightOffset: Double = 0.0
        private set

    override var showVerticalDirection: Boolean = false
        private set

    override var amount: Int = 0
        private set(value) {
            if (value <= 0) {
                throw IllegalArgumentException("The amount must be greater than zero ($value)")
            }
            field = value
        }

    override var length: Double = 0.0
        private set(value) {
            if (value <= 0) {
                throw IllegalArgumentException("The length must be greater than zero ($value)")
            }
            field = value
        }

    override var particle: Particle = Particle.FLAME
        private set

    override fun loadFromConfiguration(cfg: ConfigurationSection) {
        super.loadFromConfiguration(cfg)
        with(cfg) {

            heightOffset = getDouble("heightOffset")

            showVerticalDirection = getBoolean("showVerticalDirection")

            amount = getInt("amount")

            length = getDouble("length")

            particle = getString("particle")!!.let { Particle.valueOf(it.uppercase()) }
        }
    }
}