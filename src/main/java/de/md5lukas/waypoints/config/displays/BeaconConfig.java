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

import de.md5lukas.waypoints.display.BlockColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;

import static de.md5lukas.commons.MathHelper.square;

public class BeaconConfig {

    private boolean enabled;
    private long minDistance;
    private long maxDistance;
    private BlockData baseBlock;
    private int interval;
    private BlockColor defaultColorPrivate;
    private BlockColor defaultColorPublic;
    private BlockColor defaultColorPermission;
    private BlockColor defaultColorDeath;
    private boolean enableSelectColor;

    public void load(ConfigurationSection cfg) {
        enabled = cfg.getBoolean("enabled");
        minDistance = square(cfg.getInt("minDistance"));
        if ("auto".equalsIgnoreCase(cfg.getString("displays.beacon.maxDistance"))) {
            maxDistance = Bukkit.getViewDistance() * 16;
        } else {
            maxDistance = cfg.getInt("displays.beacon.maxDistance");
        }
        maxDistance = square(maxDistance);
        //noinspection ConstantConditions
        baseBlock = Bukkit.createBlockData(Material.matchMaterial(cfg.getString("baseBlock")));
        interval = cfg.getInt("interval");
        defaultColorPrivate = BlockColor.valueOf(cfg.getString("defaultColor.private"));
        defaultColorPublic = BlockColor.valueOf(cfg.getString("defaultColor.public"));
        defaultColorPermission = BlockColor.valueOf(cfg.getString("defaultColor.permission"));
        defaultColorDeath = BlockColor.valueOf(cfg.getString("defaultColor.death"));
        enableSelectColor = cfg.getBoolean("enableSelectColor");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public long getMinDistance() {
        return minDistance;
    }

    public long getMaxDistance() {
        return maxDistance;
    }

    public BlockData getBaseBlock() {
        return baseBlock;
    }

    public int getInterval() {
        return interval;
    }

    public BlockColor getDefaultColorPrivate() {
        return defaultColorPrivate;
    }

    public BlockColor getDefaultColorPublic() {
        return defaultColorPublic;
    }

    public BlockColor getDefaultColorPermission() {
        return defaultColorPermission;
    }

    public BlockColor getDefaultColorDeath() {
        return defaultColorDeath;
    }

    public boolean isEnableSelectColor() {
        return enableSelectColor;
    }
}
