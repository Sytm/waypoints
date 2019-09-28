package de.md5lukas.waypoints.data.waypoint;

import de.md5lukas.commons.MathHelper;
import de.md5lukas.commons.collections.ReplaceableList;
import de.md5lukas.nbt.tags.CompoundTag;
import de.md5lukas.waypoints.store.WPConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

import static de.md5lukas.waypoints.Messages.INVENTORY_WAYPOINT_PERMISSION_DESCRIPTION;
import static de.md5lukas.waypoints.Messages.INVENTORY_WAYPOINT_PERMISSION_DISPLAY_NAME;
import static de.md5lukas.waypoints.Waypoints.message;

public class PermissionWaypoint extends Waypoint {

	private String permission;

	public PermissionWaypoint(CompoundTag tag) {
		super(tag);
		permission = tag.getString("permission");
	}

	public PermissionWaypoint(String name, Location location, String permission) {
		super(name, location);
		this.permission = permission;
	}

	@Override
	public Material getMaterial() {
		return material == null ? WPConfig.inventory().getWaypointPermissionItem() : material;
	}

	@Override
	public String getDisplayName(Player player) {
		return message(INVENTORY_WAYPOINT_PERMISSION_DISPLAY_NAME, player).replace("%name%", name);
	}

	@Override
	public List<String> getDescription(Player player) {
		return ReplaceableList.ofStrings(message(INVENTORY_WAYPOINT_PERMISSION_DESCRIPTION, player).split("\\n")).replace(
			"%world%", WPConfig.translateWorldName(location.getWorld().getName(), player),
			"%x%", MathHelper.format(location.getX()),
			"%y%", MathHelper.format(location.getY()),
			"%z%", MathHelper.format(location.getZ()),
			"%blockX%", Integer.toString(location.getBlockX()),
			"%blockY%", Integer.toString(location.getBlockY()),
			"%blockZ%", Integer.toString(location.getBlockZ()),
			"%distance%", getDistance2D(player));
	}

	public String getPermission() {
		return permission;
	}

	@Override
	public CompoundTag toCompoundTag() {
		CompoundTag tag = super.toCompoundTag();
		tag.putString("permission", permission);
		return tag;
	}
}
