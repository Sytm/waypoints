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

import org.bukkit.configuration.ConfigurationSection;

public class RenamingConfig {

    private boolean allowRenamingWaypointsPrivate, allowRenamingWaypointsPublic, allowRenamingWaypointsPermission, allowRenamingFoldersPrivate;

    public void load(ConfigurationSection cfg) {
        allowRenamingWaypointsPrivate = cfg.getBoolean("waypoints.private");
        allowRenamingWaypointsPublic = cfg.getBoolean("waypoints.public");
        allowRenamingWaypointsPermission = cfg.getBoolean("waypoints.permission");
        allowRenamingFoldersPrivate = cfg.getBoolean("folders.private");
    }

    public boolean isAllowRenamingWaypointsPrivate() {
        return allowRenamingWaypointsPrivate;
    }

    public boolean isAllowRenamingWaypointsPublic() {
        return allowRenamingWaypointsPublic;
    }

    public boolean isAllowRenamingWaypointsPermission() {
        return allowRenamingWaypointsPermission;
    }

    public boolean isAllowRenamingFoldersPrivate() {
        return allowRenamingFoldersPrivate;
    }
}
