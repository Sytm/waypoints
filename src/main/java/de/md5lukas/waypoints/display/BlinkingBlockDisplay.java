package de.md5lukas.waypoints.display;

import de.md5lukas.commons.MathHelper;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static de.md5lukas.waypoints.store.WPConfig.displays;

public class BlinkingBlockDisplay extends WaypointDisplay {

	protected BlinkingBlockDisplay(Plugin plugin) {
		super(plugin, displays().getBlinkingBlockInterval());
	}

	private Map<UUID, Integer> counters = new HashMap<>();

	@Override
	public void show(Player player, Waypoint waypoint) {
		update(player, waypoint);
	}

	@Override
	public void update(Player player, Waypoint waypoint) {
		UUID pUUID = player.getUniqueId();
		double distance = MathHelper.distance2DSquared(player.getLocation(), waypoint.getLocation());
		if (distance < displays().getBlinkingBlockMinDistance() || distance > displays().getBlinkingBlockMaxDistance()) {
			if (counters.containsKey(pUUID)) {
				player.sendBlockChange(waypoint.getLocation(), waypoint.getLocation().getBlock().getBlockData());
				counters.remove(pUUID);
			}
			return;
		}
		counters.compute(pUUID, (uuid, count) -> count == null ? 0 : (count + 1) % displays().getBlinkingBlockBlocks().size());
		player.sendBlockChange(waypoint.getLocation(), displays().getBlinkingBlockBlocks().get(counters.get(player.getUniqueId())));
	}

	@Override
	public void disable(Player player) {
		counters.remove(player.getUniqueId());
	}
}
