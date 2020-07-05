/*
 *     Waypoints2, A plugin for spigot to add waypoints functionality
 *     Copyright (C) 2020  Lukas Planz
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

package de.md5lukas.waypoints.display;

import de.md5lukas.commons.MathHelper;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import de.md5lukas.waypoints.util.PlayerItemCheckRunner;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static de.md5lukas.waypoints.config.WPConfig.getDisplayConfig;

public final class BeaconDisplay extends WaypointDisplay {

    private static final BlockData BLOCK_DATA_BEACON = Bukkit.createBlockData(Material.BEACON);

    private Map<UUID, Location> activeBeacons;

    BeaconDisplay(Plugin plugin) {
        super(plugin, getDisplayConfig().getBeaconConfig().getInterval());
        activeBeacons = new HashMap<>();
        PlayerItemCheckRunner.registerUpdateHook((player, canUse) -> {
            if (canUse) {
                show(player, getActiveWaypoint(player));
            } else {
                disable(player, null);
            }
        });
    }

    @Override
    public void show(Player player, Waypoint waypoint) {
        update(player, waypoint);
    }

    @Override
    public void update(Player player, Waypoint waypoint) {
        if (waypoint != null && player.getWorld().equals(waypoint.getLocation().getWorld()) && PlayerItemCheckRunner.canPlayerUseDisplays(player)) {
            UUID uuid = player.getUniqueId();
            double distance = MathHelper.distance2DSquared(player.getLocation(), waypoint.getLocation());
            Location loc = waypoint.getLocation().getWorld().getHighestBlockAt(waypoint.getLocation()).getLocation();
            if (distance < getDisplayConfig().getBeaconConfig().getMinDistance() || distance > getDisplayConfig().getBeaconConfig().getMaxDistance()) {
                if (activeBeacons.containsKey(uuid)) {
                    sendBeacon(player, loc, null, false);
                }
                return;
            }
            if (activeBeacons.containsKey(uuid)) {
                if (!blockEquals(activeBeacons.get(uuid), loc)) {
                    sendBeacon(player, activeBeacons.get(uuid), null, false);
                }
            }
            activeBeacons.put(uuid, loc);
            sendBeacon(player, loc, waypoint.getBeaconColor(), true);
        }
    }

    @Override
    public void disable(Player player, Waypoint waypoint) {
        UUID uuid = player.getUniqueId();
        if (activeBeacons.containsKey(uuid) && Objects.equals(player.getLocation().getWorld(), activeBeacons.get(uuid).getWorld())) {
            sendBeacon(player, activeBeacons.get(uuid), null, false);
        }
        activeBeacons.remove(uuid);
    }

    private static void sendBeacon(Player player, Location location, BlockColor color, boolean create) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                location.add(x, 0, z);
                if (create) {
                    player.sendBlockChange(location, getDisplayConfig().getBeaconConfig().getBaseBlock());
                } else {
                    player.sendBlockChange(location, location.getBlock().getBlockData());
                }
                location.subtract(x, 0, z);
            }
        }
        location.add(0, 1, 0);
        if (create) {
            player.sendBlockChange(location, BLOCK_DATA_BEACON);
            location.add(0, 1, 0);
            if (color == null || color.getMaterial() == null) {
                player.sendBlockChange(location, location.getBlock().getBlockData());
            } else {
                player.sendBlockChange(location, color.getBlockData());
            }
        } else {
            player.sendBlockChange(location, location.getBlock().getBlockData());
            location.add(0, 1, 0);
            player.sendBlockChange(location, location.getBlock().getBlockData());
        }
        location.subtract(0, 2, 0);
    }

    private static boolean blockEquals(Location loc1, Location loc2) {
        return loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ();
    }
}
