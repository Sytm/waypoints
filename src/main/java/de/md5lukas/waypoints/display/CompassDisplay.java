package de.md5lukas.waypoints.display;

import de.md5lukas.nbt.tags.CompoundTag;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import de.md5lukas.waypoints.store.LocationTag;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class CompassDisplay extends WaypointDisplay {

	protected CompassDisplay(Plugin plugin) {
		super(plugin, 0);
	}

	@Override
	public void show(Player player, Waypoint waypoint) {
		CompoundTag store = getStore(player);
		store.put("location", new LocationTag(null, player.getCompassTarget()));
		player.setCompassTarget(waypoint.getLocation());
	}

	@Override
	public void update(Player player, Waypoint waypoint) {}

	@Override
	public void disable(Player player) {
		CompoundTag store = getStore(player);
		if (store.contains("location")) {
			player.setCompassTarget(((LocationTag) store.get("location")).value());
			store.put("location", null);
		}
	}
}
