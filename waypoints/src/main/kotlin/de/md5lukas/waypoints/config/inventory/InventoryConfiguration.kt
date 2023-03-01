package de.md5lukas.waypoints.config.inventory

import de.md5lukas.waypoints.util.getStringNotNull
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

class InventoryConfiguration {

    private lateinit var rootConfig: ConfigurationSection

    private val materialCache = HashMap<String, Material>()

    var disableFolderSizes: Boolean = false
        private set

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        rootConfig = cfg.root!!
        materialCache.clear()

        disableFolderSizes = cfg.getBoolean("disableFolderSizes")
    }

    fun getMaterial(path: String): Material {
        val cached = materialCache[path]
        if (cached != null) {
            return cached
        }

        val materialString = rootConfig.getStringNotNull(path)
        val material = Material.matchMaterial(materialString) ?: throw IllegalArgumentException("The material $materialString at $path is not valid")

        materialCache[path] = material

        return material
    }
}