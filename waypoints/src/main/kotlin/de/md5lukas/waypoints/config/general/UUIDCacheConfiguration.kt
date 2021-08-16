package de.md5lukas.waypoints.config.general

import org.bukkit.configuration.ConfigurationSection

class UUIDCacheConfiguration {

    var maxSize: Long = 0
        private set

    var expireAfter: Long = 0
        private set

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        maxSize = cfg.getLong("maxSize")
        expireAfter = cfg.getLong("expireAfter")
    }
}