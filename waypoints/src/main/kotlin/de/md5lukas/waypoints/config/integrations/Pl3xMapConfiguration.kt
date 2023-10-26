package de.md5lukas.waypoints.config.integrations

import de.md5lukas.konfig.Configurable

@Configurable
class Pl3xMapConfiguration {

  var enabled: Boolean = false
    private set

  var icon: String = ""
    private set

  var iconSize: Double = 3.0
    private set
}
