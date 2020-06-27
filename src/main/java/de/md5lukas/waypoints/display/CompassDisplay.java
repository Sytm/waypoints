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

import de.md5lukas.commons.tags.LocationTag;
import de.md5lukas.nbt.tags.CompoundTag;
import de.md5lukas.waypoints.Waypoints;
import de.md5lukas.waypoints.config.displays.CompassConfig;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import de.md5lukas.waypoints.util.PlayerItemCheckRunner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static de.md5lukas.waypoints.config.WPConfig.getDisplayConfigs;

public final class CompassDisplay extends WaypointDisplay {

    protected CompassDisplay(Plugin plugin) {
        super(plugin, 0);
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
        if (waypoint != null) {
            if (getDisplayConfigs().getCompassConfig().getDefaultLocationType() == CompassConfig.DefaultCompassLocationType.PREVIOUS) {
                CompoundTag store = getStore(player);
                store.put("location", new LocationTag(null, player.getCompassTarget()));
            }
            player.setCompassTarget(waypoint.getLocation());
        }
    }

    @Override
    public void update(Player player, Waypoint waypoint) {
    }

    @Override
    public void disable(Player player, Waypoint waypoint) {
        switch (getDisplayConfigs().getCompassConfig().getDefaultLocationType()) {
            case SPAWN:
                player.setCompassTarget(Bukkit.getWorlds().get(0).getSpawnLocation());
                break;
            case CONFIG:
                player.setCompassTarget(getDisplayConfigs().getCompassConfig().getDefaultLocation());
                break;
            case PREVIOUS:
                CompoundTag store = getStore(player);
                if (store.contains("location")) {
                    player.setCompassTarget(((LocationTag) store.get("location")).value());
                    store.put("location", null);
                }
                break;
            case INGAME:
            case INGAME_LOCK:
                player.setCompassTarget(Waypoints.getGlobalStore().getCompassTarget());
                break;
        }
    }
}
