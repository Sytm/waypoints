package de.md5lukas.waypoints.lang

import de.md5lukas.waypoints.WaypointsPlugin
import net.kyori.adventure.text.minimessage.MiniMessage

interface TranslationLoader {

  val plugin: WaypointsPlugin

  val itemMiniMessage: MiniMessage

  operator fun get(key: String): String

  fun registerTranslationWrapper(translation: AbstractTranslation)
}
