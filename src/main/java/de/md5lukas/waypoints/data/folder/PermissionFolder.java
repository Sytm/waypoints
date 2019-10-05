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

package de.md5lukas.waypoints.data.folder;

import de.md5lukas.nbt.tags.CompoundTag;
import de.md5lukas.nbt.tags.ListTag;
import de.md5lukas.waypoints.data.waypoint.PermissionWaypoint;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import de.md5lukas.waypoints.gui.GUIType;
import de.md5lukas.waypoints.store.WPConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

import static de.md5lukas.waypoints.Messages.INVENTORY_FOLDER_PERMISSION_DESCRIPTION;
import static de.md5lukas.waypoints.Messages.INVENTORY_FOLDER_PERMISSION_DISPLAY_NAME;

public class PermissionFolder extends Folder {

	public PermissionFolder(CompoundTag tag) {
		super(tag);
	}

	public PermissionFolder() {
		super("");
	}

	@Override
	protected List<Waypoint> loadWaypoints(ListTag waypoints) {
		return waypoints.values().stream().map(tag -> new PermissionWaypoint((CompoundTag) tag)).collect(Collectors.toList());
	}

	@Override
	public Material getMaterial() {
		return WPConfig.inventory().getFolderPrivateDefaultItem();
	}

	@Override
	public long createdAt() {
		return 0;
	}

	@Override
	public int getAmount(Player player) {
		return (int) Math.min(64, count(player));
	}

	@Override
	public String getDisplayName(Player player) {
		return INVENTORY_FOLDER_PERMISSION_DISPLAY_NAME.getRaw(player);
	}

	@Override
	public List<String> getDescription(Player player) {
		return INVENTORY_FOLDER_PERMISSION_DESCRIPTION.asList(player).replace(
			"%amount%", Long.toString(count(player)));
	}

	@Override
	public List<Waypoint> getWaypoints(Player player) {
		return waypoints.stream().filter(wp -> player.hasPermission("waypoints.gui.permission.*") || player.hasPermission(((PermissionWaypoint) wp).getPermission())).collect(Collectors.toList());
	}

	@Override
	protected boolean isCorrectWaypointType(Waypoint waypoint) {
		return waypoint instanceof PermissionWaypoint;
	}

	@Override
	public GUIType getType() {
		return GUIType.PERMISSION_FOLDER;
	}

	private long count(Player player) {
		return waypoints.stream().filter(wp -> player.hasPermission(((PermissionWaypoint) wp).getPermission())).count();
	}
}
