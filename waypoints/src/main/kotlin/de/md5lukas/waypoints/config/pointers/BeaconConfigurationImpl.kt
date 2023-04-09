package de.md5lukas.waypoints.config.pointers

import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.pointers.BeaconColor
import de.md5lukas.waypoints.pointers.PlayerTrackable
import de.md5lukas.waypoints.pointers.TemporaryWaypointTrackable
import de.md5lukas.waypoints.pointers.Trackable
import de.md5lukas.waypoints.pointers.config.BeaconConfiguration
import de.md5lukas.waypoints.util.getStringNotNull
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.configuration.ConfigurationSection

class BeaconConfigurationImpl : RepeatingPointerConfigurationImpl(), BeaconConfiguration {

    private val viewDistance: Long = (Bukkit.getViewDistance().toLong() * 16).let { it * it }

    override var minDistanceSquared: Long = 0
        private set(value) {
            if (value <= 0) {
                throw IllegalArgumentException("The minDistance must be greater than zero ($value)")
            }
            field = value * value
        }

    override var maxDistanceSquared: Long = 0
        private set(value) {
            if (value <= 0) {
                throw IllegalArgumentException("The maxDistance must be greater than zero ($value)")
            }
            field = value * value
        }

    override var baseBlock: BlockData = Material.IRON_BLOCK.createBlockData()
        private set


    override fun getDefaultColor(trackable: Trackable) = when (trackable) {
        is Waypoint -> defaultColor[trackable.type]
        is PlayerTrackable -> playerTrackableColor
        is TemporaryWaypointTrackable -> temporaryTrackableColor
        else -> null
    }

    private var defaultColor: Map<Type, BeaconColor> = emptyMap()

    private var playerTrackableColor = BeaconColor.CLEAR

    private var temporaryTrackableColor = BeaconColor.CLEAR

    override fun loadFromConfiguration(cfg: ConfigurationSection) {
        super.loadFromConfiguration(cfg)
        with(cfg) {
            minDistanceSquared = getLong("minDistance")

            maxDistanceSquared = if (isLong("maxDistance")) {
                getLong("maxDistance")
            } else {
                viewDistance
            }

            baseBlock = getStringNotNull("baseBlock").let {
                Material.matchMaterial(it)?.createBlockData() ?: throw IllegalArgumentException("The material $it could not be found")
            }

            val mutableDefaultColor: MutableMap<Type, BeaconColor> = HashMap(Type.values().size)
            Type.values().forEach {
                mutableDefaultColor[it] = BeaconColor.valueOf(getStringNotNull("defaultColor.${it.name.lowercase()}"))
            }
            defaultColor = mutableDefaultColor

            playerTrackableColor = BeaconColor.valueOf(getStringNotNull("defaultColor.player"))

            temporaryTrackableColor = BeaconColor.valueOf(getStringNotNull("defaultColor.temporary"))
        }
    }

}