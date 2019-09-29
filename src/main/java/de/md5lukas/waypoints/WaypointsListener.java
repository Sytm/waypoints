package de.md5lukas.waypoints;

import de.md5lukas.waypoints.data.WPPlayerData;
import de.md5lukas.waypoints.store.WPConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static org.bukkit.event.EventPriority.LOWEST;

public class WaypointsListener implements Listener {

	@EventHandler(priority = LOWEST)
	public void onDeath(PlayerDeathEvent e) {
		if (WPConfig.isDeathWaypointEnabled())
			WPPlayerData.getPlayerData(e.getEntity().getUniqueId()).setDeath(e.getEntity().getLocation());
	}
}
