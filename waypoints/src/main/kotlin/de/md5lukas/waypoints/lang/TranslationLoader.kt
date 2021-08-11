package de.md5lukas.waypoints.lang

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.util.aotReplace
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.nio.charset.StandardCharsets

class TranslationLoader(
    private val plugin: WaypointsPlugin,
) {

    private lateinit var translations: Map<String, String>

    private val bundledLanguages: Sequence<String>
        get() = plugin.getResource("lang/index")!!.bufferedReader(StandardCharsets.UTF_8).useLines {
            it
        }

    private fun extractLanguages() {
        bundledLanguages.forEach { langKey ->
            plugin.saveResource("lang/$langKey.yml", false)
        }
    }

    private fun processConfiguration(languageConfig: FileConfiguration): Map<String, String> {
        val map = HashMap<String, String>()

        languageConfig.getKeys(true).forEach {
            if (languageConfig.isString(it)) {
                map[it] = languageConfig.getString(it)!!
            }
        }


        map.entries.forEach {
            it.setValue(it.value.aotReplace(map))
        }

        return map
    }

    fun loadLanguage(languageKey: String) {
        extractLanguages()

        val languageFile = File(plugin.dataFolder, "lang/$languageKey.yml")

        if (languageFile.exists()) {
            translations = processConfiguration(YamlConfiguration.loadConfiguration(languageFile))
        } else {
            throw IllegalArgumentException("A language with the key $languageKey (${languageFile.absolutePath}) does not exist")
        }
    }

    operator fun get(key: String): String {
        return translations[key] ?: throw IllegalArgumentException("The key $key is not present in the translation")
    }
}