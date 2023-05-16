package de.md5lukas.waypoints.config.general

import de.md5lukas.konfig.Configurable

@Configurable
class AvailableWorldsConfiguration {

  var type: FilterType = FilterType.WHITELIST
    private set

  var worlds: List<String> = listOf()
    private set(value) {
      field = value.map { it.lowercase() }
    }
}
