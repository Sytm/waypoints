/*
 *     Waypoints
 *     Copyright (C) 2019  Lukas Planz
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

package de.md5lukas.wp.config;

import de.md5lukas.wp.Main;
import de.md5lukas.wp.PointerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Config {

	private static final File compassDefaultLocationFile = new File("plugins/Waypoints/compass.loc");

	// General settings
	public static String language;
	public static int maxWaypoints;
	public static Map<String, String> worldNameAliases;

	// Cache settings
	public static int uuidCacheMaxSize;
	public static long uuidCacheInvalidAfter;
	public static TimeUnit uuidCacheInvalidTimeUnit;

	// Inventory materials
	public static Material inventoryEmptyItem, inventoryArrowItem, inventoryDeselectItem, inventoryWaypointItem,
			inventorySelectWaypointItem, inventoryDeleteWaypointItem, inventoryBackItem, teleportToWaypointItem;

	// Actionbar parameters
	public static boolean actionBarEnabled;
	public static String actionBarIndicator, actionBarNormal, actionBarSection, actionBarArrowLeft, actionBarArrowRight;
	public static int actionBarAmountOfSections;
	public static double actionBarRange;
	public static boolean actionBarCrouchEnabled;

	// Compass parameters
	public static boolean compassEnabled, compassDefaultIsSpawn = false;
	public static Location compassDefaultLocation;

	// Flashing Block parameters
	public static boolean flashingBlockEnabled;
	private static List<Material> flashingBlockMaterials;
	public static int flashingBlockMinDistance;

	// Beacon parameters
	public static boolean beaconEnabled;
	public static int beaconMinDistance;

	public static boolean load(File file) {
		if (!file.exists()) {
			Main.logger().log(Level.SEVERE, "The config file is missing and therefore the plugin cannot operate");
			return false;
		}
		try {
			YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			language = cfg.getString("language").toLowerCase();
			maxWaypoints = cfg.getInt("general.maxWaypoints");
			Map<String, String> temp = new HashMap<>();
			String sectionPath = "general.worldNameAliases";
			for (String key : cfg.getConfigurationSection(sectionPath).getKeys(false)) {
				temp.put(key, cfg.getString(sectionPath + "." + key));
			}
			worldNameAliases = Collections.unmodifiableMap(temp);

			if (cfg.isInt("general.playerInfoCache.size")) {
				uuidCacheMaxSize = cfg.getInt("general.playerInfoCache.size");
			} else if ("auto".equalsIgnoreCase(cfg.getString("general.playerInfoCache.size"))) {
				uuidCacheMaxSize = (int) Math.round(Bukkit.getMaxPlayers() * 0.75);
			} else {
				Main.logger().log(Level.SEVERE, "The provided maximum cache size is neither a number or auto!");
				return false;
			}
			uuidCacheInvalidAfter = cfg.getLong("general.playerInfoCache.invalidAfter");
			try {
				uuidCacheInvalidTimeUnit = TimeUnit.valueOf(cfg.getString("general.playerInfoCache.timeUnit"));
			} catch (IllegalArgumentException iae) {
				Main.logger().log(Level.SEVERE, "The timeunit used in the cache section is not one of the valid options!");
				return false;
			}
			inventoryEmptyItem = Material.matchMaterial(cfg.getString("inventory.emptyItem"));
			inventoryArrowItem = Material.matchMaterial(cfg.getString("inventory.arrowItem"));
			inventoryDeselectItem = Material.matchMaterial(cfg.getString("inventory.deselectItem"));
			inventoryWaypointItem = Material.matchMaterial(cfg.getString("inventory.waypointItem"));
			inventorySelectWaypointItem = Material.matchMaterial(cfg.getString("inventory.selectWaypointItem"));
			inventoryDeleteWaypointItem = Material.matchMaterial(cfg.getString("inventory.deleteWaypointItem"));
			inventoryBackItem = Material.matchMaterial(cfg.getString("inventory.backItem"));
			teleportToWaypointItem = Material.matchMaterial(cfg.getString("inventory.teleportToWaypointItem"));

			if (!(inventoryEmptyItem.isItem() && inventoryArrowItem.isItem() && inventoryDeselectItem.isItem() && inventoryDeselectItem.isItem()
					&& inventoryWaypointItem.isItem() && inventorySelectWaypointItem.isItem() && inventoryDeleteWaypointItem.isItem()
					&& inventoryBackItem.isItem() && teleportToWaypointItem.isItem())) {
				Main.logger().log(Level.SEVERE, "An item type from the inventory section in the config.yml cannot be used in the inventory gui as it is not an item and therefore cannot be displayed in a inventory");
				return false;
			}

			actionBarEnabled = cfg.getBoolean("actionBar.enabled");
			actionBarIndicator = ChatColor.translateAlternateColorCodes('&', cfg.getString("actionBar.indicatorColor"));
			actionBarNormal = ChatColor.translateAlternateColorCodes('&', cfg.getString("actionBar.normalColor"));
			actionBarSection = cfg.getString("actionBar.section");
			actionBarArrowLeft = cfg.getString("actionBar.arrow.left");
			actionBarArrowRight = cfg.getString("actionBar.arrow.right");
			actionBarAmountOfSections = cfg.getInt("actionBar.amountOfSections");
			actionBarRange = cfg.getDouble("actionBar.range");

			actionBarCrouchEnabled = cfg.getBoolean("actionBar.crouch.enabled");

			compassEnabled = cfg.getBoolean("compass.enabled");
			switch (cfg.getString("compass.defaultLocationType").toLowerCase()) {
				case "ingame":
					if (compassDefaultLocationFile.exists()) {
						compassDefaultLocation = loadCompassLocation();
						if (compassDefaultLocation == null) {
							return false;
						}
						if (compassDefaultLocation.getWorld() == null) {
							Main.logger().log(Level.SEVERE, "The world that has been used to set the default compass target ingame is missing");
							return false;
						}
						break;
					}
					Main.logger().log(Level.WARNING, "In the config the default compass location source has been set to 'ingame', but it hasn't been set yet. Fallback to spawn location");
				case "spawn":
					compassDefaultIsSpawn = true;
					break;
				case "config":
					compassDefaultLocation = new Location(Bukkit.getWorld(cfg.getString("compass.defaultLocation.world")),
							cfg.getDouble("compass.defaultLocation.x"), cfg.getDouble("compass.defaultLocation.y"), cfg.getDouble("compass.defaultLocation.z"));
					if (compassDefaultLocation.getWorld() == null) {
						Main.logger().log(Level.SEVERE, "The world that has been set in the config as default compass target is missing");
						return false;
					}
					break;
				default:
					Main.logger().log(Level.SEVERE, "The setting 'compass.defaultLocationType' in the config.yml is either missing or a invalid option is chosen");
					return false;
			}

			flashingBlockEnabled = cfg.getBoolean("flashingBlock.enabled");
			flashingBlockMinDistance = (int) Math.round(Math.pow(cfg.getInt("flashingBlock.minDistance"), 2));
			flashingBlockMaterials = cfg.getStringList("flashingBlock.blocks").stream()
					.map(s -> Material.matchMaterial(s.toLowerCase())).filter(Objects::nonNull).collect(Collectors.toList());
			PointerManager.setupBlockData(flashingBlockMaterials);

			beaconEnabled = cfg.getBoolean("beacon.enabled");
			beaconMinDistance = (int) Math.round(Math.pow(cfg.getInt("beacon.minDistance"), 2));
		} catch (NullPointerException npe) {
			Main.logger().log(Level.SEVERE, "Some parts of the configuration file are missing. If you just upgraded from an older version, make a copy of your current config and delete it."
					+ "After that put in the values from the old config into the new one and reload / restart.");
			return false;
		}
		return true;
	}

	private static Location loadCompassLocation() {
		try (DataInputStream dis = new DataInputStream(new FileInputStream(compassDefaultLocationFile))) {
			byte[] string = new byte[dis.readInt()];
			dis.read(string);
			return new Location(Bukkit.getWorld(new String(string, StandardCharsets.UTF_8)), dis.readDouble(), dis.readDouble(), dis.readDouble());
		} catch (IOException e) {
			Main.logger().log(Level.SEVERE, "An error occurred while loading the default compass location", e);
		}
		return null;
	}

	public static boolean saveCompassLocation(Location location) {
		try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(compassDefaultLocationFile))) {
			dos.writeInt(location.getWorld().getName().getBytes(StandardCharsets.UTF_8).length);
			dos.write(location.getWorld().getName().getBytes(StandardCharsets.UTF_8));
			dos.writeDouble(location.getX());
			dos.writeDouble(location.getY());
			dos.writeDouble(location.getZ());
			dos.flush();
			return true;
		} catch (IOException e) {
			Main.logger().log(Level.SEVERE, "Error while saving location data for default compass position", e);
		}
		return false;
	}
}
