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

public class DefaultOverviewMenuConfig {

    private Material backgroundItem;
    private Material cycleSortItem;
    private Material deselectItem;
    private Material toggleGlobalsItem;
    private Material setWaypointItem;
    private Material createFolderItem;

    public void load(ConfigurationSection cfg) {
        backgroundItem = Material.matchMaterial(Objects.requireNonNull(cfg.getString("backgroundItem")));
        cycleSortItem = Material.matchMaterial(Objects.requireNonNull(cfg.getString("cycleSortItem")));
        deselectItem = Material.matchMaterial(Objects.requireNonNull(cfg.getString("deselectItem")));
        toggleGlobalsItem = Material.matchMaterial(Objects.requireNonNull(cfg.getString("toggleGlobalsItem")));
        setWaypointItem = Material.matchMaterial(Objects.requireNonNull(cfg.getString("setWaypointItem")));
        createFolderItem = Material.matchMaterial(Objects.requireNonNull(cfg.getString("createFolderItem")));
    }

    public Material getBackgroundItem() {
        return backgroundItem;
    }

    public Material getCycleSortItem() {
        return cycleSortItem;
    }

    public Material getDeselectItem() {
        return deselectItem;
    }

    public Material getToggleGlobalsItem() {
        return toggleGlobalsItem;
    }

    public Material getSetWaypointItem() {
        return setWaypointItem;
    }

    public Material getCreateFolderItem() {
        return createFolderItem;
    }
}
