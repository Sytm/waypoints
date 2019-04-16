package de.md5lukas.wp.config;

import de.md5lukas.wp.PointerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Config {

	public static String language;
	public static int maxWaypoints;

	// Actionbar parameters //p.sendActionBar(StringHelper.generateDirectionIndicator("§2§l", "§7§l", "⬛", angle, 35, 70));
	public static String actionBarIndicator, actionBarNormal, actionBarSection, actionBarArrowLeft, actionBarArrowRight;
	public static int actionBarAmountOfSections;
	public static double actionBarRange;
	// Other display method
	public static boolean enableFlashingBlock, enableBeacon;
	public static List<Material> flashingBlockMaterials;
	public static int minFlashingBlockDistance, minBeaconDistance;

	public static boolean load(File file) {
		if (!file.exists()) {
			System.err.println("The config file is missing and therefore the plugin cannot operate");
			return false;
		}
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		language = cfg.getString("language").toLowerCase();
		maxWaypoints = cfg.getInt("general.maxWaypoints");

		actionBarIndicator = ChatColor.translateAlternateColorCodes('&', cfg.getString("actionBar.indicatorColor"));
		actionBarNormal = ChatColor.translateAlternateColorCodes('&', cfg.getString("actionBar.normalColor"));
		actionBarSection = cfg.getString("actionBar.section");
		actionBarArrowLeft = cfg.getString("actionBar.arrow.left");
		actionBarArrowRight = cfg.getString("actionBar.arrow.right");
		actionBarAmountOfSections = cfg.getInt("actionBar.amountOfSections");
		actionBarRange = cfg.getDouble("actionBar.range");

		enableFlashingBlock = cfg.getBoolean("flashingBlock.enabled");
		minFlashingBlockDistance = (int) Math.round(Math.pow(cfg.getInt("flashingBlock.minDistance"), 2));
		flashingBlockMaterials = cfg.getStringList("flashingBlock.blocks").stream()
				.map(s -> Material.matchMaterial(s.toLowerCase())).filter(Objects::nonNull).collect(Collectors.toList());
		PointerManager.setupBlockData(flashingBlockMaterials);

		enableBeacon = cfg.getBoolean("beacon.enabled");
		minBeaconDistance = (int) Math.round(Math.pow(cfg.getInt("beacon.minDistance"), 2));
		return true;
	}
}
