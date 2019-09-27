package de.md5lukas.waypoints.display;

import de.md5lukas.nbt.tags.CompoundTag;
import de.md5lukas.waypoints.Waypoints;
import de.md5lukas.waypoints.data.WPPlayerData;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public abstract class WaypointDisplay implements Listener {

	private static Map<String, Supplier<WaypointDisplay>> displayFactories = new HashMap<>();
	private static List<WaypointDisplay> activeDisplays = new ArrayList<>();
	private static WaypointDisplay global = new GlobalWaypointDisplay();
	private static Map<String, BukkitTask> updateTasks = new HashMap<>();

	static {
		Bukkit.getPluginManager().registerEvents(global, Waypoints.instance());
		registerDisplay("compass", () -> new CompassDisplay(Waypoints.instance()));
		registerDisplay("beacon", () -> new BeaconDisplay(Waypoints.instance()));
		registerDisplay("wrongWorld", () -> new WrongWorldDisplay(Waypoints.instance()));
		registerDisplay("actionBar", () -> new ActionBarDisplay(Waypoints.instance()));
	}

	public static void activateDisplays(List<String> types) {
		for (String type : checkNotNull(types, "The types to activate cannot be null")) {
			checkArgument(displayFactories.containsKey(type), "The type " + type + " is not registered");
			WaypointDisplay display = displayFactories.get(type).get();
			display.type = type;
			activeDisplays.add(display);
			Bukkit.getPluginManager().registerEvents(display, Waypoints.instance());
		}
	}

	public static void registerDisplay(String type, Supplier<WaypointDisplay> factory) {
		displayFactories.put(checkNotNull(type, "The type name cannot be null"), checkNotNull(factory, "The display factory cannot be null"));
	}

	public static WaypointDisplay getAll() {
		return global;
	}

	protected final Plugin plugin;
	private String type;
	protected final long updateInterval;

	protected WaypointDisplay(Plugin plugin, long updateInterval) {
		this.plugin = plugin;
		this.updateInterval = updateInterval;
	}

	public abstract void show(Player player, Waypoint waypoint);

	public abstract void update(Player player, Waypoint waypoint);

	public abstract void disable(Player player);

	protected final CompoundTag getStore(Player player) {
		return WPPlayerData.getPlayerData(player.getUniqueId()).getCustomTag("display").getCompound(type);
	}

	private static class GlobalWaypointDisplay extends WaypointDisplay {

		protected GlobalWaypointDisplay() {
			super(null, 0);
		}

		@Override
		public void show(Player player, Waypoint waypoint) {
			activeDisplays.forEach(wd -> {
				wd.show(player, waypoint);
				if (wd.updateInterval > 0) {
					updateTasks.computeIfPresent(wd.type, (type, task) -> {
						task.cancel();
						return null;
					});

					BukkitTask task = Bukkit.getScheduler().runTaskTimer(Waypoints.instance(), () -> {
						wd.update(player, waypoint);
					}, wd.updateInterval, wd.updateInterval);

					updateTasks.put(wd.type, task);
				}
			});
		}

		@Override
		public void update(Player player, Waypoint waypoint) {
			activeDisplays.stream().filter(wd -> wd.updateInterval <= 0).forEach(wd -> update(player, waypoint));
		}

		@Override
		public void disable(Player player) {
			activeDisplays.forEach(wd -> {
				if (wd.updateInterval > 0) {
					updateTasks.computeIfPresent(wd.type, (type, task) -> {
						task.cancel();
						return null;
					});
				}
				wd.disable(player);
			});
		}

		@EventHandler
		public void onQuit(PlayerQuitEvent e) {
			disable(e.getPlayer());
		}
	}
}
