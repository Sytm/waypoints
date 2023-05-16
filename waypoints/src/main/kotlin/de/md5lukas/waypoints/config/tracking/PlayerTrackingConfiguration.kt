package de.md5lukas.waypoints.config.tracking

import de.md5lukas.konfig.Configurable

@Configurable
class PlayerTrackingConfiguration {

  var enabled = false
    private set

  var toggleable = false
    private set

  var trackingRequiresTrackable = false
    private set

  var notification = false
    private set
}
