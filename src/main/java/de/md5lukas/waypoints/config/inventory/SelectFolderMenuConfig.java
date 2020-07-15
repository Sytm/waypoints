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

package de.md5lukas.waypoints.config.inventory;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;

public class SelectFolderMenuConfig {

    private Material backgroundItem;
    private Material noFolderItem;

    public void load(ConfigurationSection cfg) {
        backgroundItem = Material.matchMaterial(Objects.requireNonNull(cfg.getString("backgroundItem")));
        noFolderItem = Material.matchMaterial(Objects.requireNonNull(cfg.getString("noFolderItem")));
    }

    public Material getBackgroundItem() {
        return backgroundItem;
    }

    public Material getNoFolderItem() {
        return noFolderItem;
    }
}
