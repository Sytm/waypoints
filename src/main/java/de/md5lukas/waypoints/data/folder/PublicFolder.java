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

import de.md5lukas.commons.StringHelper;
import de.md5lukas.commons.collections.ReplaceableList;
import de.md5lukas.nbt.tags.CompoundTag;
import de.md5lukas.nbt.tags.ListTag;
import de.md5lukas.waypoints.data.waypoint.PrivateWaypoint;
import de.md5lukas.waypoints.data.waypoint.PublicWaypoint;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import de.md5lukas.waypoints.gui.GUIType;
import de.md5lukas.waypoints.store.WPConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

import static de.md5lukas.waypoints.Messages.INVENTORY_FOLDER_PUBLIC_DESCRIPTION;
import static de.md5lukas.waypoints.Messages.INVENTORY_FOLDER_PUBLIC_DISPLAY_NAME;
import static de.md5lukas.waypoints.Waypoints.message;

public class PublicFolder extends Folder {

	public PublicFolder(CompoundTag tag) {
		super(tag);
	}

	public PublicFolder() {
		super("");
	}

	@Override
	protected List<Waypoint> loadWaypoints(ListTag waypoints) {
		return waypoints.values().stream().map(tag -> new PublicWaypoint((CompoundTag) tag)).collect(Collectors.toList());
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
	public String getDisplayName(Player player) {
		return message(INVENTORY_FOLDER_PUBLIC_DISPLAY_NAME, player);
	}

	@Override
	public List<String> getDescription(Player player) {
		return new ReplaceableList(StringHelper.split(message(INVENTORY_FOLDER_PUBLIC_DESCRIPTION, player), '\n')).replace(
			"%amount%", Integer.toString(waypoints.size()));
	}

	@Override
	protected boolean isCorrectWaypointType(Waypoint waypoint) {
		return waypoint instanceof PublicWaypoint;
	}

	@Override
	public GUIType getType() {
		return GUIType.PUBLIC_FOLDER;
	}
}
