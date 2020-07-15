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

package de.md5lukas.waypoints.config.general;

import de.md5lukas.waypoints.data.waypoint.*;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TeleportConfig {

    private boolean countFreeTeleports;
    private long cooldown;
    private long standStillTime;
    private final Map<String, TeleportWaypointConfig> waypointConfigs;

    public TeleportConfig() {
        waypointConfigs = new HashMap<>();
    }

    public void load(ConfigurationSection cfg) {
        countFreeTeleports = cfg.getBoolean("countFreeTeleportations");
        TimeUnit teleportCdTU = TimeUnit.valueOf(cfg.getString("condition.cooldown.timeUnit").toUpperCase());
        cooldown = teleportCdTU.toMillis(cfg.getLong("condition.cooldown.value"));
        standStillTime = cfg.getLong("condition.standStillTime");
        waypointConfigs.clear();
        waypointConfigs.put(DeathWaypoint.class.getSimpleName(), new TeleportWaypointConfig(cfg.getConfigurationSection("waypoint.death")));
        waypointConfigs
                .put(PrivateWaypoint.class.getSimpleName(), new TeleportWaypointConfig(cfg.getConfigurationSection("waypoint.private")));
        waypointConfigs.put(PublicWaypoint.class.getSimpleName(), new TeleportWaypointConfig(cfg.getConfigurationSection("waypoint.public")));
        waypointConfigs
                .put(PermissionWaypoint.class.getSimpleName(), new TeleportWaypointConfig(cfg.getConfigurationSection("waypoint.permission")));
    }

    public boolean isCountFreeTeleports() {
        return countFreeTeleports;
    }

    public long getCooldown() {
        return cooldown;
    }

    public long getStandStillTime() {
        return standStillTime;
    }

    public TeleportWaypointConfig getTeleportSettings(Class<? extends Waypoint> clazz) {
        return waypointConfigs.get(clazz.getSimpleName());
    }

    public boolean isVaultRequired() {
        return waypointConfigs.values().stream().anyMatch(settings -> TeleportWaypointConfig.TeleportPaymentMethod.VAULT.equals(settings.getPaymentMethod()));
    }
}
