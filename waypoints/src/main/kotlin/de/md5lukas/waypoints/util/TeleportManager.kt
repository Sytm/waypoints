package de.md5lukas.waypoints.util

import de.md5lukas.commons.time.DurationFormatter
import de.md5lukas.waypoints.WaypointsPermissions
import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.config.general.TeleportPaymentType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitTask
import java.util.*
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToLong

class TeleportManager(private val plugin: WaypointsPlugin) : Listener, Runnable {

    private val pendingTeleports = HashMap<Player, BukkitTask>()
    private val onCooldown = HashMap<Player, Long>()

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
        plugin.server.scheduler.runTaskTimer(plugin, this, 50, 50)
    }

    fun getTeleportPermission(waypoint: Waypoint) = when (waypoint.type) {
        Type.PRIVATE, Type.DEATH -> WaypointsPermissions.TELEPORT_PRIVATE
        Type.PUBLIC -> WaypointsPermissions.TELEPORT_PUBLIC
        Type.PERMISSION -> WaypointsPermissions.TELEPORT_PERMISSION
    }

    fun isTeleportEnabled(waypoint: Waypoint) = when (waypoint.type) {
        Type.PRIVATE, Type.DEATH -> plugin.waypointsConfig.general.teleport.private
        Type.PUBLIC -> plugin.waypointsConfig.general.teleport.public
        Type.PERMISSION -> plugin.waypointsConfig.general.teleport.permission
    }.type != TeleportPaymentType.DISABLED

    fun teleportPlayerToWaypoint(player: Player, waypoint: Waypoint) {
        cancelRunningTeleport(player)
        if (player.hasPermission(getTeleportPermission(waypoint))) {
            player.teleportKeepOrientation(waypoint.location)
            return
        }

        val cooldownUntil = onCooldown[player]
        if (cooldownUntil != null) {
            val remainingCooldown = DurationFormatter.formatDuration(player, cooldownUntil - System.currentTimeMillis())

            plugin.translations.MESSAGE_TELEPORT_ON_COOLDOWN.send(player, Collections.singletonMap("cooldownUntil", remainingCooldown))

            return
        }

        val config = when (waypoint.type) {
            Type.PRIVATE, Type.DEATH -> plugin.waypointsConfig.general.teleport.private
            Type.PUBLIC -> plugin.waypointsConfig.general.teleport.public
            Type.PERMISSION -> plugin.waypointsConfig.general.teleport.permission
        }

        val meta = waypoint.getWaypointMeta(player.uniqueId)

        val price = min(config.maxCost.toDouble(), config.formula.eval(Collections.singletonMap("n", meta.teleportations.toDouble())))

        fun canTeleport() = when (config.type) {
            TeleportPaymentType.DISABLED -> false
            TeleportPaymentType.FREE -> true
            TeleportPaymentType.XP -> {
                val canTeleport = player.level >= price
                if (!canTeleport) {
                    plugin.translations.MESSAGE_TELEPORT_NOT_ENOUGH_XP.send(
                        player, mapOf(
                            "currentLevel" to player.level.toString(),
                            "requiredLevel" to ceil(price).toInt().toString()
                        )
                    )
                }
                canTeleport
            }
            TeleportPaymentType.VAULT -> {
                val balance = plugin.vaultHook.getBalance(player)
                val canTeleport = balance >= price
                if (!canTeleport) {
                    plugin.translations.MESSAGE_TELEPORT_NOT_ENOUGH_BALANCE.send(
                        player, mapOf(
                            "currentBalance" to balance.format(),
                            "requiredBalance" to price.format()
                        )
                    )
                }
                canTeleport
            }
        }

        if (canTeleport()) {
            val action = Runnable {
                if (canTeleport() &&
                    when (config.type) {
                        TeleportPaymentType.DISABLED -> false
                        TeleportPaymentType.FREE -> true
                        TeleportPaymentType.XP -> {
                            player.giveExpLevels((-price).toInt())
                            true
                        }
                        TeleportPaymentType.VAULT -> plugin.vaultHook.withdraw(player, price)
                    }
                ) {
                    player.teleportKeepOrientation(waypoint.location)

                    val cooldown = plugin.waypointsConfig.general.teleport.cooldown

                    if (cooldown > 0) {
                        onCooldown[player] = System.currentTimeMillis() + cooldown
                    }
                }
            }

            val standStillTime = plugin.waypointsConfig.general.teleport.standStillTime

            if (standStillTime > 0) {
                plugin.translations.MESSAGE_TELEPORT_STAND_STILL_NOTICE.send(
                    player,
                    Collections.singletonMap("duration", DurationFormatter.formatDuration(player, standStillTime))
                )
                // 20 Ticks / Per 1 second (in Milliseconds)
                pendingTeleports[player] = plugin.server.scheduler.runTaskLater(plugin, action, (standStillTime * (20.0 / 1000.0)).roundToLong())
            } else {
                action.run()
            }
        }
    }

    override fun run() {
        onCooldown.values.removeIf {
            it < System.currentTimeMillis()
        }
    }

    @EventHandler
    private fun onPlayerLeave(e: PlayerQuitEvent) {
        cancelRunningTeleport(e.player)
    }

    @EventHandler
    private fun onPlayerMove(e: PlayerMoveEvent) {
        if (cancelRunningTeleport(e.player)) {
            plugin.translations.MESSAGE_TELEPORT_STAND_STILL_MOVED.send(e.player)
        }
    }

    private fun cancelRunningTeleport(player: Player): Boolean = pendingTeleports.remove(player)?.cancel() != null
}