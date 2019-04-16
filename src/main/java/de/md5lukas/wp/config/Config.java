package de.md5lukas.wp.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Config {

	public static String language;

	// Actionbar parameters
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
		try {
			YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			language = cfg.getString("language").toLowerCase();

			actionBarIndicator = cfg.getString("actionBar.indicatorColor");
			actionBarNormal = cfg.getString("actionBar.normalColor");
			actionBarSection = cfg.getString("actionBar.section");
			actionBarArrowLeft = cfg.getString("actionBar.arrow.left");
			actionBarArrowRight = cfg.getString("actionBar.arrow.right");
			actionBarAmountOfSections = cfg.getInt("actionBar.amountOfSections");
			actionBarRange = cfg.getDouble("actionBar.range");

			enableFlashingBlock = cfg.getBoolean("flashingBlock.enabled");
			minFlashingBlockDistance = cfg.getInt("flashingBlock.minDistance");
			flashingBlockMaterials = cfg.getStringList("flashingBlock.blocks").stream()
					.map(s -> Material.matchMaterial(s.toLowerCase())).filter(Objects::nonNull).collect(Collectors.toList());
			
			enableBeacon = cfg.getBoolean("beacon.enabled");
			minBeaconDistance = cfg.getInt("beacon.minDistance");
		} catch (NullPointerException e) {
			System.err.println("Some variables from the config are missing and therefore the plugin cannot operate");
			return false;
		}
		return true;
	}
}
