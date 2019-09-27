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

package de.md5lukas.waypoints.data.waypoint;

import de.md5lukas.commons.MathHelper;
import de.md5lukas.commons.collections.ReplaceableList;
import de.md5lukas.nbt.tags.CompoundTag;
import de.md5lukas.waypoints.gui.GUIType;
import de.md5lukas.waypoints.store.WPConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

import static de.md5lukas.waypoints.Messages.*;
import static de.md5lukas.waypoints.Waypoints.message;

public class DeathWaypoint extends Waypoint {

	public DeathWaypoint(CompoundTag tag) {
		super(tag);
	}

	public DeathWaypoint(Location location) {
		super("", location);
	}

	@Override
	public Material getMaterial() {
		return WPConfig.inventory().getWaypointDeathItem();
	}

	@Override
	public String getDisplayName(Player player) {
		return message(INVENTORY_WAYPOINT_DEATH_DISPLAY_NAME, player);
	}

	@Override
	public GUIType getType() {
		return GUIType.DEATH_WAYPOINT;
	}

	@Override
	public List<String> getDescription(Player player) {
		return ReplaceableList.ofStrings(message(INVENTORY_WAYPOINT_DEATH_DESCRIPTION, player).split("\\n")).replace(
			"%world%", WPConfig.translateWorldName(location.getWorld().getName(), player),
			"%x%", MathHelper.format(location.getX()),
			"%y%", MathHelper.format(location.getY()),
			"%z%", MathHelper.format(location.getZ()),
			"%blockX%", Integer.toString(location.getBlockX()),
			"%blockY%", Integer.toString(location.getBlockY()),
			"%blockZ%", Integer.toString(location.getBlockZ()),
			"%distance%", MathHelper.format(MathHelper.distance2D(player.getLocation(), location)));
	}
}
