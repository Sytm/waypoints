package de.md5lukas.waypoints.config.integrations

import de.md5lukas.konfig.Configurable

@Configurable
class SquareMapConfiguration {

  var enabled: Boolean = false
    private set

  var icon: String = ""
    private set

  var iconSize: Int = 3
    private set
}
