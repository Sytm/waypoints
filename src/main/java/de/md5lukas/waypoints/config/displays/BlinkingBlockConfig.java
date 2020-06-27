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
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static de.md5lukas.commons.MathHelper.square;

public class BlinkingBlockConfig {

    private boolean enabled;
    private long minDistance;
    private long maxDistance;
    private List<BlockData> blocks;
    private int interval;

    public void load(ConfigurationSection cfg) {
        enabled = cfg.getBoolean("enabled");
        minDistance = square(cfg.getInt("minDistance"));
        maxDistance = square(cfg.getInt("maxDistance"));
        blocks = cfg.getStringList("blocks").stream()
                .map(Material::matchMaterial).filter(Objects::nonNull).map(Bukkit::createBlockData).collect(Collectors.toList());
        interval = cfg.getInt("interval");
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

    public List<BlockData> getBlocks() {
        return blocks;
    }

    public int getInterval() {
        return interval;
    }
}
