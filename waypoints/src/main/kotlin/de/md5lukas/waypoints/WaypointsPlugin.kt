package de.md5lukas.waypoints

import de.md5lukas.commons.time.DurationFormatter
import de.md5lukas.commons.uuid.UUIDCacheSettings
import de.md5lukas.commons.uuid.UUIDUtils
import de.md5lukas.waypoints.api.SQLiteManager
import de.md5lukas.waypoints.api.WaypointsAPI
import de.md5lukas.waypoints.api.base.DatabaseManager
import de.md5lukas.waypoints.command.WaypointsCommand
import de.md5lukas.waypoints.command.WaypointsScriptCommand
import de.md5lukas.waypoints.config.WaypointsConfiguration
import de.md5lukas.waypoints.events.WaypointsListener
import de.md5lukas.waypoints.gui.APIExtensions
import de.md5lukas.waypoints.integrations.DynMapIntegration
import de.md5lukas.waypoints.integrations.VaultIntegration
import de.md5lukas.waypoints.lang.TranslationLoader
import de.md5lukas.waypoints.lang.Translations
import de.md5lukas.waypoints.lang.WorldTranslations
import de.md5lukas.waypoints.pointer.PointerManagerImpl
import de.md5lukas.waypoints.util.TeleportManager
import org.bstats.bukkit.Metrics
import org.bstats.charts.SingleLineChart
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.logging.Level

class WaypointsPlugin : JavaPlugin() {

    @Suppress("PrivatePropertyName")
    private val METRICS_PLUGIN_ID = 6864

    private lateinit var databaseManager: DatabaseManager
    lateinit var waypointsConfig: WaypointsConfiguration
        private set

    lateinit var api: WaypointsAPI
        private set
    val apiExtensions: APIExtensions by lazy {
        APIExtensions(this)
    }

    private lateinit var translationLoader: TranslationLoader
    lateinit var translations: Translations
        private set
    lateinit var worldTranslations: WorldTranslations
        private set

    lateinit var teleportManager: TeleportManager
        private set

    lateinit var uuidUtils: UUIDUtils
        private set
    private var vaultIntegration0: VaultIntegration? = null
    val vaultIntegration: VaultIntegration
        get() = vaultIntegration0 ?: throw IllegalStateException("The vault integration is configured to be used, but no vault compatible plugin is installed")

    override fun onEnable() {
        logger.level = Level.FINE
        loadConfiguration()
        initDatabase()
        initApiServiceProvider()

        initTranslations()
        initTeleportManager()
        initUUIDUtils()
        initDurationFormatter()
        initIntegrations()


        registerCommands()
        registerEvents()

        startMetrics()
    }

    //<editor-fold desc="onEnable Methods">
    private fun loadConfiguration() {
        saveDefaultConfig()
        waypointsConfig = WaypointsConfiguration()
        waypointsConfig.loadFromConfiguration(config)
    }

    private fun initDatabase() {
        databaseManager = SQLiteManager(this, File(dataFolder, "waypoints.db"), PointerManagerImpl(this))

        databaseManager.initDatabase()

        api = databaseManager.api
    }

    private fun initApiServiceProvider() {
        server.servicesManager.register(WaypointsAPI::class.java, api, this, ServicePriority.Normal)
    }

    private fun initTranslations() {
        translationLoader = TranslationLoader(this)

        translationLoader.loadLanguage(waypointsConfig.general.language)

        translations = Translations(translationLoader)

        worldTranslations = WorldTranslations(translationLoader)
    }

    private fun initTeleportManager() {
        teleportManager = TeleportManager(this)
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

    private fun initIntegrations() {
        val integration = VaultIntegration(this)
        if (integration.setupEconomy()) {
            vaultIntegration0 = integration
        }

        if (waypointsConfig.general.features.globalWaypoints) {
            DynMapIntegration(this).setupDynMap()
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

    private fun startMetrics() {
        val metrics = Metrics(this, METRICS_PLUGIN_ID)

        with(api.statistics) {
            metrics.addCustomChart(SingleLineChart("total_waypoints") {
                totalWaypoints
            })
            metrics.addCustomChart(SingleLineChart("total_folders") {
                totalFolders
            })
        }
    }
    //</editor-fold>

    override fun onDisable() {
        if (this::databaseManager.isInitialized) {
            databaseManager.close()
        }
    }
}