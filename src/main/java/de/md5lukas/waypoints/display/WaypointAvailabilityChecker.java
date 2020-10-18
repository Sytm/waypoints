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

package de.md5lukas.waypoints.display;

import de.md5lukas.waypoints.data.waypoint.PermissionWaypoint;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import de.md5lukas.waypoints.store.WPConfig;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class WaypointAvailabilityChecker extends WaypointDisplay {

    protected WaypointAvailabilityChecker(Plugin plugin) {
        super(plugin, 20 * 2 /* 2 seconds */);
    }

    @Override
    public void show(Player player, Waypoint waypoint) {

    }

    @Override
    public void update(Player player, Waypoint waypoint) {
        if (waypoint instanceof PermissionWaypoint) {
            PermissionWaypoint pwp = (PermissionWaypoint) waypoint;
            if (!player.hasPermission(pwp.getPermission())) {
                WaypointDisplay.getAll().disable(player);
                return;
            }
        }
        if (WPConfig.getDeselectRangeSquared() > 0
                && player.getLocation().distanceSquared(waypoint.getLocation()) <= WPConfig.getDeselectRangeSquared()) {
            WaypointDisplay.getAll().disable(player);
        }
    }

    @Override
    public void disable(Player player, Waypoint waypoint) {

    }
}
