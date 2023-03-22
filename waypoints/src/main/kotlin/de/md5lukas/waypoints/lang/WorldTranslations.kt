package de.md5lukas.waypoints.lang

import de.md5lukas.waypoints.events.ConfigReloadEvent
import de.md5lukas.waypoints.util.registerEvents
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.logging.Level

class WorldTranslations(
    private val tl: TranslationLoader
) : Listener {

    init {
        tl.plugin.registerEvents(this)
    }

    private val warned: MutableSet<World> = mutableSetOf()

    fun getWorldName(world: World): String {
        val key = "worlds.${world.name}"
        return if (key in tl) {
            tl[key]
        } else {
            if (warned.add(world)) {
                tl.plugin.logger.log(Level.WARNING, "The world ${world.name} has no translation present. Using actual name as a fallback.")
            }
            world.name
        }
    }

    @Suppress("UNUSED_PARAMETER")
    @EventHandler
    private fun onReload(e: ConfigReloadEvent) {
        warned.clear()
    }
}