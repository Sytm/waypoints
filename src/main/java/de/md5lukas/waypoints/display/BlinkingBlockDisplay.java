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

package de.md5lukas.waypoints.display;

import de.md5lukas.commons.MathHelper;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static de.md5lukas.waypoints.store.WPConfig.displays;

public class BlinkingBlockDisplay extends WaypointDisplay {

	protected BlinkingBlockDisplay(Plugin plugin) {
		super(plugin, displays().getBlinkingBlockInterval());
	}

	private Map<UUID, Integer> counters = new HashMap<>();

	@Override
	public void show(Player player, Waypoint waypoint) {
		update(player, waypoint);
	}

	@Override
	public void update(Player player, Waypoint waypoint) {
		UUID pUUID = player.getUniqueId();
		double distance = MathHelper.distance2DSquared(player.getLocation(), waypoint.getLocation());
		if (distance < displays().getBlinkingBlockMinDistance() || distance > displays().getBlinkingBlockMaxDistance()) {
			if (counters.containsKey(pUUID)) {
				player.sendBlockChange(waypoint.getLocation(), waypoint.getLocation().getBlock().getBlockData());
				counters.remove(pUUID);
			}
			return;
		}
		counters.compute(pUUID, (uuid, count) -> count == null ? 0 : (count + 1) % displays().getBlinkingBlockBlocks().size());
		player.sendBlockChange(waypoint.getLocation(), displays().getBlinkingBlockBlocks().get(counters.get(player.getUniqueId())));
	}

	@Override
	public void disable(Player player) {
		counters.remove(player.getUniqueId());
	}
}