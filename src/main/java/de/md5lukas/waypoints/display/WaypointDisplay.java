/*
 *     Waypoints2, A plugin for spigot to add waypoints functionality
 *     Copyright (C) 2020  Lukas Planz
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.md5lukas.waypoints.display;

import de.md5lukas.commons.tuples.Tuple2;
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

import static com.google.common.base.Preconditions.checkNotNull;
import static de.md5lukas.waypoints.store.WPConfig.displays;

public abstract class WaypointDisplay implements Listener {

	private final static Map<String, Tuple2<Supplier<Boolean>, Supplier<WaypointDisplay>>> displays = new HashMap<>();
	private final static List<WaypointDisplay> activeDisplays = new ArrayList<>();
	private final static AllWaypointDisplays global = new AllWaypointDisplays();
	private final static Map<Player, Map<String, BukkitTask>> updateTasks = new HashMap<>();
	private final static Map<Player, Waypoint> activeWaypoint = new HashMap<>();

	static {
		Bukkit.getPluginManager().registerEvents(global, Waypoints.instance());
		registerDisplay("compass", () -> displays().isCompassEnabled(), () -> new CompassDisplay(Waypoints.instance()));
		registerDisplay("beacon", () -> displays().isBeaconEnabled(), () -> new BeaconDisplay(Waypoints.instance()));
		registerDisplay("blinkingBlock", () -> displays().isBlinkingBlockEnabled(), () -> new BlinkingBlockDisplay(Waypoints.instance()));
		registerDisplay("wrongWorld", () -> displays().isWrongWorldEnabled(), () -> new WrongWorldDisplay(Waypoints.instance()));
		registerDisplay("actionBar", () -> displays().isActionBarEnabled(), () -> new ActionBarDisplay(Waypoints.instance()));
		registerDisplay("particles", () -> displays().isParticlesEnabled(), () -> new ParticleDisplay(Waypoints.instance()));
	}

	public static void activateDisplays() {
		displays.forEach((type, checkerAndFactory) -> {
			if (checkerAndFactory.getL().get()) {
				WaypointDisplay display = checkerAndFactory.getR().get();
				display.type = type;
				Bukkit.getPluginManager().registerEvents(display, Waypoints.instance());
				activeDisplays.add(display);
			}
		});
	}

	public static void registerDisplay(String type, Supplier<Boolean> isActiveCheck, Supplier<WaypointDisplay> factory) {
		displays.put(checkNotNull(type, "The type name cannot be null"),
			Tuple2.of(checkNotNull(isActiveCheck, "The is active check cannot be null"), checkNotNull(factory, "The display factory cannot be null")));
	}

	public static AllWaypointDisplays getAll() {
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

	public abstract void disable(Player player, Waypoint waypoint);

	protected final CompoundTag getStore(Player player) {
		return WPPlayerData.getPlayerData(player.getUniqueId()).getCustomTag("display").getCompound(type);
	}

	protected final Waypoint getActiveWaypoint(Player player) {
		return activeWaypoint.get(player);
	}

	public final static class AllWaypointDisplays implements Listener {

		private Map<Player, Waypoint> lastActiveWaypoint;


		private AllWaypointDisplays() {
			lastActiveWaypoint = new HashMap<>();
		}

		public void show(Player player, Waypoint waypoint) {
			Waypoint lastWaypoint = lastActiveWaypoint.get(player);
			lastActiveWaypoint.put(player, waypoint);
			activeDisplays.forEach(wd -> {
				if (lastWaypoint != null)
					wd.disable(player, lastWaypoint);
				wd.show(player, waypoint);
				if (wd.updateInterval > 0) {
					updateTasks.computeIfPresent(player, (p, tasks) -> {
						tasks.computeIfPresent(wd.type, (type, task) -> {
							task.cancel();
							return null;
						});
						return tasks;
					});

					BukkitTask task = Bukkit.getScheduler().runTaskTimer(Waypoints.instance(), () -> {
						wd.update(player, waypoint);
					}, wd.updateInterval, wd.updateInterval);

					updateTasks.compute(player, (p, tasks) -> {
						if (tasks == null)
							tasks = new HashMap<>();
						tasks.put(wd.type, task);
						return tasks;
					});
				}
			});
		}

		public void disable(Player player) {
			Waypoint lastWaypoint = lastActiveWaypoint.get(player);
			lastActiveWaypoint.remove(player);
			activeDisplays.forEach(wd -> {
				wd.disable(player, lastWaypoint);
				if (wd.updateInterval > 0) {
					updateTasks.computeIfPresent(player, (p, tasks) -> {
						tasks.computeIfPresent(wd.type, (type, task) -> {
							task.cancel();
							return null;
						});
						return tasks;
					});
				}
			});
		}

		@EventHandler
		public void onQuit(PlayerQuitEvent e) {
			disable(e.getPlayer());
		}
	}
}
