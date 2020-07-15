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

package de.md5lukas.waypoints.config.displays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;

public class CompassConfig {

    private boolean enabled;
    private DefaultCompassLocationType defaultLocationType;
    private Location defaultLocation;

    public void load(ConfigurationSection cfg) {
        enabled = cfg.getBoolean("enabled");
        defaultLocationType = DefaultCompassLocationType.getFromConfig(cfg.getString("defaultLocationType"));
        if (defaultLocationType == DefaultCompassLocationType.CONFIG) {
            //noinspection ConstantConditions
            defaultLocation = new Location(Bukkit.getWorld(cfg.getString("displays.compass.defaultLocation.world")),
                    cfg.getDouble("displays.compass.defaultLocation.x"), 0, cfg.getDouble("displays.compass.defaultLocation.y"));
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public DefaultCompassLocationType getDefaultLocationType() {
        return defaultLocationType;
    }

    public Location getDefaultLocation() {
        return defaultLocation;
    }

    public enum DefaultCompassLocationType {
        SPAWN("spawn"), CONFIG("config"), PREVIOUS("previous"), INGAME("ingame"), INGAME_LOCK("ingame-lock");

        private final String inConfig;

        DefaultCompassLocationType(String inConfig) {
            this.inConfig = inConfig;
        }

        public static DefaultCompassLocationType getFromConfig(String inConfig) {
            return Arrays.stream(DefaultCompassLocationType.values()).filter(type -> type.inConfig.equalsIgnoreCase(inConfig)).findFirst()
                    .orElse(SPAWN); // TODO replace with enum matcher when new md5-commons is in place
        }
    }
}
