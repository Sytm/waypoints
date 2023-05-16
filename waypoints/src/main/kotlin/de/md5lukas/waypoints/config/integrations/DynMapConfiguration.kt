package de.md5lukas.waypoints.config.integrations

import de.md5lukas.konfig.Configurable

@Configurable
class DynMapConfiguration {

  var enabled: Boolean = false
    private set

  var icon: String = ""
    private set
}
