package de.md5lukas.waypoints.lang

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.events.ConfigReloadEvent
import de.md5lukas.waypoints.util.aotReplace
import de.md5lukas.waypoints.util.registerEvents
import de.md5lukas.waypoints.util.translateColorCodes
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.logging.Level

class TranslationLoader(
    val plugin: WaypointsPlugin,
) : Listener {

    private val defaultLanguage = "en"

    init {
        plugin.registerEvents(this)
    }

    private lateinit var loadedLanguage: String
    private lateinit var translations: Map<String, String>

    private val bundledLanguages: List<String> = plugin.getResource("resourceIndex")!!.bufferedReader(StandardCharsets.UTF_8).useLines { seq ->
        seq.filter {
            it.isNotBlank() && it.startsWith("lang/")
        }.map { it.removeSurrounding("lang/", ".yml") }.toList()
    }

    private fun getLanguageFilePath(languageKey: String) = "lang/$languageKey.yml"

    private fun getLanguageFile(languageKey: String) = File(plugin.dataFolder, getLanguageFilePath(languageKey))

    private fun extractLanguages() {
        bundledLanguages.forEach { languageKey ->
            if (!getLanguageFile(languageKey).exists()) {
                plugin.saveResource(getLanguageFilePath(languageKey), false)
            }
        }
    }

    private fun processConfiguration(languageConfig: FileConfiguration): Map<String, String> {
        val map = HashMap<String, String>()

        languageConfig.getKeys(true).forEach {
            if (languageConfig.isString(it)) {
                map[it] = languageConfig.getString(it)!!.translateColorCodes()
            }
        }


        map.entries.forEach {
            it.setValue(it.value.aotReplace(map))
        }

        return map
    }

    fun loadLanguage(languageKey: String) {
        loadedLanguage = languageKey
        extractLanguages()

        val languageFile = getLanguageFile(languageKey)


        if (languageFile.exists()) {
            val loadedTranslations = YamlConfiguration.loadConfiguration(languageFile)

            // Languages are applied in the following order: File > In Jar > In Jar (English)
            val sameLanguageFallback: FileConfiguration? = if (languageKey in bundledLanguages) {
                plugin.getResource(getLanguageFilePath(languageKey))!!.reader().use {
                    YamlConfiguration.loadConfiguration(it)
                }
            } else null

            val fallback = plugin.getResource(getLanguageFilePath(defaultLanguage))!!.reader().use {
                val fallback = YamlConfiguration.loadConfiguration(it)
                if (sameLanguageFallback == null) {
                    fallback
                } else {
                    sameLanguageFallback.setDefaults(fallback)
                    sameLanguageFallback.options().copyDefaults(true)
                    sameLanguageFallback
                }
            }

            loadedTranslations.setDefaults(fallback)
            loadedTranslations.options().copyDefaults(true)

            translations = processConfiguration(loadedTranslations)
        } else {
            throw IllegalArgumentException("A language with the key $languageKey (${languageFile.absolutePath}) does not exist")
        }
    }

    operator fun get(key: String): String {
        return translations[key] ?: throw IllegalArgumentException("The key $key is not present in the translation file for the language $loadedLanguage")
    }

    operator fun contains(key: String): Boolean {
        val contains = key in translations
        if (!contains) {
            plugin.logger.log(Level.WARNING, "The translation key $key is missing in the translation file for the language $loadedLanguage, but not required")
        }
        return contains
    }

    @EventHandler
    private fun onConfigReload(e: ConfigReloadEvent) {
        loadLanguage(e.config.general.language)
    }
}