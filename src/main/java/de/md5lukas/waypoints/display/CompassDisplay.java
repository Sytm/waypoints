package de.md5lukas.waypoints.display;

import de.md5lukas.nbt.tags.CompoundTag;
import de.md5lukas.waypoints.Waypoints;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import de.md5lukas.waypoints.store.LocationTag;
import de.md5lukas.waypoints.store.WPConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static de.md5lukas.waypoints.store.WPConfig.displays;

public final class CompassDisplay extends WaypointDisplay {

	protected CompassDisplay(Plugin plugin) {
		super(plugin, 0);
	}

	@Override
	public void show(Player player, Waypoint waypoint) {
		if (displays().getCompassDefaultLocationType() == WPConfig.DefaultCompassLocationType.PREVIOUS) {
			CompoundTag store = getStore(player);
			store.put("location", new LocationTag(null, player.getCompassTarget()));
		}
		player.setCompassTarget(waypoint.getLocation());
	}

	@Override
	public void update(Player player, Waypoint waypoint) {}

	@Override
	public void disable(Player player) {
		switch (displays().getCompassDefaultLocationType()) {
			case SPAWN:
				player.setCompassTarget(Bukkit.getWorlds().get(0).getSpawnLocation());
				break;
			case CONFIG:
				player.setCompassTarget(displays().getCompassDefaultLocation());
				break;
			case PREVIOUS:
				CompoundTag store = getStore(player);
				if (store.contains("location")) {
					player.setCompassTarget(((LocationTag) store.get("location")).value());
					store.put("location", null);
				}
				break;
			case INGAME:
			case INGAME_LOCK:
				player.setCompassTarget(Waypoints.getGlobalStore().getCompassTarget());
				break;
		}
	}
}
