package de.md5lukas.waypoints.config.general

import de.md5lukas.waypoints.util.getStringNotNull
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.event.block.Action

class OpenWithItemConfig {

    var enabled: Boolean = false
        private set

    var validClicks: List<Action> = emptyList()
        private set

    var mustSneak: Boolean = false
        private set

    var items: List<Material> = emptyList()
        private set

    fun loadFromConfiguration(cfg: ConfigurationSection) {
        enabled = cfg.getBoolean("enabled")

        validClicks = when(val click = cfg.getStringNotNull("click").lowercase()) {
            "right" -> listOf(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK)
            "left" -> listOf(Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK)
            else -> throw IllegalArgumentException("The click type $click is at ${cfg.currentPath}.click invalid")
        }

        mustSneak = cfg.getBoolean("mustSneak")

        items = cfg.getStringList("items")
            .map { Material.matchMaterial(it) ?: throw IllegalArgumentException("The item $it at ${cfg.currentPath}.items is not valid") }
    }
}