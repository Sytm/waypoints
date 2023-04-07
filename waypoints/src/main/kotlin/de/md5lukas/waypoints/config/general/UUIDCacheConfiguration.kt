package de.md5lukas.waypoints.config.general

import de.md5lukas.konfig.Configurable

@Configurable
class UUIDCacheConfiguration {

    var maxSize: Long = 0
        private set

    var expireAfter: Long = 0
        private set
}