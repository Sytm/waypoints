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

package de.md5lukas.waypoints.config;

import de.md5lukas.waypoints.config.displays.DisplayConfig;
import de.md5lukas.waypoints.config.general.GeneralConfig;
import de.md5lukas.waypoints.config.inventory.InventoryConfig;
import org.bukkit.configuration.file.FileConfiguration;

public class WPConfig {

    private static DisplayConfig displayConfig;
    private static GeneralConfig generalConfig;
    private static InventoryConfig inventoryConfig;

    public static DisplayConfig getDisplayConfig() {
        return displayConfig;
    }

    public static GeneralConfig getGeneralConfig() {
        return generalConfig;
    }

    public static InventoryConfig getInventoryConfig() {
        return inventoryConfig;
    }

    public static void loadConfig(FileConfiguration cfg) {
        generalConfig = new GeneralConfig(cfg.getConfigurationSection("general"));
        displayConfig = new DisplayConfig(cfg.getConfigurationSection("displays"));
        inventoryConfig = new InventoryConfig(cfg.getConfigurationSection("inventory"));
    }
}
