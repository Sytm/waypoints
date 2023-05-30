package de.md5lukas.waypoints.config.pointers

import de.md5lukas.commons.paper.getStringNotNull
import de.md5lukas.konfig.ConfigPath
import de.md5lukas.konfig.Configurable
import de.md5lukas.konfig.TypeAdapter
import de.md5lukas.konfig.UseAdapter
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.pointers.BeaconColor
import de.md5lukas.waypoints.pointers.PlayerTrackable
import de.md5lukas.waypoints.pointers.TemporaryWaypointTrackable
import de.md5lukas.waypoints.pointers.Trackable
import de.md5lukas.waypoints.pointers.config.BeaconConfiguration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.configuration.ConfigurationSection

@Configurable
class BeaconConfigurationImpl : RepeatingPointerConfigurationImpl(), BeaconConfiguration {

  @ConfigPath("minDistance")
  override var minDistanceSquared: Long = 0
    private set(value) {
      if (value <= 0) {
        throw IllegalArgumentException("The minDistance must be greater than zero ($value)")
      }
      field = value * value
    }

  @ConfigPath("maxDistance")
  @UseAdapter(AutoViewDistance::class)
  override var maxDistanceSquared: Long = 0
    private set(value) {
      if (value <= 0) {
        throw IllegalArgumentException("The maxDistance must be greater than zero ($value)")
      }
      field = value * value
    }

  override var baseBlock: BlockData = Material.IRON_BLOCK.createBlockData()
    private set

  override fun getDefaultColor(trackable: Trackable) =
      when (trackable) {
        is Waypoint -> defaultColor[trackable.type]
        is PlayerTrackable -> playerTrackableColor
        is TemporaryWaypointTrackable -> temporaryTrackableColor
        else -> null
      }

  @UseAdapter(BeaconColorDefaults::class)
  private var defaultColor: Map<Type, BeaconColor> = emptyMap()

  @ConfigPath("defaultColor.player") private var playerTrackableColor = BeaconColor.CLEAR

  @ConfigPath("defaultColor.temporary") private var temporaryTrackableColor = BeaconColor.CLEAR

  private class AutoViewDistance : TypeAdapter<Long> {
    override fun get(section: ConfigurationSection, path: String) =
        if (section.isLong(path)) {
          section.getLong("maxDistance")
        } else {
          Bukkit.getViewDistance().toLong() * 16
        }
  }

  private class BeaconColorDefaults : TypeAdapter<Map<Type, BeaconColor>> {
    override fun get(section: ConfigurationSection, path: String) =
        mutableMapOf<Type, BeaconColor>().also { map ->
          Type.values().forEach {
            map[it] = BeaconColor.valueOf(section.getStringNotNull("$path.${it.name.lowercase()}"))
          }
        }
  }
}
