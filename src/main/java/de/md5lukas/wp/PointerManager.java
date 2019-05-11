package de.md5lukas.wp;

import de.md5lukas.wp.config.Config;
import de.md5lukas.wp.storage.Waypoint;
import de.md5lukas.wp.util.MathHelper;
import de.md5lukas.wp.util.StringHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static de.md5lukas.wp.config.Message.*;
import static de.md5lukas.wp.config.Messages.get;

public class PointerManager {

	private static boolean isPaper = false;

	private static final Map<UUID, Waypoint> activePointers;
	private static final Map<UUID, Integer> markerTimer;
	private static final Map<UUID, Location> beaconPosition;
	private static List<BlockData> blockDatas;

	static {
		activePointers = new HashMap<>();
		markerTimer = new HashMap<>();
		beaconPosition = new HashMap<>();
	}

	public static void setupBlockData(List<Material> materials) {
		blockDatas = new ArrayList<>();
		blockDatas.add(Bukkit.createBlockData(Material.BEACON));
		blockDatas.add(Bukkit.createBlockData(Material.IRON_BLOCK));
		blockDatas.addAll(materials.stream().map(Bukkit::createBlockData).collect(Collectors.toCollection(ArrayList::new)));
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
		try {
			Class.forName("com.destroystokyo.paper.PaperConfig");
			isPaper = true;
		} catch (ClassNotFoundException e) {
		}
		Bukkit.getScheduler().runTaskTimer(main, () -> {
			List<UUID> remove = new ArrayList<>();
			activePointers.forEach((key, value) -> {
				Player p = Bukkit.getPlayer(key);
				if (p == null) {
					remove.add(key);
					return;
				}
				if (p.getWorld() != value.getLocation().getWorld()) {
					p.sendActionBar(get(AB_WRONGWORLD)
							.replace("%currentworld%", StringHelper.correctWorldName(p.getWorld()))
							.replace("%correctworld%", StringHelper.correctWorldName(value.getLocation().getWorld())));
					return;
				}
				if (isPaper && Config.actionBarEnabled) {
					if (p.isSneaking() && Config.actionBarCrouchEnabled) {
						String text = get(AB_CROUCH_TEMPLATE);
						if (text.contains("%distance3d%")) {
							text = text.replace("%distance3d%", MathHelper.DF.format(p.getLocation().distance(value.getLocation())));
						}
						if (text.contains("%distance2d%")) {
							text = text.replace("%distance2d%", MathHelper.DF.format(p.getLocation().toVector().setY(0).distance(value.getLocation().toVector().setY(0))));
						}
						if (text.contains("%yoffset%")) {
							double diff = p.getLocation().getY() - value.getLocation().getY();
							if (diff > 1) {
								text = text.replace("%yoffset%", get(AB_CROUCH_TOHIGH).replace("%amount%", MathHelper.DF.format(diff)));
							} if (diff == 1) {
								text = text.replace("%yoffset%", get(AB_CROUCH_TOHIGH_SINGLE));
							} else if (diff < 1 && diff > -1) {
								text = text.replace("%yoffset%", get(AB_CROUCH_DIFFUNDERONE));
							} if (diff == -1) {
								text = text.replace("%yoffset%", get(AB_CROUCH_TOLOW_SINGLE));
							}  else {
								text = text.replace("%yoffset%", get(AB_CROUCH_TOLOW).replace("%amount%", MathHelper.DF.format(-diff)));
							}
						}
						p.sendActionBar(text);
					} else {
						double angle = MathHelper.deltaAngleToTarget(p, value.getLocation());
						p.sendActionBar(StringHelper.generateDirectionIndicator(Config.actionBarIndicator, Config.actionBarNormal,
								Config.actionBarArrowLeft, Config.actionBarArrowRight, Config.actionBarSection, angle, Config.actionBarAmountOfSections, Config.actionBarRange));
					}
				}
				if (Config.compassEnabled) {
					p.setCompassTarget(value.getLocation());
				}
				// To compensate for not using Math.sqrt here while loading the config from file distances are squared
				double distance = p.getLocation().distanceSquared(value.getLocation());
				if (distance > Config.flashingBlockMinDistance && distance < Math.pow(16 * p.getClientViewDistance(), 2)) {
					if (distance > Config.beaconMinDistance && Config.beaconEnabled) {
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
					if (!Config.flashingBlockEnabled)
						return;
					int timer = markerTimer.get(key);
					if (timer >= blockDatas.size() - 2)
						timer = 0;
					p.sendBlockChange(value.getLocation(), blockDatas.get(timer + 2));
					markerTimer.put(key, ++timer);
				} else {
					p.sendBlockChange(value.getLocation(), value.getLocation().getBlock().getBlockData());
				}
			});
			if (Config.compassEnabled)
				Bukkit.getOnlinePlayers().stream().filter(o -> !activePointers.containsKey(o.getUniqueId())).forEach(o -> o.setCompassTarget(defaultLocation(o)));
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

	private static Location defaultLocation(Player player) {
		if (Config.compassDefaultIsSpawn) {
			return player.getWorld().getSpawnLocation();
		}
		return Config.compassDefaultLocation;
	}
}
