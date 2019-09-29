/*
 *     Waypoints2, A plugin for spigot to add waypoints functionality
 *     Copyright (C) 2019  Lukas Planz
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

package de.md5lukas.waypoints.listener;

import de.md5lukas.waypoints.data.WPPlayerData;
import de.md5lukas.waypoints.store.WPConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static org.bukkit.event.EventPriority.LOWEST;

public class WaypointsListener implements Listener {

	@EventHandler(priority = LOWEST)
	public void onDeath(PlayerDeathEvent e) {
		if (WPConfig.isDeathWaypointEnabled())
			WPPlayerData.getPlayerData(e.getEntity().getUniqueId()).setDeath(e.getEntity().getLocation());
	}
}
