package de.md5lukas.waypoints.config.general

import org.bukkit.configuration.ConfigurationSection

class LimitConfiguration {

    var limit: Int = 0
        private set

    var allowDuplicateNamesPrivate = false
        private set
    var allowDuplicateNamesPublic = false
        private set
    var allowDuplicateNamesPermission = false
        private set

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        limit = cfg.getInt("limit")

        allowDuplicateNamesPrivate = cfg.getBoolean("allowDuplicateNames.private")

        allowDuplicateNamesPublic = cfg.getBoolean("allowDuplicateNames.public")

        allowDuplicateNamesPermission = cfg.getBoolean("allowDuplicateNames.permission")
    }
}