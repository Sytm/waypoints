package de.md5lukas.waypoints.config.general

import de.md5lukas.waypoints.util.getStringNotNull
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

class CustomIconFilterConfiguration {

    enum class FilterType {
        WHITELIST, BLACKLIST
    }

    var type: FilterType = FilterType.WHITELIST
        private set

    var materials: List<Material> = listOf()
        private set

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        type = if (cfg.getStringNotNull("type").equals("blacklist", true)) FilterType.BLACKLIST else FilterType.WHITELIST

        materials = cfg.getStringList("materials")
            .map { Material.matchMaterial(it) ?: throw IllegalArgumentException("The material $it at ${cfg.currentPath}.materials is not valid") }
    }
}