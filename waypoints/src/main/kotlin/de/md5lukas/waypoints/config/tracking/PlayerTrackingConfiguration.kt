package de.md5lukas.waypoints.config.tracking

import de.md5lukas.konfig.ConfigPath
import de.md5lukas.konfig.Configurable
import java.time.Duration

@Configurable
class PlayerTrackingConfiguration {

  var enabled = false
    private set

  var toggleable = false
    private set

  var trackingRequiresTrackable = false
    private set

  @ConfigPath("request.enabled")
  var requestEnabled = false
    private set

  @ConfigPath("request.validFor")
  var requestValidFor: Duration = Duration.ZERO
    private set

  var notification = false
    private set
}
