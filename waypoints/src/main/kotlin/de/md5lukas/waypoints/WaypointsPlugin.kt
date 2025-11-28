package de.md5lukas.waypoints

import de.md5lukas.commons.paper.UUIDUtils
import de.md5lukas.commons.paper.registerEvents
import de.md5lukas.commons.time.DurationFormatter
import de.md5lukas.configurate.commonSerializers
import de.md5lukas.schedulers.Schedulers
import de.md5lukas.waypoints.api.WaypointsAPI
import de.md5lukas.waypoints.command.WaypointsCommand
import de.md5lukas.waypoints.command.WaypointsScriptCommand
import de.md5lukas.waypoints.config.InventoryConfiguration
import de.md5lukas.waypoints.config.TeleportPaymentType
import de.md5lukas.waypoints.config.WaypointsConfiguration
import de.md5lukas.waypoints.data.DatabaseManager
import de.md5lukas.waypoints.data.SQLiteManager
import de.md5lukas.waypoints.events.ConfigReloadEvent
import de.md5lukas.waypoints.events.PointerEvents
import de.md5lukas.waypoints.events.WaypointsListener
import de.md5lukas.waypoints.integrations.*
import de.md5lukas.waypoints.lang.Translations
import de.md5lukas.waypoints.lang.WorldTranslations
import de.md5lukas.waypoints.lang.YmlTranslationLoader
import de.md5lukas.waypoints.pointers.PointerManager
import de.md5lukas.waypoints.pointers.PointerManagerHooks
import de.md5lukas.waypoints.tasks.CleanDatabaseTask
import de.md5lukas.waypoints.util.APIExtensions
import de.md5lukas.waypoints.util.TeleportManager
import de.md5lukas.waypoints.util.UpdateChecker
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import org.bstats.bukkit.Metrics
import org.bstats.charts.SimplePie
import org.bstats.charts.SingleLineChart
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.permissions.Permission
import org.bukkit.plugin.java.JavaPlugin
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.kotlin.extensions.set
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader

class WaypointsPlugin : JavaPlugin() {

  internal lateinit var databaseManager: DatabaseManager
  lateinit var waypointsConfig: WaypointsConfiguration
    private set

  lateinit var inventoryConfig: InventoryConfiguration
    private set

  lateinit var api: WaypointsAPI
    private set

  val apiExtensions = APIExtensions(this)
  lateinit var pointerManager: PointerManager

  private lateinit var translationLoader: YmlTranslationLoader
  lateinit var translations: Translations
    private set

  lateinit var worldTranslations: WorldTranslations
    private set

  lateinit var teleportManager: TeleportManager
    private set

  lateinit var uuidUtils: UUIDUtils
    private set

  lateinit var durationFormatter: DurationFormatter
    private set

  private var vaultIntegration0: VaultIntegration? = null
  val vaultIntegration: VaultIntegration
    get() =
        vaultIntegration0
            ?: throw IllegalStateException(
                "The vault integration is configured to be used, but no vault compatible plugin is installed")

  var geyserIntegration: GeyserIntegration? = null
    private set

  var dynMapIntegrationAvailable = false
    private set

  var squareMapIntegrationAvailable = false
    private set

  var pl3xMapIntegrationAvailable = false
    private set

  private var blueMapIntegrationAvailable = false

  private lateinit var metrics: Metrics

  override fun onEnable() {
    try {
      Class.forName("io.papermc.paper.configuration.Configuration")
    } catch (_: ClassNotFoundException) {
      logger.log(Level.SEVERE, "Waypoints requires the Paper server implementation")
      server.pluginManager.disablePlugin(this)
      return
    }
    loadConfiguration()
    initDatabase()

    initTranslations()
    initTeleportManager()
    initCommons()
    initIntegrations()

    registerCommands()
    registerEvents()
    registerCustomizablePermissions(false)

    startMetrics()
    startBackgroundTasks()
  }

  // <editor-fold desc="onEnable Methods">
  private val configLoader =
      YamlConfigurationLoader.builder()
          .path(dataFolder.toPath().resolve("config.yml"))
          .defaultOptions { options ->
            options.serializers {
              it.registerAll(commonSerializers()).registerAll(PointerManager.serializers())
            }
          }
          .commentsEnabled(true)
          .nodeStyle(NodeStyle.BLOCK)
          .indent(2)
          .build()

  private fun loadConfiguration() {
    val node = configLoader.load()

    waypointsConfig =
        node.get<WaypointsConfiguration>()
            ?: throw IllegalStateException("Config could not be loaded")
    node.set(WaypointsConfiguration::class, waypointsConfig)
    configLoader.save(node)

    val inventoryFile = File(dataFolder, "inventory.yml")

    if (!inventoryFile.exists()) {
      saveResource("inventory.yml", false)
    }

    val inventoryYaml = YamlConfiguration.loadConfiguration(inventoryFile)
    inventoryYaml.setDefaults(
        YamlConfiguration.loadConfiguration(getResource("inventory.yml")!!.reader()))

    inventoryConfig = InventoryConfiguration(inventoryYaml)
  }

  fun reloadConfiguration() {
    // TODO does it really work doe?
    loadConfiguration()

    ConfigReloadEvent(waypointsConfig).callEvent()
    registerCustomizablePermissions(true)
  }

  private fun initDatabase() {
    databaseManager =
        SQLiteManager(this, waypointsConfig.database, File(dataFolder, "waypoints.db"))

    databaseManager.initDatabase()

    api = databaseManager.api

    pointerManager = PointerManager(this, PointerManagerHooks(this), waypointsConfig.pointers)
  }

  private fun initTranslations() {
    translationLoader = YmlTranslationLoader(this)

    translationLoader.loadLanguage(waypointsConfig.general.language)

    translations = Translations(translationLoader)

    worldTranslations = WorldTranslations(translationLoader)
  }

  private fun initTeleportManager() {
    teleportManager = TeleportManager(this)
  }

  private fun initCommons() {
    uuidUtils = UUIDUtils(Dispatchers.Default.asExecutor())
    initDurationFormatter()
  }

  fun initDurationFormatter() {
    with(translations) {
      durationFormatter =
          DurationFormatter(
              { timeUnit, isPlural ->
                when (timeUnit) {
                  TimeUnit.SECONDS -> if (isPlural) TEXT_DURATION_SECONDS else TEXT_DURATION_SECOND
                  TimeUnit.MINUTES -> if (isPlural) TEXT_DURATION_MINUTES else TEXT_DURATION_MINUTE
                  TimeUnit.HOURS -> if (isPlural) TEXT_DURATION_HOURS else TEXT_DURATION_HOUR
                  TimeUnit.DAYS -> if (isPlural) TEXT_DURATION_DAYS else TEXT_DURATION_DAY
                  else ->
                      throw UnsupportedOperationException("The TimeUnit $timeUnit is not supported")
                }.rawText
              },
              TEXT_DURATION_ADD_SPACES.value())
    }
  }

  private fun initIntegrations() {
    val vault = VaultIntegration(this)
    if (vault.setupEconomy()) {
      vaultIntegration0 = vault
    }

    if (waypointsConfig.integrations.geyser.enabled) {
      val geyser = GeyserIntegration(this)
      if (geyser.setupGeyser()) {
        geyserIntegration = geyser
      } else {
        slF4JLogger.warn(
            "The geyser integration is enabled in the config but geyser is not installed on this server")
      }
    }

    if (waypointsConfig.features.globalWaypoints) {
      with(waypointsConfig.integrations) {
        if (dynmap.enabled) {
          dynMapIntegrationAvailable = DynMapIntegration(this@WaypointsPlugin).setupDynMap()
        }
        if (squaremap.enabled) {
          squareMapIntegrationAvailable =
              SquareMapIntegration(this@WaypointsPlugin).setupSquareMap()
        }
        if (bluemap.enabled) {
          blueMapIntegrationAvailable = BlueMapIntegration(this@WaypointsPlugin).setupBlueMap()
        }
        if (pl3xmap.enabled) {
          pl3xMapIntegrationAvailable = Pl3xMapIntegration(this@WaypointsPlugin).setupPl3xMap()
        }
      }
    }
  }

  private fun registerCommands() {
    lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) {
      val registrar = it.registrar()
      registrar.register(
          WaypointsCommand(this).buildCommand(), waypointsConfig.commandAliases.waypoints)
      registrar.register(
          WaypointsScriptCommand(this).buildCommand(),
          waypointsConfig.commandAliases.waypointsScript)
    }
  }

  private fun registerEvents() {
    registerEvents(WaypointsListener(this))
    registerEvents(PointerEvents(this))
  }

  private fun registerCustomizablePermissions(clearPrevious: Boolean) {
    val pm = server.pluginManager
    if (clearPrevious) {
      pm.permissions.forEach {
        val name = it.name
        if (name.startsWith(WaypointsPermissions.LIMIT_PREFIX_WAYPOINTS) ||
            name.startsWith(WaypointsPermissions.LIMIT_PREFIX_FOLDERS) ||
            name.startsWith(WaypointsPermissions.LIMIT_PREFIX_PUBLIC_WAYPOINTS) ||
            name.startsWith(WaypointsPermissions.LIMIT_PREFIX_PUBLIC_FOLDERS)) {
          pm.removePermission(name)
        }
      }
    }

    val permissions = mutableListOf<Permission>()
    waypointsConfig.limits.waypoints.permissionLimits.mapTo(permissions) {
      Permission(WaypointsPermissions.LIMIT_PREFIX_WAYPOINTS + it)
    }
    waypointsConfig.limits.folders.permissionLimits.mapTo(permissions) {
      Permission(WaypointsPermissions.LIMIT_PREFIX_FOLDERS + it)
    }
    waypointsConfig.limits.waypoints.public.permissionLimits.mapTo(permissions) {
      Permission(WaypointsPermissions.LIMIT_PREFIX_PUBLIC_WAYPOINTS + it)
    }
    waypointsConfig.limits.folders.public.permissionLimits.mapTo(permissions) {
      Permission(WaypointsPermissions.LIMIT_PREFIX_PUBLIC_FOLDERS + it)
    }

    pm.addPermissions(permissions)
  }

  private fun startMetrics() {
    if (Environment.DEV) return
    metrics = Metrics(this, Environment.METRICS_PLUGIN_ID)

    with(api.statistics) {
      metrics.addCustomChart(SingleLineChart("total_waypoints") { totalWaypoints })
      metrics.addCustomChart(SingleLineChart("total_folders") { totalFolders })
    }
    metrics.addCustomChart(
        SimplePie("web_map") {
          when {
            dynMapIntegrationAvailable -> "DynMap"
            squareMapIntegrationAvailable -> "squaremap"
            blueMapIntegrationAvailable -> "BlueMap"
            pl3xMapIntegrationAvailable -> "Pl3xMap"
            else -> "none"
          }
        })
    metrics.addCustomChart(
        SimplePie("actually_uses_vault") {
          if (vaultIntegration0 !== null) {
                waypointsConfig.teleport
                    .let { arrayOf(it.private, it.death, it.public, it.permission) }
                    .any { it.paymentType === TeleportPaymentType.VAULT }
              } else {
                false
              }
              .toString()
        })
    metrics.addCustomChart(
        SimplePie("global_waypoints_enabled") {
          waypointsConfig.features.globalWaypoints.toString()
        })
    metrics.addCustomChart(
        SimplePie("death_waypoints_enabled") { waypointsConfig.features.deathWaypoints.toString() })
    metrics.addCustomChart(
        SimplePie("player_tracking_enabled") { waypointsConfig.playerTracking.enabled.toString() })
    metrics.addCustomChart(
        SimplePie("protocollib_available") {
          (server.pluginManager.getPlugin("ProtocolLib") !== null).toString()
        })
    metrics.addCustomChart(
        SimplePie("actionbar_pointer_enabled") {
          waypointsConfig.pointers.actionBar.enabled.toString()
        })
    metrics.addCustomChart(
        SimplePie("bossbar_pointer_enabled") {
          waypointsConfig.pointers.bossBar.enabled.toString()
        })
    metrics.addCustomChart(
        SimplePie("beacon_pointer_enabled") { waypointsConfig.pointers.beacon.enabled.toString() })
    metrics.addCustomChart(
        SimplePie("blinking_block_pointer_enabled") {
          waypointsConfig.pointers.blinkingBlock.enabled.toString()
        })
    metrics.addCustomChart(
        SimplePie("compass_pointer_enabled") {
          waypointsConfig.pointers.compass.enabled.toString()
        })
    metrics.addCustomChart(
        SimplePie("particle_pointer_enabled") {
          waypointsConfig.pointers.particle.enabled.toString()
        })
    metrics.addCustomChart(
        SimplePie("trails_pointer_enabled") { waypointsConfig.pointers.trail.enabled.toString() })
  }

  private fun startBackgroundTasks() {
    val scheduler = Schedulers.global(this)
    // Run once every day
    val h24: Long = 20 * 60 * 60 * 24
    scheduler.scheduleAtFixedRateAsync(h24, h24, CleanDatabaseTask(this))
    if (!Environment.DEV && waypointsConfig.general.updateChecker) {
      val checker = UpdateChecker(this)
      checker.setTaskHandle(scheduler.scheduleAtFixedRateAsync(h24, 0, checker))
    }
  }
  // </editor-fold>

  override fun onDisable() {
    if (this::databaseManager.isInitialized) {
      databaseManager.close()
    }
    if (this::metrics.isInitialized) {
      metrics.shutdown()
    }
  }
}
