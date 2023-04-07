package de.md5lukas.waypoints.config.general

import de.md5lukas.konfig.Configurable

@Configurable
class PointToDeathWaypointOnDeathConfiguration {

    var enabled = false
        private set

    var overwriteCurrent = false
        private set
}