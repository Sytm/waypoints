package de.md5lukas.waypoints.config.general

import de.md5lukas.konfig.Configurable
import org.bukkit.Material

@Configurable
class CustomIconFilterConfiguration {

  var type: FilterType = FilterType.WHITELIST
    private set

  var materials: List<Material> = listOf()
    private set
}
