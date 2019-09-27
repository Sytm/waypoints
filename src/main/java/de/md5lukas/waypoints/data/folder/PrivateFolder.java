package de.md5lukas.waypoints.data.folder;

import de.md5lukas.commons.collections.ReplaceableList;
import de.md5lukas.nbt.tags.CompoundTag;
import de.md5lukas.nbt.tags.ListTag;
import de.md5lukas.waypoints.data.waypoint.PrivateWaypoint;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import de.md5lukas.waypoints.gui.GUIType;
import de.md5lukas.waypoints.store.WPConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

import static de.md5lukas.waypoints.Messages.INVENTORY_FOLDER_PRIVATE_DESCRIPTION;
import static de.md5lukas.waypoints.Messages.INVENTORY_FOLDER_PRIVATE_DISPLAY_NAME;
import static de.md5lukas.waypoints.Waypoints.message;

public class PrivateFolder extends Folder {

	public PrivateFolder(CompoundTag tag) {
		super(tag);
	}

	public PrivateFolder(String name) {
		super(name);
	}

	@Override
	protected List<Waypoint> loadWaypoints(ListTag waypoints) {
		return waypoints.values().stream().map(tag -> new PrivateWaypoint((CompoundTag) tag)).collect(Collectors.toList());
	}

	@Override
	public Material getMaterial() {
		return material == null ? WPConfig.inventory().getFolderPrivateDefaultItem() : material;
	}

	@Override
	public String getDisplayName(Player player) {
		return message(INVENTORY_FOLDER_PRIVATE_DISPLAY_NAME, player);
	}

	@Override
	public List<String> getDescription(Player player) {
		return ReplaceableList.ofStrings(message(INVENTORY_FOLDER_PRIVATE_DESCRIPTION, player).split("\\n")).replace(
			"%amount%", Integer.toString(waypoints.size()));
	}

	@Override
	protected boolean isCorrectWaypointType(Waypoint waypoint) {
		return waypoint instanceof PrivateWaypoint;
	}

	@Override
	public GUIType getType() {
		return GUIType.PRIVATE_FOLDER;
	}
}
