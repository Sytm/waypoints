/*
 *     waypoints2
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

package de.md5lukas.waypoints.display;

import de.md5lukas.waypoints.data.waypoint.Waypoint;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import static de.md5lukas.waypoints.store.WPConfig.displays;

public final class ParticleDisplay extends WaypointDisplay {

	protected ParticleDisplay(Plugin plugin) {
		super(plugin, displays().getParticlesInterval());
	}

	@Override
	public void show(Player player, Waypoint waypoint) {
		update(player, waypoint);
	}

	@Override
	public void update(Player player, Waypoint waypoint) {
		if (player.getWorld().equals(waypoint.getLocation().getWorld())) {
			Location pLoc = player.getLocation();
			Vector dir = waypoint.getLocation().toVector().subtract(pLoc.toVector()).normalize().multiply(displays().getParticlesDistance());
			for (int i = 0; i < displays().getParticlesAmount(); i++) {
				player.spawnParticle(displays().getParticlesParticle(),
					pLoc.getX() + dir.getX() * i,
					pLoc.getY() + displays().getParticlesHeightOffset() + (displays().isParticlesVerticalDirection() ? dir.getY() * i : 0),
					pLoc.getZ() + dir.getZ() * i,
					1, 0, 0, 0, 0);
			}
		}
	}

	@Override
	public void disable(Player player) {
	}
}
