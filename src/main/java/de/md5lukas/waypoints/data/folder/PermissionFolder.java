package de.md5lukas.waypoints.data.folder;

import de.md5lukas.commons.collections.ReplaceableList;
import de.md5lukas.nbt.tags.CompoundTag;
import de.md5lukas.nbt.tags.ListTag;
import de.md5lukas.waypoints.data.waypoint.PermissionWaypoint;
import de.md5lukas.waypoints.data.waypoint.PrivateWaypoint;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import de.md5lukas.waypoints.gui.GUIType;
import de.md5lukas.waypoints.store.WPConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

import static de.md5lukas.waypoints.Messages.INVENTORY_FOLDER_PERMISSION_DESCRIPTION;
import static de.md5lukas.waypoints.Messages.INVENTORY_FOLDER_PERMISSION_DISPLAY_NAME;
import static de.md5lukas.waypoints.Waypoints.message;

public class PermissionFolder extends Folder {

	public PermissionFolder(CompoundTag tag) {
		super(tag);
	}

	public PermissionFolder() {
		super("");
	}

	@Override
	protected List<Waypoint> loadWaypoints(ListTag waypoints) {
		return waypoints.values().stream().map(tag -> new PrivateWaypoint((CompoundTag) tag)).collect(Collectors.toList());
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
		return (int) Math.min(64, waypoints.stream().filter(wp -> player.hasPermission(((PermissionWaypoint) wp).getPermission())).count());
	}

	@Override
	public String getDisplayName(Player player) {
		return message(INVENTORY_FOLDER_PERMISSION_DISPLAY_NAME, player);
	}

	@Override
	public List<String> getDescription(Player player) {
		return ReplaceableList.ofStrings(message(INVENTORY_FOLDER_PERMISSION_DESCRIPTION, player).split("\\n")).replace(
			"%amount%", Integer.toString(waypoints.stream().filter(wp -> player.hasPermission(((PermissionWaypoint) wp).getPermission())).mapToInt(wp -> 1).sum()));
	}

	@Override
	public List<Waypoint> getWaypoints(Player player) {
		return waypoints.stream().filter(wp -> player.hasPermission(((PermissionWaypoint) wp).getPermission())).collect(Collectors.toList());
	}

	@Override
	protected boolean isCorrectWaypointType(Waypoint waypoint) {
		return waypoint instanceof PermissionWaypoint;
	}

	@Override
	public GUIType getType() {
		return GUIType.PERMISSION_FOLDER;
	}
}
