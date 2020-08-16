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

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GeneralConfig {

    private String defaultLanguage;

    private boolean deathWaypointEnabled;
    private int waypointLimit, folderLimit;
    private OpenUsingCompass openUsingCompass;

    private final RenamingConfig renamingConfig;
    private final DuplicateNameConfig duplicateConfig;
    private final TeleportConfig teleportConfig;

    private boolean customItemEnabled;
    private boolean customItemFilterIsBlacklist;
    private List<Material> customItemFilter;

    public GeneralConfig(ConfigurationSection cfg) {
        renamingConfig = new RenamingConfig();
        duplicateConfig = new DuplicateNameConfig();
        teleportConfig = new TeleportConfig();

        load(cfg);
    }

    public void load(ConfigurationSection cfg) {
        defaultLanguage = cfg.getString("defaultLanguage");
        deathWaypointEnabled = cfg.getBoolean("deathWaypointEnabled");
        waypointLimit = cfg.getInt("limits.waypoints");
        folderLimit = cfg.getInt("limits.folders");

        openUsingCompass = OpenUsingCompass.getFromConfig(cfg.getString("openUsingCompass"));

        renamingConfig.load(Objects.requireNonNull(cfg.getConfigurationSection("allowRenaming")));
        duplicateConfig.load(Objects.requireNonNull(cfg.getConfigurationSection("allowDuplicateNames")));
        teleportConfig.load(Objects.requireNonNull(cfg.getConfigurationSection("teleport")));

        customItemEnabled = cfg.getBoolean("customItem.enabled");
        customItemFilterIsBlacklist = "blacklist".equalsIgnoreCase(cfg.getString("customItem.filter.useAs"));
        customItemFilter =
                cfg.getStringList("inventory.customItem.filter.list").stream().map(Material::matchMaterial).filter(Objects::nonNull)
                        .collect(Collectors.toList());
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public boolean isDeathWaypointEnabled() {
        return deathWaypointEnabled;
    }

    public int getWaypointLimit() {
        return waypointLimit;
    }

    public int getFolderLimit() {
        return folderLimit;
    }

    public OpenUsingCompass getOpenUsingCompass() {
        return openUsingCompass;
    }

    public RenamingConfig getRenamingConfig() {
        return renamingConfig;
    }

    public DuplicateNameConfig getDuplicateConfig() {
        return duplicateConfig;
    }

    public TeleportConfig getTeleportConfig() {
        return teleportConfig;
    }

    public boolean isCustomItemEnabled() {
        return customItemEnabled;
    }

    public boolean isValidCustomItem(Material material) {
        return customItemEnabled && customItemFilterIsBlacklist != customItemFilter.contains(material);
    }
}
