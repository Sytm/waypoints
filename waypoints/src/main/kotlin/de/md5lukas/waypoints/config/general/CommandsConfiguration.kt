package de.md5lukas.waypoints.config.general

import org.bukkit.configuration.ConfigurationSection

class CommandsConfiguration {

    var waypointsAliases = emptyList<String>()
        private set

    var waypointsScriptAliases = emptyList<String>()
        private set

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        waypointsAliases = cfg.getStringList("waypoints.aliases")
        waypointsScriptAliases = cfg.getStringList("waypointsscript.aliases")
    }
}