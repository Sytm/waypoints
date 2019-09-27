package de.md5lukas.waypoints.data.waypoint;

import de.md5lukas.commons.MathHelper;
import de.md5lukas.commons.collections.ReplaceableList;
import de.md5lukas.nbt.tags.CompoundTag;
import de.md5lukas.waypoints.store.WPConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

import static de.md5lukas.waypoints.Messages.INVENTORY_WAYPOINT_PUBLIC_DESCRIPTION;
import static de.md5lukas.waypoints.Messages.INVENTORY_WAYPOINT_PUBLIC_DISPLAY_NAME;
import static de.md5lukas.waypoints.Waypoints.message;

public class PublicWaypoint extends Waypoint {

	public PublicWaypoint(CompoundTag tag) {
		super(tag);
	}

	public PublicWaypoint(String name, Location location) {
		super(name, location);
	}

	@Override
	public Material getMaterial() {
		return material == null ? WPConfig.inventory().getWaypointPublicItem() : material;
	}

	@Override
	public String getDisplayName(Player player) {
		return message(INVENTORY_WAYPOINT_PUBLIC_DISPLAY_NAME, player).replace("%name%", name);
	}

	@Override
	public List<String> getDescription(Player player) {
		return ReplaceableList.ofStrings(message(INVENTORY_WAYPOINT_PUBLIC_DESCRIPTION, player).split("\\n")).replace(
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
