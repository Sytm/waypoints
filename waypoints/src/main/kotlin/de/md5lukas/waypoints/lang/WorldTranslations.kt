package de.md5lukas.waypoints.lang

import org.bukkit.World
import java.util.logging.Level

class WorldTranslations(
    private val tl: TranslationLoader
) {
    fun getWorldName(world: World): String {
        val key = "worlds.${world.name}"
        return if (key in tl) {
            tl[key]
        } else {
            tl.plugin.logger.log(Level.WARNING, "The world ${world.name} has no translation present. Using actual name as a fallback.")
            world.name
        }
    }
}