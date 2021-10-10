package de.md5lukas.waypoints

import de.md5lukas.commons.time.DurationFormatter
import de.md5lukas.commons.uuid.UUIDCacheSettings
import de.md5lukas.commons.uuid.UUIDUtils
import de.md5lukas.waypoints.api.WaypointsAPI
import de.md5lukas.waypoints.command.WaypointsCommand
import de.md5lukas.waypoints.command.WaypointsScriptCommand
import de.md5lukas.waypoints.config.WaypointsConfig
import de.md5lukas.waypoints.db.CompassStorage
import de.md5lukas.waypoints.db.DatabaseManager
import de.md5lukas.waypoints.db.impl.WaypointsAPIImpl
import de.md5lukas.waypoints.events.WaypointsListener
import de.md5lukas.waypoints.lang.TranslationLoader
import de.md5lukas.waypoints.lang.Translations
import de.md5lukas.waypoints.lang.WorldTranslations
import de.md5lukas.waypoints.pointer.PointerManager
import de.md5lukas.waypoints.util.TeleportManager
import de.md5lukas.waypoints.util.VaultHook
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
    lateinit var worldTranslations: WorldTranslations
        private set

    lateinit var pointerManager: PointerManager
        private set
    lateinit var teleportManager: TeleportManager
        private set

    lateinit var uuidUtils: UUIDUtils
        private set
    private var vaultHook0: VaultHook? = null
    val vaultHook: VaultHook
        get() = vaultHook0 ?: throw IllegalStateException("The vault hook is configured to be used, but no vault compatible plugin is installed")

    override fun onEnable() {
        loadConfiguration()
        initDatabase()
        initPointerManager()
        initTranslations()
        initUUIDUtils()
        initDurationFormatter()
        initVaultHook()

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
        WaypointsAPI.INSTANCE = api

        waypointsConfig.pointer.compass.compassStorage = CompassStorage(databaseManager)
    }

    private fun initPointerManager() {
        pointerManager = PointerManager(this)
    }

    private fun initTranslations() {
        translationLoader = TranslationLoader(this)

        translationLoader.loadLanguage(waypointsConfig.general.language)

        translations = Translations(translationLoader)

        worldTranslations = WorldTranslations(translationLoader)
    }

    private fun initUUIDUtils() {
        with(waypointsConfig.general.uuidCache) {
            uuidUtils = UUIDUtils(this@WaypointsPlugin, UUIDCacheSettings().also {
                it.maxSize = this@with.maxSize
                it.expireAfterWrite = this@with.expireAfter
                it.expireAfterWriteTimeUnit = TimeUnit.HOURS
            })
        }
    }

    private fun initDurationFormatter() {
        with(translations) {
            DurationFormatter.setPluralizationHelper { _, timeUnit, isPlural ->
                when (timeUnit) {
                    TimeUnit.SECONDS -> if (isPlural) TEXT_DURATION_SECONDS else TEXT_DURATION_SECOND
                    TimeUnit.MINUTES -> if (isPlural) TEXT_DURATION_MINUTES else TEXT_DURATION_MINUTE
                    TimeUnit.HOURS -> if (isPlural) TEXT_DURATION_HOURS else TEXT_DURATION_HOUR
                    TimeUnit.DAYS -> if (isPlural) TEXT_DURATION_DAYS else TEXT_DURATION_DAY
                    else -> throw UnsupportedOperationException("The TimeUnit $timeUnit is not supported")
                }.text
            }
        }
    }

    private fun initVaultHook() {
        val hook = VaultHook()
        if (hook.setupEconomy()) {
            vaultHook0 = hook
        }
    }

    private fun registerCommands() {
        val waypointsCommand = getCommand("waypoints")!!
        WaypointsCommand(this).let {
            waypointsCommand.setExecutor(it)
            waypointsCommand.tabCompleter = it
        }
        getCommand("waypointsscript")!!.setExecutor(WaypointsScriptCommand(this))
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