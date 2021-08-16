package de.md5lukas.waypoints.lang

import org.bukkit.World

class WorldTranslations(
    private val tl: TranslationLoader
) {
    fun getWorldName(world: World): String {
        val key = "worlds.${world.name}"
        return if (key in tl) {
            tl[key]
        } else {
            world.name
        }
    }
}