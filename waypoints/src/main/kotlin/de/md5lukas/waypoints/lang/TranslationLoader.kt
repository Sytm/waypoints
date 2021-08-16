package de.md5lukas.waypoints.lang

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.util.aotReplace
import de.md5lukas.waypoints.util.translateColorCodes
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.logging.Level

class TranslationLoader(
    private val plugin: WaypointsPlugin,
) {

    private lateinit var loadedLanguage: String
    private lateinit var translations: Map<String, String>

    private val bundledLanguages: List<String> = plugin.getResource("lang/index.txt")!!.bufferedReader(StandardCharsets.UTF_8).useLines { seq ->
        seq.filter {
            it.isNotBlank()
        }.toList()
    }

    private fun getLanguageFilePath(languageKey: String) = "lang/$languageKey.yml"

    private fun extractLanguages() {
        bundledLanguages.forEach { languageKey ->
            plugin.saveResource(getLanguageFilePath(languageKey), false)
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

        val languageFile = File(plugin.dataFolder, getLanguageFilePath(languageKey))


        if (languageFile.exists()) {
            val loadedTranslations = YamlConfiguration.loadConfiguration(languageFile)

            val defaultTranslations = YamlConfiguration.loadConfiguration(plugin.getResource(getLanguageFilePath(bundledLanguages[0]))!!.reader())

            loadedTranslations.setDefaults(defaultTranslations)

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
}