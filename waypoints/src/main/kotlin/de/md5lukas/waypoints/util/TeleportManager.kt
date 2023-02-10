package de.md5lukas.waypoints.util

import de.md5lukas.commons.time.DurationFormatter
import de.md5lukas.waypoints.WaypointsPermissions
import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.WaypointsPlayer
import de.md5lukas.waypoints.config.general.TeleportPaymentType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitTask
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.util.*
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt

class TeleportManager(private val plugin: WaypointsPlugin) : Listener {

    private val pendingTeleports = HashMap<Player, BukkitTask>()

    init {
        plugin.registerEvents(this)
    }

    fun getTeleportPermission(waypoint: Waypoint) = when (waypoint.type) {
        Type.PRIVATE, Type.DEATH -> WaypointsPermissions.TELEPORT_PRIVATE
        Type.PUBLIC -> WaypointsPermissions.TELEPORT_PUBLIC
        Type.PERMISSION -> WaypointsPermissions.TELEPORT_PERMISSION
    }

    private fun getTeleportConfig(waypoint: Waypoint) = when (waypoint.type) {
        Type.PRIVATE -> plugin.waypointsConfig.general.teleport.private
        Type.DEATH -> plugin.waypointsConfig.general.teleport.death
        Type.PUBLIC -> plugin.waypointsConfig.general.teleport.public
        Type.PERMISSION -> plugin.waypointsConfig.general.teleport.permission
    }

    fun isTeleportEnabled(player: WaypointsPlayer, waypoint: Waypoint): Boolean {
        val config = getTeleportConfig(waypoint)
        return when {
            config.paymentType === TeleportPaymentType.DISABLED -> false
            waypoint.type !== Type.DEATH || !config.onlyLastWaypoint -> true
            else -> {
                player.deathFolder.waypoints.maxByOrNull { it.createdAt } == waypoint
            }
        }
    }

    private fun getTeleportationPrice(player: Player, waypoint: Waypoint): Double {
        val config = getTeleportConfig(waypoint)

        val teleportations = if (config.perCategory) {
            plugin.api.getWaypointPlayer(player.uniqueId).getTeleportations(waypoint.type)
        } else {
            waypoint.getWaypointMeta(player.uniqueId).teleportations
        }

        return min(
            config.maxCost.toDouble(),
            config.formula.eval(Collections.singletonMap("n", teleportations.toDouble()))
        )
    }

    fun getTeleportCostDescription(player: Player, waypoint: Waypoint): String? {
        val config = getTeleportConfig(waypoint)

        if (player.hasPermission(getTeleportPermission(waypoint))) {
            return null
        }

        return when (config.paymentType) {
            TeleportPaymentType.XP -> plugin.translations.WAYPOINT_TELEPORT_XP_LEVEL.withReplacements(
                Collections.singletonMap("levels", getTeleportationPrice(player, waypoint).roundToInt().toString())
            )

            TeleportPaymentType.VAULT -> plugin.translations.WAYPOINT_TELEPORT_BALANCE.withReplacements(
                Collections.singletonMap("balance", getTeleportationPrice(player, waypoint).format())
            )

            else -> null
        }
    }

    fun isAllowedToTeleportToWaypoint(player: Player, waypoint: Waypoint): Boolean {
        if (player.hasPermission(getTeleportPermission(waypoint)))
            return true
        val config = getTeleportConfig(waypoint)
        if (!config.mustVisit)
            return true
        return waypoint.getWaypointMeta(player.uniqueId).visited
    }

    fun teleportPlayerToWaypoint(player: Player, waypoint: Waypoint) {
        cancelRunningTeleport(player)
        if (player.hasPermission(getTeleportPermission(waypoint))) {
            player.teleportKeepOrientation(waypoint.location)
            return
        }

        val playerData = plugin.api.getWaypointPlayer(player.uniqueId)

        val cooldownUntil = playerData.getCooldownUntil(waypoint.type)
        if (cooldownUntil != null) {
            val remainingMillis = Duration.between(Instant.now(), cooldownUntil).toMillis()
            if (remainingMillis > 0) {
                val remainingCooldown = DurationFormatter.formatDuration(player, remainingMillis)

                plugin.translations.MESSAGE_TELEPORT_ON_COOLDOWN.send(player, Collections.singletonMap("remainingCooldown", remainingCooldown))

                return
            }
        }

        val config = getTeleportConfig(waypoint)

        val price = getTeleportationPrice(player, waypoint)

        fun canTeleport() = when (config.paymentType) {
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
                val balance = plugin.vaultIntegration.getBalance(player)
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
                pendingTeleports.remove(player)
                if (canTeleport() &&
                    when (config.paymentType) {
                        TeleportPaymentType.DISABLED -> false
                        TeleportPaymentType.FREE -> true
                        TeleportPaymentType.XP -> {
                            player.giveExpLevels((-price).toInt())
                            incrementTeleportations(player, waypoint)
                            true
                        }
                        TeleportPaymentType.VAULT -> {
                            incrementTeleportations(player, waypoint)
                            plugin.vaultIntegration.withdraw(player, price)
                        }
                    }
                ) {
                    player.teleportKeepOrientation(waypoint.location)

                    val cooldown = config.cooldown

                    if (cooldown.toSeconds() > 0) {
                        playerData.setCooldownUntil(waypoint.type, OffsetDateTime.now().plus(cooldown))
                    }
                }
            }

            val standStillTime = plugin.waypointsConfig.general.teleport.standStillTime

            if (standStillTime.toSeconds() > 0) {
                plugin.translations.MESSAGE_TELEPORT_STAND_STILL_NOTICE.send(
                    player,
                    Collections.singletonMap("duration", DurationFormatter.formatDuration(player, standStillTime.toMillis()))
                )
                pendingTeleports[player] = plugin.server.scheduler.runTaskLater(plugin, action, standStillTime.toSeconds() * 20)
            } else {
                action.run()
            }
        }
    }

    private fun incrementTeleportations(player: Player, waypoint: Waypoint) {
        if (getTeleportConfig(waypoint).perCategory) {
            val playerData = plugin.api.getWaypointPlayer(player.uniqueId)
            playerData.setTeleportations(waypoint.type, playerData.getTeleportations(waypoint.type) + 1)
        } else {
            waypoint.getWaypointMeta(player.uniqueId).teleportations++
        }
    }

    @EventHandler
    private fun onPlayerLeave(e: PlayerQuitEvent) {
        cancelRunningTeleport(e.player)
    }

    @EventHandler
    private fun onPlayerMove(e: PlayerMoveEvent) {
        e.to?.let { to ->
            // Need to perform a fuzzy equals, because after a player stopped moving and first looks around he "snaps" to some double value that's nearby
            if (e.from.fuzzyEquals(to, 0.1)) {
                return
            }
        }

        if (cancelRunningTeleport(e.player)) {
            plugin.translations.MESSAGE_TELEPORT_STAND_STILL_MOVED.send(e.player)
        }
    }

    private fun cancelRunningTeleport(player: Player): Boolean = pendingTeleports.remove(player)?.cancel() != null
}