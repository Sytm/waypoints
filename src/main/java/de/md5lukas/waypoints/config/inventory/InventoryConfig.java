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

public class InventoryConfig {

    private Material previousItem;
    private Material nextItem;
    private Material backItem;

    private final ConfirmMenuConfig confirmMenuConfig;
    private final SelectBeaconColorMenuConfig selectBeaconColorMenuConfig;
    private final SelectFolderMenuConfig selectFolderMenuConfig;
    private final SelectWaypointTypeMenuConfig selectWaypointTypeMenuConfig;

    private final DefaultOverviewMenuConfig defaultOverviewMenuConfig;
    private final GlobalOverviewMenuConfig publicOverviewMenuConfig;
    private final GlobalOverviewMenuConfig permissionOverviewMenuConfig;

    private final DeathWaypointMenuConfig deathWaypointMenuConfig;
    private final GlobalWaypointMenuConfig publicWaypointMenuConfig;
    private final GlobalWaypointMenuConfig permissionWaypointMenuConfig;
    private final PrivateWaypointMenuConfig privateWaypointMenuConfig;

    private final PrivateFolderMenuConfig privateFolderMenuConfig;

    public InventoryConfig(ConfigurationSection cfg) {
        confirmMenuConfig = new ConfirmMenuConfig();
        selectBeaconColorMenuConfig = new SelectBeaconColorMenuConfig();
        selectFolderMenuConfig = new SelectFolderMenuConfig();
        selectWaypointTypeMenuConfig = new SelectWaypointTypeMenuConfig();

        defaultOverviewMenuConfig = new DefaultOverviewMenuConfig();
        publicOverviewMenuConfig = new GlobalOverviewMenuConfig();
        permissionOverviewMenuConfig = new GlobalOverviewMenuConfig();

        deathWaypointMenuConfig = new DeathWaypointMenuConfig();
        publicWaypointMenuConfig = new GlobalWaypointMenuConfig();
        permissionWaypointMenuConfig = new GlobalWaypointMenuConfig();
        privateWaypointMenuConfig = new PrivateWaypointMenuConfig();

        privateFolderMenuConfig = new PrivateFolderMenuConfig();
        load(cfg);
    }

    public void load(ConfigurationSection cfg) {
        previousItem = Material.matchMaterial(Objects.requireNonNull(cfg.getString("general.previousItem")));
        nextItem = Material.matchMaterial(Objects.requireNonNull(cfg.getString("general.nextItem")));
        backItem = Material.matchMaterial(Objects.requireNonNull(cfg.getString("general.backItem")));

        confirmMenuConfig.load(Objects.requireNonNull(cfg.getConfigurationSection("confirmMenu")));
        selectBeaconColorMenuConfig.load(Objects.requireNonNull(cfg.getConfigurationSection("selectBeaconColor")));
        selectFolderMenuConfig.load(Objects.requireNonNull(cfg.getConfigurationSection("selectFolder")));
        selectWaypointTypeMenuConfig.load(Objects.requireNonNull(cfg.getConfigurationSection("selectWaypointType")));

        defaultOverviewMenuConfig.load(Objects.requireNonNull(cfg.getConfigurationSection("overviews.default")));
        publicOverviewMenuConfig.load(Objects.requireNonNull(cfg.getConfigurationSection("overviews.public")));
        permissionOverviewMenuConfig.load(Objects.requireNonNull(cfg.getConfigurationSection("overviews.permission")));

        deathWaypointMenuConfig.load(Objects.requireNonNull(cfg.getConfigurationSection("waypoints.death")));
        privateWaypointMenuConfig.load(Objects.requireNonNull(cfg.getConfigurationSection("waypoints.private")));
        publicWaypointMenuConfig.load(Objects.requireNonNull(cfg.getConfigurationSection("waypoints.public")));
        permissionWaypointMenuConfig.load(Objects.requireNonNull(cfg.getConfigurationSection("waypoints.permission")));

        privateFolderMenuConfig.load(Objects.requireNonNull(cfg.getConfigurationSection("folders.private")));
    }

    public Material getPreviousItem() {
        return previousItem;
    }

    public Material getNextItem() {
        return nextItem;
    }

    public Material getBackItem() {
        return backItem;
    }

    public ConfirmMenuConfig getConfirmMenuConfig() {
        return confirmMenuConfig;
    }

    public SelectBeaconColorMenuConfig getSelectBeaconColorMenuConfig() {
        return selectBeaconColorMenuConfig;
    }

    public SelectFolderMenuConfig getSelectFolderMenuConfig() {
        return selectFolderMenuConfig;
    }

    public SelectWaypointTypeMenuConfig getSelectWaypointTypeMenuConfig() {
        return selectWaypointTypeMenuConfig;
    }

    public DefaultOverviewMenuConfig getDefaultOverviewMenuConfig() {
        return defaultOverviewMenuConfig;
    }

    public GlobalOverviewMenuConfig getPublicOverviewMenuConfig() {
        return publicOverviewMenuConfig;
    }

    public GlobalOverviewMenuConfig getPermissionOverviewMenuConfig() {
        return permissionOverviewMenuConfig;
    }

    public DeathWaypointMenuConfig getDeathWaypointMenuConfig() {
        return deathWaypointMenuConfig;
    }

    public GlobalWaypointMenuConfig getPublicWaypointMenuConfig() {
        return publicWaypointMenuConfig;
    }

    public GlobalWaypointMenuConfig getPermissionWaypointMenuConfig() {
        return permissionWaypointMenuConfig;
    }

    public PrivateWaypointMenuConfig getPrivateWaypointMenuConfig() {
        return privateWaypointMenuConfig;
    }

    public PrivateFolderMenuConfig getPrivateFolderMenuConfig() {
        return privateFolderMenuConfig;
    }
}
