package de.md5lukas.wp;

import de.md5lukas.wp.storage.Waypoint;
import de.md5lukas.wp.util.MathHelper;
import de.md5lukas.wp.util.StringHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class PointerManager {


	private static Map<UUID, Waypoint> activePointers;
	private static Map<UUID, Integer> markerTimer;
	private static Map<UUID, Location> beaconPosition;
	private static List<Material> hightlighterBlocks = Arrays.asList(Material.GLASS, Material.GLOWSTONE);
	private static List<BlockData> blockDatas;


	static {
		activePointers = new HashMap<>();
		markerTimer = new HashMap<>();
		beaconPosition = new HashMap<>();
		blockDatas = new ArrayList<>();
		blockDatas.add(Bukkit.createBlockData(Material.BEACON));
		blockDatas.add(Bukkit.createBlockData(Material.IRON_BLOCK));
		blockDatas.addAll(hightlighterBlocks.stream().map(Bukkit::createBlockData).collect(Collectors.toCollection(ArrayList::new)));
	}

	public static void activate(UUID uuid, Waypoint waypoint) {
		Player p = Bukkit.getPlayer(uuid);
		if (p != null) {
			if (activePointers.containsKey(uuid)) {
				Waypoint wp = activePointers.get(uuid);
				p.sendBlockChange(wp.getLocation(), wp.getLocation().getBlock().getBlockData());
			}
			if (beaconPosition.containsKey(uuid)) {
				clearBeacon(p, beaconPosition.get(uuid));
			}
		}
		activePointers.put(uuid, waypoint);
		markerTimer.put(uuid, 0);
	}

	public static void deactivate(UUID uuid) {
		Player p = Bukkit.getPlayer(uuid);
		if (p != null) {
			if (activePointers.containsKey(uuid)) {
				Waypoint wp = activePointers.get(uuid);
				p.sendBlockChange(wp.getLocation(), wp.getLocation().getBlock().getBlockData());
			}
			if (beaconPosition.containsKey(uuid)) {
				clearBeacon(p, beaconPosition.get(uuid));
			}
		}
		activePointers.remove(uuid);
		markerTimer.remove(uuid);
		beaconPosition.remove(uuid);
	}

	public static Waypoint activeWaypoint(UUID uuid) {
		return activePointers.get(uuid);
	}

	public static void activateTimer(Main main) {
		Bukkit.getScheduler().runTaskTimer(main, () -> {
			List<UUID> remove = new ArrayList<>();
			activePointers.forEach((key, value) -> {
				Player p = Bukkit.getPlayer(key);
				if (p == null) {
					remove.add(key);
					return;
				}
				if (p.getWorld() != value.getLocation().getWorld()) {
					p.sendActionBar("§cDu bist in der falschen Welt! (Korrekte Welt: §a" + value.getLocation().getWorld().getName() + "§c)");
					return;
				}
				double angle = MathHelper.deltaAngleToTarget(p, value.getLocation());
				p.sendActionBar(StringHelper.generateDirectionIndicator("§2§l", "§7§l", "⬛", angle, 35, 70));
				double distance = p.getLocation().distanceSquared(value.getLocation());
				if (distance > 100 /* Squared 10 */ && distance < Math.pow(16 * p.getClientViewDistance(), 2)) {
					if (distance > 2500 /* Squared 50 */) {
						Location target = value.getLocation().getWorld().getHighestBlockAt(value.getLocation()).getLocation();
						Location oldTarget = beaconPosition.get(key);
						if (sameXZ(target, oldTarget)) {
							if (target.getBlockY() != oldTarget.getBlockY()) {
								clearBeacon(p, oldTarget);
								beaconPosition.remove(key);
							}
						}
						if (target.getY() + 2 < value.getLocation().getWorld().getMaxHeight()) {
							beaconPosition.put(key, target.clone());
							p.sendBlockChange(target, blockDatas.get(0));
							p.sendBlockChange(target.add(0, -1, 0), blockDatas.get(1));

							p.sendBlockChange(target.add(1, 0, 0), blockDatas.get(1));
							target.subtract(1, 0, 0);

							p.sendBlockChange(target.add(1, 0, 1), blockDatas.get(1));
							target.subtract(1, 0, 1);

							p.sendBlockChange(target.add(1, 0, -1), blockDatas.get(1));
							target.subtract(1, 0, -1);

							p.sendBlockChange(target.add(0, 0, 1), blockDatas.get(1));
							target.subtract(0, 0, 1);

							p.sendBlockChange(target.add(0, 0, -1), blockDatas.get(1));
							target.subtract(0, 0, -1);

							p.sendBlockChange(target.add(-1, 0, 0), blockDatas.get(1));
							target.subtract(-1, 0, 0);

							p.sendBlockChange(target.add(-1, 0, 1), blockDatas.get(1));
							target.subtract(-1, 0, 1);

							p.sendBlockChange(target.add(-1, 0, -1), blockDatas.get(1));
							target.subtract(-1, 0, -1);
							return;
						}
					}
					if (beaconPosition.containsKey(key)) {
						clearBeacon(p, beaconPosition.get(key));
						beaconPosition.remove(key);
					}
					int timer = markerTimer.get(key);
					if (timer >= hightlighterBlocks.size())
						timer = 0;
					p.sendBlockChange(value.getLocation(), blockDatas.get(timer + 2));
					markerTimer.put(key, ++timer);
				} else {
					p.sendBlockChange(value.getLocation(), value.getLocation().getBlock().getBlockData());
				}
			});
			remove.forEach(uuid -> activePointers.remove(uuid));
		}, 20, 20);
	}

	private static void clearBeacon(Player p, Location location) {
		p.sendBlockChange(location, location.getBlock().getBlockData());
		p.sendBlockChange(location.add(0, -1, 0), location.getBlock().getBlockData());

		p.sendBlockChange(location.add(1, 0, 0), location.getBlock().getBlockData());
		location.subtract(1, 0, 0);

		p.sendBlockChange(location.add(1, 0, 1), location.getBlock().getBlockData());
		location.subtract(1, 0, 1);

		p.sendBlockChange(location.add(1, 0, -1), location.getBlock().getBlockData());
		location.subtract(1, 0, -1);

		p.sendBlockChange(location.add(0, 0, 1), location.getBlock().getBlockData());
		location.subtract(0, 0, 1);

		p.sendBlockChange(location.add(0, 0, -1), location.getBlock().getBlockData());
		location.subtract(0, 0, -1);

		p.sendBlockChange(location.add(-1, 0, 0), location.getBlock().getBlockData());
		location.subtract(-1, 0, 0);

		p.sendBlockChange(location.add(-1, 0, 1), location.getBlock().getBlockData());
		location.subtract(-1, 0, 1);

		p.sendBlockChange(location.add(-1, 0, -1), location.getBlock().getBlockData());
		location.subtract(-1, 0, -1);
	}

	private static boolean sameXZ(Location loc1, Location loc2) {
		if (loc1 == null || loc2 == null)
			return false;
		return loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockZ() == loc2.getBlockZ();
	}
}
