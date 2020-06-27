/*
 *     Waypoints2, A plugin for spigot to add waypoints functionality
 *     Copyright (C) 2019-2020 Lukas Planz
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.md5lukas.waypoints.util;

import de.md5lukas.waypoints.Waypoints;
import de.md5lukas.waypoints.data.WPPlayerData;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import de.md5lukas.waypoints.config.WPConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static de.md5lukas.waypoints.Messages.*;

;

public class TeleportManager {

    private static Map<UUID, BukkitTask> waitingForTeleport = new HashMap<>();

    public static void removeWaitingPlayer(Player player) {
        BukkitTask task = waitingForTeleport.remove(player.getUniqueId());
        if (task != null)
            task.cancel();
    }

    public static void playerMoved(Player player) {
        if (WPConfig.getTeleportStandStillTime() > 0 && waitingForTeleport.containsKey(player.getUniqueId())) {
            waitingForTeleport.remove(player.getUniqueId()).cancel();
            CHAT_TELEPORT_CANCELLED_MOVE.send(player);
        }
    }

    public static void teleportPlayerToWaypoint(Player player, WPPlayerData playerData, Waypoint waypoint) {
        if (playerData.canTeleport()) {
            if (!checkPlayerCurrency(player, waypoint))
                return;
            player.closeInventory();
            if (WPConfig.getTeleportStandStillTime() > 0) {
                player.sendMessage(CHAT_TELEPORT_STAND_STILL_NOTICE.getRaw(player)
                        .replace("%timeRequired%", TimeHelper.pluralizeSeconds(player, WPConfig.getTeleportStandStillTime())));
                BukkitTask task = Bukkit.getScheduler().runTaskLater(Waypoints.instance(), () -> {
                    waitingForTeleport.remove(player.getUniqueId());
                    teleportPlayer(player, playerData, waypoint);
                }, WPConfig.getTeleportStandStillTime() * 20L);
                waitingForTeleport.putIfAbsent(player.getUniqueId(), task);
            } else {
                teleportPlayer(player, playerData, waypoint);
            }
        } else {
            player.sendMessage(CHAT_TELEPORT_ON_COOLDOWN.getRaw(player)
                    .replace("%remainingTime%", TimeHelper.formatDuration(player, playerData.remainingTeleportCooldown())));
        }
    }

    private static void sendPlayerNotEnoughCurrency(Player player, Waypoint waypoint) {
        switch (waypoint.getTeleportSettings().getPaymentMethod()) {
            case XP_POINTS:
                CHAT_TELEPORT_NOT_ENOUGH_XP_POINTS.send(player);
                break;
            case XP_LEVELS:
                CHAT_TELEPORT_NOT_ENOUGH_XP_LEVELS.send(player);
                break;
            case VAULT:
                CHAT_TELEPORT_NOT_ENOUGH_BALANCE.send(player);
                break;
            default:
                throw new IllegalArgumentException("The payment method of the waypoint " + waypoint.getName() + " is null");
        }
    }

    private static void teleportPlayer(Player player, WPPlayerData playerData, Waypoint waypoint) {
        waypoint.teleportationUsed(player);
        playerData.playerTeleported();
        teleportKeepOrientation(player, waypoint.getLocation());
    }

    private static boolean checkPlayerCurrency(Player player, Waypoint waypoint) {
        if (WPConfig.TeleportEnabled.PAY.equals(waypoint.getTeleportSettings().getEnabled())) {
            if (!waypoint.getTeleportSettings().hasPlayerEnoughCurrency(player, waypoint.getTeleportations())) {
                sendPlayerNotEnoughCurrency(player, waypoint);
                return false;
            }
        }
        return true;
    }

    public static void teleportKeepOrientation(Player player, Location location) {
        Location target = location.clone();
        target.setPitch(player.getLocation().getPitch());
        target.setYaw(player.getLocation().getYaw());
        player.teleport(target);
    }
}
