package de.md5lukas.waypoints.config.general

import de.md5lukas.konfig.ConfigPath
import de.md5lukas.konfig.Configurable

@Configurable
class LimitConfiguration {

    var limit: Int = 0
        private set

    @ConfigPath("allowDuplicateNames.private")
    var allowDuplicateNamesPrivate = false
        private set

    @ConfigPath("allowDuplicateNames.public")
    var allowDuplicateNamesPublic = false
        private set

    @ConfigPath("allowDuplicateNames.permission")
    var allowDuplicateNamesPermission = false
        private set
}