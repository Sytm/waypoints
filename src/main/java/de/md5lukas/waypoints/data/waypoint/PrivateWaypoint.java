package de.md5lukas.waypoints.data.waypoint;

import de.md5lukas.commons.MathHelper;
import de.md5lukas.commons.collections.ReplaceableList;
import de.md5lukas.nbt.tags.CompoundTag;
import de.md5lukas.waypoints.store.WPConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

import static de.md5lukas.waypoints.Messages.INVENTORY_WAYPOINT_PRIVATE_DESCRIPTION;
import static de.md5lukas.waypoints.Messages.INVENTORY_WAYPOINT_PRIVATE_DISPLAY_NAME;
import static de.md5lukas.waypoints.Waypoints.message;

public class PrivateWaypoint extends Waypoint {

	public PrivateWaypoint(CompoundTag tag) {
		super(tag);
	}

	public PrivateWaypoint(String name, Location location) {
		super(name, location);
	}

	@Override
	public Material getMaterial() {
		return material == null ? WPConfig.inventory().getWaypointPrivateDefaultItem() : material;
	}

	@Override
	public String getDisplayName(Player player) {
		return message(INVENTORY_WAYPOINT_PRIVATE_DISPLAY_NAME, player).replace("%name%", name);
	}

	@Override
	public List<String> getDescription(Player player) {
		return ReplaceableList.ofStrings(message(INVENTORY_WAYPOINT_PRIVATE_DESCRIPTION, player).split("\\n")).replace(
			"%world%", WPConfig.translateWorldName(location.getWorld().getName(), player),
			"%x%", MathHelper.format(location.getX()),
			"%y%", MathHelper.format(location.getY()),
			"%z%", MathHelper.format(location.getZ()),
			"%blockX%", Integer.toString(location.getBlockX()),
			"%blockY%", Integer.toString(location.getBlockY()),
			"%blockZ%", Integer.toString(location.getBlockZ()),
			"%distance%", MathHelper.format(MathHelper.distance2D(player.getLocation(), location)));
	}

	public static PrivateWaypoint fromCompoundTag(CompoundTag tag) {
		return new PrivateWaypoint(tag);
	}
}
