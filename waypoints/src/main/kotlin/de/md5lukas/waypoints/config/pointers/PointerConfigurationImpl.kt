package de.md5lukas.waypoints.config.pointers

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import de.md5lukas.konfig.ConfigPath
import de.md5lukas.konfig.Configurable
import de.md5lukas.konfig.TypeAdapter
import de.md5lukas.konfig.UseAdapter
import de.md5lukas.waypoints.pointers.config.PointerConfiguration
import org.bukkit.configuration.ConfigurationSection

@Configurable
class PointerConfigurationImpl : PointerConfiguration {

  @ConfigPath("disableWhenReachedRadius")
  override var disableWhenReachedRadiusSquared = 0
    private set(value) {
      field = value * value
    }

  @UseAdapter(BiMapAdapter::class)
  override var connectedWorlds: BiMap<String, String> = HashBiMap.create(0)
    private set

  override val actionBar = ActionBarConfigurationImpl()

  override val beacon = BeaconConfigurationImpl()

  override val blinkingBlock = BlinkingBlockConfigurationImpl()

  override val compass = CompassConfigurationImpl()

  override val particle = ParticleConfigurationImpl()

  override val hologram = HologramConfigurationImpl()

  override val bossBar = BossBarConfigurationImpl()

  override val trail = TrailConfigurationImpl()

  private class BiMapAdapter : TypeAdapter<BiMap<String, String>> {
    override fun get(section: ConfigurationSection, path: String): BiMap<String, String>? {
      val subSection = section.getConfigurationSection(path)

      if (subSection === null) {
        return null
      }

      return subSection.getKeys(false).let {
        val map = HashBiMap.create<String, String>(it.size)
        it.forEach { primary -> map[primary] = subSection.getString(primary) }
        map
      }
    }
  }
}
