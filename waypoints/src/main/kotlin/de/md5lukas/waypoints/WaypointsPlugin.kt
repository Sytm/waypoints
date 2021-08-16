package de.md5lukas.waypoints

import de.md5lukas.commons.uuid.UUIDCacheSettings
import de.md5lukas.commons.uuid.UUIDUtils
import de.md5lukas.painventories.PainVentoriesAPI
import de.md5lukas.waypoints.api.WaypointsAPI
import de.md5lukas.waypoints.command.WaypointsCommand
import de.md5lukas.waypoints.config.WaypointsConfig
import de.md5lukas.waypoints.db.CompassStorage
import de.md5lukas.waypoints.db.DatabaseManager
import de.md5lukas.waypoints.db.impl.WaypointsAPIImpl
import de.md5lukas.waypoints.events.WaypointsListener
import de.md5lukas.waypoints.gui.WaypointsGUI
import de.md5lukas.waypoints.lang.ItemTranslations
import de.md5lukas.waypoints.lang.TranslationLoader
import de.md5lukas.waypoints.lang.Translations
import de.md5lukas.waypoints.lang.WorldTranslations
import de.md5lukas.waypoints.pointer.PointerManager
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.concurrent.TimeUnit

class WaypointsPlugin : JavaPlugin() {

    private lateinit var databaseManager: DatabaseManager
    lateinit var waypointsConfig: WaypointsConfig
        private set

    lateinit var api: WaypointsAPI
        private set

    private lateinit var translationLoader: TranslationLoader
    lateinit var translations: Translations
        private set
    lateinit var itemTranslations: ItemTranslations
        private set
    lateinit var worldTranslations: WorldTranslations
        private set

    lateinit var waypointsGUI: WaypointsGUI
        private set
    lateinit var pointerManager: PointerManager
        private set
    lateinit var uuidUtils: UUIDUtils
        private set

    override fun onEnable() {
        loadConfiguration()
        initDatabase()
        initPointerManager()
        initTranslations()
        initUUIDUtils()
        initGUI()

        registerCommands()
        registerEvents()
    }

    //<editor-fold desc="onEnable Methods">
    private fun loadConfiguration() {
        saveDefaultConfig()
        waypointsConfig = WaypointsConfig()
        waypointsConfig.loadFromConfiguration(config)
    }

    private fun initDatabase() {
        databaseManager = DatabaseManager(this, File(dataFolder, "waypoints.db"))

        databaseManager.initDatabase()

        api = WaypointsAPIImpl(databaseManager)

        waypointsConfig.pointerConfiguration.compassConfiguration.compassStorage = CompassStorage(databaseManager)
    }

    private fun initPointerManager() {
        pointerManager = PointerManager(this)
    }

    private fun initTranslations() {
        translationLoader = TranslationLoader(this)

        translationLoader.loadLanguage(waypointsConfig.generalConfiguration.language)

        translations = Translations(translationLoader)

        itemTranslations = ItemTranslations(translationLoader)

        worldTranslations = WorldTranslations(translationLoader)
    }

    private fun initUUIDUtils() {
        with(waypointsConfig.generalConfiguration.uuidCacheConfiguration) {
            uuidUtils = UUIDUtils(this@WaypointsPlugin, UUIDCacheSettings().also {
                it.maxSize = this@with.maxSize
                it.expireAfterWrite = this@with.expireAfter
                it.expireAfterWriteTimeUnit = TimeUnit.HOURS
            })
        }
    }

    private fun initGUI() {
        PainVentoriesAPI.plugin = this

        waypointsGUI = WaypointsGUI(this)
    }

    private fun registerCommands() {
        val waypointsCommand = getCommand("waypoints")!!
        WaypointsCommand(this).let {
            waypointsCommand.setExecutor(it)
            waypointsCommand.tabCompleter = it
        }
    }

    private fun registerEvents() {
        val pluginManager = server.pluginManager

        pluginManager.registerEvents(WaypointsListener(this), this)
    }
    //</editor-fold>

    override fun onDisable() {
        if (this::databaseManager.isInitialized) {
            databaseManager.close()
        }
    }
}