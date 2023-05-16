package de.md5lukas.waypoints.config.pointers

import de.md5lukas.konfig.ConfigPath
import de.md5lukas.konfig.Configurable
import de.md5lukas.konfig.TypeAdapter
import de.md5lukas.konfig.UseAdapter
import de.md5lukas.waypoints.pointers.config.BlinkingBlockConfiguration
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.configuration.ConfigurationSection

@Configurable
class BlinkingBlockConfigurationImpl :
    RepeatingPointerConfigurationImpl(), BlinkingBlockConfiguration {

  @ConfigPath("minDistance")
  override var minDistanceSquared: Long = 0
    set(value) {
      if (value <= 0) {
        throw IllegalArgumentException("The minDistance must be greater than zero ($value)")
      }
      field = value * value
    }

  @ConfigPath("maxDistance")
  override var maxDistanceSquared: Long = 0
    set(value) {
      if (value <= 0) {
        throw IllegalArgumentException("The maxDistance must be greater than zero ($value)")
      }
      field = value * value
    }

  @ConfigPath("blockSequence")
  @UseAdapter(BlockDataArray::class)
  override var blockDataSequence: Array<BlockData> = arrayOf(Material.BEACON.createBlockData())

  private class BlockDataArray : TypeAdapter<Array<BlockData>> {
    override fun get(section: ConfigurationSection, path: String) =
        section
            .getStringList(path)
            .map {
              val material =
                  Material.matchMaterial(it)
                      ?: throw IllegalArgumentException("The material $it could not be found")
              if (!material.isBlock) {
                throw IllegalArgumentException("The material $it is not a block")
              }
              material.createBlockData()
            }
            .toTypedArray()
  }
}
