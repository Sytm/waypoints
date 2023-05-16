package de.md5lukas.waypoints.config.general

import de.md5lukas.konfig.ConfigPath
import de.md5lukas.konfig.Configurable

@Configurable
class CommandsConfiguration {

  @ConfigPath("waypoints.aliases")
  var waypointsAliases = emptyList<String>()
    private set

  @ConfigPath("waypointsscript.aliases")
  var waypointsScriptAliases = emptyList<String>()
    private set
}
