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

/**
 * Two instances can be used. One for public and one for permission waypoints
 */
public class GlobalWaypointMenuConfig {

    private Material item;
    private Material backgroundItem;
    private Material selectItem;
    private Material deleteItem;
    private Material renameItem;
    private Material teleportItem;
    private Material selectBeaconColorItem;

    public void load(ConfigurationSection cfg) {
        item = Material.matchMaterial(Objects.requireNonNull(cfg.getString("item")));
        backgroundItem = Material.matchMaterial(Objects.requireNonNull(cfg.getString("backgroundItem")));
        selectItem = Material.matchMaterial(Objects.requireNonNull(cfg.getString("selectItem")));
        deleteItem = Material.matchMaterial(Objects.requireNonNull(cfg.getString("deleteItem")));
        renameItem = Material.matchMaterial(Objects.requireNonNull(cfg.getString("renameItem")));
        teleportItem = Material.matchMaterial(Objects.requireNonNull(cfg.getString("teleportItem")));
        selectBeaconColorItem = Material.matchMaterial(Objects.requireNonNull(cfg.getString("selectBeaconColorItem")));
    }

    public Material getItem() {
        return item;
    }

    public Material getBackgroundItem() {
        return backgroundItem;
    }

    public Material getSelectItem() {
        return selectItem;
    }

    public Material getDeleteItem() {
        return deleteItem;
    }

    public Material getRenameItem() {
        return renameItem;
    }

    public Material getTeleportItem() {
        return teleportItem;
    }

    public Material getSelectBeaconColorItem() {
        return selectBeaconColorItem;
    }
}
