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

public class DuplicateNameConfig {

    private boolean allowDuplicateNamesWaypointPrivate, allowDuplicateNamesWaypointPublic,
            allowDuplicateNamesWaypointPermission, allowDuplicateNamesFolderPrivate;

    public void load(ConfigurationSection cfg) {
        allowDuplicateNamesWaypointPrivate = cfg.getBoolean("waypoints.private");
        allowDuplicateNamesWaypointPublic = cfg.getBoolean("waypoints.public");
        allowDuplicateNamesWaypointPermission = cfg.getBoolean("waypoints.permission");
        allowDuplicateNamesFolderPrivate = cfg.getBoolean("folders.private");
    }

    public boolean isAllowDuplicateNamesWaypointPrivate() {
        return allowDuplicateNamesWaypointPrivate;
    }

    public boolean isAllowDuplicateNamesWaypointPublic() {
        return allowDuplicateNamesWaypointPublic;
    }

    public boolean isAllowDuplicateNamesWaypointPermission() {
        return allowDuplicateNamesWaypointPermission;
    }

    public boolean isAllowDuplicateNamesFolderPrivate() {
        return allowDuplicateNamesFolderPrivate;
    }
}
