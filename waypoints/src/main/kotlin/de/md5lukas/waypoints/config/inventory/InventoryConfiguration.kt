package de.md5lukas.waypoints.config.inventory

import de.md5lukas.commons.paper.getStringNotNull
import de.md5lukas.konfig.Configurable
import de.md5lukas.konfig.ExportConfigurationSection
import de.md5lukas.konfig.SkipConfig
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

@Configurable
class InventoryConfiguration {

  @SkipConfig private var _rootConfig: ConfigurationSection? = null

  @ExportConfigurationSection(true)
  private var rootConfig: ConfigurationSection
    set(value) {
      _rootConfig = value
      materialCache.clear()
    }
    get() = _rootConfig!!

  private val materialCache = HashMap<String, Material>()

  fun getMaterial(path: String): Material {
    val cached = materialCache[path]
    if (cached != null) {
      return cached
    }

    val materialString = rootConfig.getStringNotNull(path)
    val material =
        Material.matchMaterial(materialString)
            ?: throw IllegalArgumentException("The material $materialString at $path is not valid")

    materialCache[path] = material

    return material
  }
}
