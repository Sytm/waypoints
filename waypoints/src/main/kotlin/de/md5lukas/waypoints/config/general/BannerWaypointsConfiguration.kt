package de.md5lukas.waypoints.config.general

import de.md5lukas.konfig.ConfigPath
import de.md5lukas.konfig.Configurable

@Configurable
class BannerWaypointsConfiguration {

  var enabled = true
    private set

  @ConfigPath("bannerBreaking.removeWaypoint")
  var bannerBreakingRemoveWaypoint = true
    private set

  @ConfigPath("bannerBreaking.triggerOnlyForOwner")
  var bannerBreakingTriggerOnlyForOwner = true
    private set
}
