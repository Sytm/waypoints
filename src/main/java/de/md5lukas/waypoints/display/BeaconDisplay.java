package de.md5lukas.waypoints.display;

import de.md5lukas.commons.MathHelper;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static de.md5lukas.waypoints.store.WPConfig.displays;

public final class BeaconDisplay extends WaypointDisplay {

	private static final BlockData BLOCK_DATA_BEACON = Bukkit.createBlockData(Material.BEACON);

	private Map<UUID, Location> activeBeacons;

	BeaconDisplay(Plugin plugin) {
		super(plugin, displays().getBeaconInterval());
		activeBeacons = new HashMap<>();
	}

	@Override
	public void show(Player player, Waypoint waypoint) {
		update(player, waypoint);
	}

	@Override
	public void update(Player player, Waypoint waypoint) {
		if (player.getWorld().equals(waypoint.getLocation().getWorld())) {
			UUID uuid = player.getUniqueId();
			double distance = MathHelper.distance2DSquared(player.getLocation(), waypoint.getLocation());
			Location loc = waypoint.getLocation().getWorld().getHighestBlockAt(waypoint.getLocation()).getLocation();
			if (distance < displays().getBeaconMinDistance() || distance > displays().getBeaconMaxDistance()) {
				if (activeBeacons.containsKey(uuid)) {
					sendBeacon(player, loc, false);
				}
				return;
			}
			if (activeBeacons.containsKey(uuid)) {
				if (!blockEquals(activeBeacons.get(uuid), loc)) {
					sendBeacon(player, activeBeacons.get(uuid), false);
				}
			}
			activeBeacons.put(uuid, loc);
			sendBeacon(player, loc, true);
		}
	}

	@Override
	public void disable(Player player) {
		UUID uuid = player.getUniqueId();
		if (activeBeacons.containsKey(uuid) && Objects.equals(player.getLocation().getWorld(), activeBeacons.get(uuid).getWorld())) {
			sendBeacon(player, activeBeacons.get(uuid), false);
		}
		activeBeacons.remove(uuid);
	}

	private static void sendBeacon(Player player, Location location, boolean create) {
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				location.add(x, 0, z);
				if (create) {
					player.sendBlockChange(location, displays().getBeaconBaseBlock());
				} else {
					player.sendBlockChange(location, location.getBlock().getBlockData());
				}
				location.subtract(x, 0, z);
			}
		}
		location.add(0, 1, 0);
		if (create) {
			player.sendBlockChange(location, BLOCK_DATA_BEACON);
		} else {
			player.sendBlockChange(location, location.getBlock().getBlockData());
		}
		location.subtract(0, 1, 0);
	}

	private static boolean blockEquals(Location loc1, Location loc2) {
		return loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ();
	}
}
