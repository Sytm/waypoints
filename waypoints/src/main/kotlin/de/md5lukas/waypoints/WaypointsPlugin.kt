package de.md5lukas.waypoints

import de.md5lukas.commons.time.DurationFormatter
import de.md5lukas.commons.uuid.UUIDCacheSettings
import de.md5lukas.commons.uuid.UUIDUtils
import de.md5lukas.konfig.Konfig
import de.md5lukas.schedulers.Schedulers
import de.md5lukas.waypoints.api.SQLiteManager
import de.md5lukas.waypoints.api.WaypointsAPI
import de.md5lukas.waypoints.api.base.DatabaseManager
import de.md5lukas.waypoints.command.WaypointsCommand
import de.md5lukas.waypoints.command.WaypointsScriptCommand
import de.md5lukas.waypoints.config.*
import de.md5lukas.waypoints.events.ConfigReloadEvent
import de.md5lukas.waypoints.events.PointerEvents
import de.md5lukas.waypoints.events.WaypointsListener
import de.md5lukas.waypoints.util.APIExtensions
import de.md5lukas.waypoints.integrations.BlueMapIntegration
import de.md5lukas.waypoints.integrations.DynMapIntegration
import de.md5lukas.waypoints.integrations.SquareMapIntegration
import de.md5lukas.waypoints.integrations.VaultIntegration
import de.md5lukas.waypoints.lang.TranslationLoader
import de.md5lukas.waypoints.lang.Translations
import de.md5lukas.waypoints.lang.WorldTranslations
import de.md5lukas.waypoints.pointers.PointerManager
import de.md5lukas.waypoints.pointers.PointerManagerHooks
import de.md5lukas.waypoints.tasks.CleanDatabaseTask
import de.md5lukas.waypoints.util.TeleportManager
import de.md5lukas.waypoints.util.UpdateChecker
import de.md5lukas.waypoints.util.callEvent
import de.md5lukas.waypoints.util.registerEvents
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import org.bstats.bukkit.Metrics
import org.bstats.charts.SimplePie
import org.bstats.charts.SingleLineChart
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.concurrent.TimeUnit

class WaypointsPlugin : JavaPlugin() {

    @Suppress("PrivatePropertyName")
    private val METRICS_PLUGIN_ID = 6864

    internal lateinit var databaseManager: DatabaseManager
    lateinit var waypointsConfig: WaypointsConfiguration
        private set

    lateinit var api: WaypointsAPI
        private set
    val apiExtensions: APIExtensions by lazy {
        APIExtensions(this)
    }
    lateinit var pointerManager: PointerManager

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
    var dynMapIntegrationAvailable = false
        private set
    var squareMapIntegrationAvailable = false
        private set
    private var blueMapIntegrationAvailable = false

    private lateinit var metrics: Metrics

    override fun onLoad() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).silentLogs(true))
        Konfig.preloadClasses<WaypointsConfiguration>()
    }

    override fun onEnable() {
        CommandAPI.onEnable()

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
        startBackgroundTasks()
    }

    //<editor-fold desc="onEnable Methods">
    private val konfig = Konfig(
        listOf(
            MaterialListAdapter,
            BlockDataAdapter,
            DurationAdapter,
            StyleAdapter,
        )
    )

    private fun loadConfiguration() {
        saveDefaultConfig()
        waypointsConfig = WaypointsConfiguration()
        konfig.deserializeInto(config, waypointsConfig)
    }

    fun reloadConfiguration() {
        saveDefaultConfig()
        reloadConfig()
        konfig.deserializeInto(config, waypointsConfig)

        callEvent(ConfigReloadEvent(waypointsConfig))
    }

    private fun initDatabase() {
        databaseManager = SQLiteManager(this, waypointsConfig.database, File(dataFolder, "waypoints.db"))

        databaseManager.initDatabase()

        api = databaseManager.api

        pointerManager = PointerManager(this, PointerManagerHooks(this), waypointsConfig.pointers)
    }

    private fun initApiServiceProvider() {
        server.servicesManager.register(WaypointsAPI::class.java, api, this, ServicePriority.Normal)
        server.servicesManager.register(PointerManager::class.java, pointerManager, this, ServicePriority.Normal)
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
                }.rawText
            }
        }
    }

    private fun initIntegrations() {
        val integration = VaultIntegration(this)
        if (integration.setupEconomy()) {
            vaultIntegration0 = integration
        }

        if (waypointsConfig.general.features.globalWaypoints) {
            with(waypointsConfig.integrations) {
                if (dynmap.enabled) {
                    dynMapIntegrationAvailable = DynMapIntegration(this@WaypointsPlugin).setupDynMap()
                }
                if (squaremap.enabled) {
                    squareMapIntegrationAvailable = SquareMapIntegration(this@WaypointsPlugin).setupSquareMap()
                }
                if (bluemap.enabled) {
                    blueMapIntegrationAvailable = BlueMapIntegration(this@WaypointsPlugin).setupBlueMap()
                }
            }
        }
    }

    private fun registerCommands() {
        WaypointsCommand(this).register()
        WaypointsScriptCommand(this).register()
    }

    private fun registerEvents() {
        registerEvents(WaypointsListener(this))
        registerEvents(PointerEvents(this))
    }

    private fun startMetrics() {
        metrics = Metrics(this, METRICS_PLUGIN_ID)

        with(api.statistics) {
            metrics.addCustomChart(SingleLineChart("total_waypoints") {
                totalWaypoints
            })
            metrics.addCustomChart(SingleLineChart("total_folders") {
                totalFolders
            })
        }
        metrics.addCustomChart(SimplePie("web_map") {
            when {
                dynMapIntegrationAvailable -> "DynMap"
                squareMapIntegrationAvailable -> "squaremap"
                blueMapIntegrationAvailable -> "BlueMap"
                else -> "none"
            }
        })
        metrics.addCustomChart(SimplePie("uses_vault") {
            (vaultIntegration0 !== null).toString()
        })
        metrics.addCustomChart(SimplePie("global_waypoints_enabled") {
            waypointsConfig.general.features.globalWaypoints.toString()
        })
        metrics.addCustomChart(SimplePie("death_waypoints_enabled") {
            waypointsConfig.general.features.deathWaypoints.toString()
        })
        metrics.addCustomChart(SimplePie("player_tracking_enabled") {
            waypointsConfig.playerTracking.enabled.toString()
        })
        metrics.addCustomChart(SimplePie("protocollib_available") {
            (server.pluginManager.getPlugin("ProtocolLib") !== null).toString()
        })
        metrics.addCustomChart(SimplePie("actionbar_pointer_enabled") {
            waypointsConfig.pointers.actionBar.enabled.toString()
        })
        metrics.addCustomChart(SimplePie("bossbar_pointer_enabled") {
            waypointsConfig.pointers.bossBar.enabled.toString()
        })
        metrics.addCustomChart(SimplePie("beacon_pointer_enabled") {
            waypointsConfig.pointers.beacon.enabled.toString()
        })
        metrics.addCustomChart(SimplePie("blinking_block_pointer_enabled") {
            waypointsConfig.pointers.blinkingBlock.enabled.toString()
        })
        metrics.addCustomChart(SimplePie("compass_pointer_enabled") {
            waypointsConfig.pointers.compass.enabled.toString()
        })
        metrics.addCustomChart(SimplePie("particle_pointer_enabled") {
            waypointsConfig.pointers.particle.enabled.toString()
        })
    }

    private fun startBackgroundTasks() {
        val scheduler = Schedulers.global(this)
        // Run once every day
        scheduler.scheduleAtFixedRateAsync(20 * 60 * 60 * 24, 20 * 60 * 60 * 24, CleanDatabaseTask(this))
        if (waypointsConfig.general.updateChecker) {
            scheduler.scheduleAsync(UpdateChecker(this, "Sytm", "waypoints"))
        }
    }
    //</editor-fold>

    override fun onDisable() {
        CommandAPI.onDisable()
        if (this::databaseManager.isInitialized) {
            databaseManager.close()
        }
        if (this::metrics.isInitialized) {
            metrics.shutdown()
        }
    }
}