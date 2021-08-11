package de.md5lukas.waypoints

import de.md5lukas.painventories.PainVentoriesAPI
import de.md5lukas.waypoints.api.WaypointsAPI
import de.md5lukas.waypoints.config.WaypointsConfig
import de.md5lukas.waypoints.db.DatabaseManager
import de.md5lukas.waypoints.db.impl.WaypointsAPIImpl
import de.md5lukas.waypoints.lang.ItemTranslations
import de.md5lukas.waypoints.lang.TranslationLoader
import de.md5lukas.waypoints.lang.Translations
import de.md5lukas.waypoints.pointer.PointerManager
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class WaypointsPlugin : JavaPlugin() {

    private lateinit var databaseManager: DatabaseManager
    lateinit var waypointsConfig: WaypointsConfig
        private set
    lateinit var pointerManager: PointerManager
        private set
    private lateinit var translationLoader: TranslationLoader
    lateinit var translations: Translations
        private set
    lateinit var itemTranslations: ItemTranslations
        private set

    override fun onEnable() {
        PainVentoriesAPI.plugin = this
        loadConfiguration()
        initDatabase()
        initPointerManager()
        initTranslations()
    }

    override fun onDisable() {
        databaseManager.close()
    }

    private fun loadConfiguration() {
        TODO("Load pointer config and set compass storage")
    }

    private fun initDatabase() {
        databaseManager = DatabaseManager(this, File(dataFolder, "waypoints.db"))

        databaseManager.initDatabase()

        WaypointsAPI.setInstance(WaypointsAPIImpl(databaseManager))
    }

    private fun initPointerManager() {
        pointerManager = PointerManager(this)
    }

    private fun initTranslations() {
        translationLoader = TranslationLoader(this)

        translationLoader.loadLanguage(waypointsConfig.generalConfiguration.language)

        translations = Translations(translationLoader)

        itemTranslations = ItemTranslations(translationLoader)
    }
}