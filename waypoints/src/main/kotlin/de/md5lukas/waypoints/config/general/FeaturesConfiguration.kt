package de.md5lukas.waypoints.config.general

import de.md5lukas.konfig.Configurable

@Configurable
class FeaturesConfiguration {

  var globalWaypoints = true
    private set

  var deathWaypoints = true
    private set
}
