package de.md5lukas.waypoints.store;

import de.md5lukas.commons.UUIDUtils;
import de.md5lukas.commons.messages.Languages;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static de.md5lukas.commons.MathHelper.square;
import static org.bukkit.Material.matchMaterial;

public class WPConfig {

	private static Map<String, Map<String, String>> worldNameAliases;
	private static WPConfigDisplays displays;
	private static WPConfigInventory inventory;
	private static int waypointLimit;
	private static boolean deathWaypointEnabled;
	private static boolean allowDuplicateFolderPrivateNames, allowDuplicateWaypointNamesPrivate, allowDuplicateWaypointNamesPublic, allowDuplicateWaypointNamesPermission;
	private static boolean allowRenamingWaypointsPrivate, allowRenamingWaypointsPublic, allowRenamingWaypointsPermission, allowRenamingFoldersPrivate;

	public static WPConfigDisplays displays() {
		return displays;
	}

	public static WPConfigInventory inventory() {
		return inventory;
	}

	public static String translateWorldName(String world, String language) {
		if (!worldNameAliases.containsKey(language))
			language = Languages.getDefaultLanguage();
		return worldNameAliases.get(language).get(world);
	}

	public static String translateWorldName(String world, CommandSender sender) {
		return translateWorldName(world, Languages.getLanguage(sender instanceof Player ? ((Player) sender).getUniqueId() : UUIDUtils.ZERO_UUID));
	}

	public static int getWaypointLimit() {
		return waypointLimit;
	}

	public static boolean isDeathWaypointEnabled() {
		return deathWaypointEnabled;
	}

	public static boolean allowDuplicateFolderPrivateNames() {
		return allowDuplicateFolderPrivateNames;
	}

	public static boolean allowDuplicateWaypointNamesPrivate() {
		return allowDuplicateWaypointNamesPrivate;
	}

	public static boolean allowDuplicateWaypointNamesPublic() {
		return allowDuplicateWaypointNamesPublic;
	}

	public static boolean allowDuplicateWaypointNamesPermission() {
		return allowDuplicateWaypointNamesPermission;
	}

	public static boolean allowRenamingWaypointsPrivate() {
		return allowRenamingWaypointsPrivate;
	}

	public static boolean allowRenamingWaypointsPublic() {
		return allowRenamingWaypointsPublic;
	}

	public static boolean allowRenamingWaypointsPermission() {
		return allowRenamingWaypointsPermission;
	}

	public static boolean allowRenamingFoldersPrivate() {
		return allowRenamingFoldersPrivate;
	}

	@SuppressWarnings("ConstantConditions")
	public static void loadConfig(FileConfiguration cfg) {
		//<editor-fold defaultstate="collapsed" desc="General">
		waypointLimit = cfg.getInt("general.waypointLimit");

		deathWaypointEnabled = cfg.getBoolean("general.deathWaypointsEnabled");

		allowDuplicateFolderPrivateNames = cfg.getBoolean("general.allowDuplicatePrivateFolderNames");
		allowDuplicateWaypointNamesPrivate = cfg.getBoolean("general.allowDuplicateWaypointNames.private");
		allowDuplicateWaypointNamesPublic = cfg.getBoolean("general.allowDuplicateWaypointNames.public");
		allowDuplicateWaypointNamesPermission = cfg.getBoolean("general.allowDuplicateWaypointNames.permission");

		allowRenamingFoldersPrivate = cfg.getBoolean("general.allowRenamingPrivateFolders");
		allowRenamingWaypointsPrivate = cfg.getBoolean("general.allowRenamingWaypoints.private");
		allowRenamingWaypointsPublic = cfg.getBoolean("general.allowRenamingWaypoints.public");
		allowRenamingWaypointsPermission = cfg.getBoolean("general.allowRenamingWaypoints.permission");

		worldNameAliases = new HashMap<>();
		for (String lang : cfg.getConfigurationSection("general.worldNameAliases").getKeys(false)) {
			Map<String, String> aliases = new HashMap<>();
			worldNameAliases.put(lang.toLowerCase(), aliases);
			for (String world : cfg.getConfigurationSection("general.worldNameAliases." + lang).getKeys(false)) {
				aliases.put(world, cfg.getString("general.worldNameAliases." + lang + "." + world));
			}
		}
		//</editor-fold>
		//<editor-fold defaultstate="collapsed" desc="Displays">
		displays = new WPConfigDisplays();

		displays.wrongWorldEnabled = cfg.getBoolean("displays.wrongWorld.enabled");
		displays.wrongWorldActionBar = cfg.getBoolean("displays.wrongWorld.actionBar");
		displays.wrongWorldInterval = cfg.getInt("displays.wrongWorld.interval");

		displays.actionBarEnabled = cfg.getBoolean("displays.actionBar.enabled");
		displays.actionBarInterval = cfg.getInt("displays.actionBar.interval");
		displays.actionBarIndicatorColor = ChatColor.translateAlternateColorCodes('&', cfg.getString("displays.actionBar.indicatorColor"));
		displays.actionBarNormalColor = ChatColor.translateAlternateColorCodes('&', cfg.getString("displays.actionBar.normalColor"));
		displays.actionBarSection = cfg.getString("displays.actionBar.section");
		displays.actionBarLeftArrow = cfg.getString("displays.actionBar.arrow.left");
		displays.actionBarRightArrow = cfg.getString("displays.actionBar.arrow.right");
		displays.actionBarAmountOfSections = cfg.getInt("displays.actionBar.amountOfSections");
		if (displays.actionBarAmountOfSections % 2 == 0)
			displays.actionBarAmountOfSections++;
		displays.actionBarRange = cfg.getInt("displays.actionBar.range");

		displays.compassEnabled = cfg.getBoolean("displays.compass.enabled");
		displays.compassDefaultLocationType = DefaultCompassLocationType.getFromConfig(cfg.getString("displays.compass.defaultLocationType"));
		if (displays.compassDefaultLocationType == DefaultCompassLocationType.CONFIG) {
			displays.compassDefaultLocation = new Location(Bukkit.getWorld(cfg.getString("displays.compass.defaultLocation.world")),
				cfg.getDouble("displays.compass.defaultLocation.x"), 0, cfg.getDouble("displays.compass.defaultLocation"));
		}

		displays.blinkingBlockEnabled = cfg.getBoolean("displays.blinkingBlock.enabled");
		displays.blinkingBlockMinDistance = square(cfg.getInt("displays.blinkingBlock.minDistance"));
		displays.blinkingBlockMaxDistance = square(cfg.getInt("displays.blinkingBlock.maxDistance"));
		displays.blinkingBlockInterval = cfg.getInt("displays.blinkingBlock.interval");
		displays.blinkingBlockBlocks = cfg.getStringList("displays.blinkingBlock.blocks").stream()
			.map(Material::matchMaterial).filter(Objects::nonNull).map(Bukkit::createBlockData).collect(Collectors.toList());

		displays.beaconEnabled = cfg.getBoolean("displays.beacon.enabled");
		displays.beaconMinDistance = square(cfg.getInt("displays.beacon.minDistance"));
		if (cfg.isString("displays.beacon.maxDistance") && cfg.getString("displays.beacon.maxDistance").equalsIgnoreCase("auto")) {
			displays.beaconMaxDistance = square(Bukkit.getViewDistance() * 16);
		} else {
			displays.beaconMaxDistance = square(cfg.getInt("displays.beacon.maxDistance"));
		}
		displays.beaconBaseBlock = Bukkit.createBlockData(matchMaterial(cfg.getString("displays.beacon.baseBlock")));
		displays.beaconInterval = cfg.getInt("displays.beacon.interval");

		displays.particlesEnabled = cfg.getBoolean("displays.particles.enabled");
		displays.particlesInterval = cfg.getInt("displays.particles.interval");
		displays.particlesHeightOffset = cfg.getDouble("displays.particles.heightOffset");
		displays.particlesVerticalDirection = cfg.getBoolean("displays.particles.verticalDirection");
		displays.particlesAmount = cfg.getInt("displays.particles.amount");
		displays.particlesDistance = cfg.getDouble("displays.particles.distance");
		displays.particlesParticle = Particle.valueOf(cfg.getString("displays.particles.particle").toUpperCase());
		//</editor-fold>
		//<editor-fold defaultstate="collapsed" desc="Inventory">
		inventory = new WPConfigInventory();

		inventory.maxDescriptionLineLength = cfg.getInt("inventory.maxDescriptionLineLength");

		inventory.confirmMenuDescriptionItem = matchMaterial(cfg.getString("inventory.confirmMenu.descriptionItem"));
		inventory.confirmMenuYesItem = matchMaterial(cfg.getString("inventory.confirmMenu.yesItem"));
		inventory.confirmMenuNoItem = matchMaterial(cfg.getString("inventory.confirmMenu.noItem"));
		inventory.confirmMenuBackgroundItem = matchMaterial(cfg.getString("inventory.confirmMenu.backgroundItem"));

		inventory.generalPreviousItem = matchMaterial(cfg.getString("inventory.generalItems.previousItem"));
		inventory.generalNextItem = matchMaterial(cfg.getString("inventory.generalItems.nextItem"));
		inventory.generalBackItem = matchMaterial(cfg.getString("inventory.generalItems.backItem"));

		inventory.customItemEnabled = cfg.getBoolean("inventory.customItem.enabled");
		inventory.customItemFilterIsBlacklist = "blacklist".equalsIgnoreCase(cfg.getString("inventory.customItem.filter.useAs"));
		inventory.customItemFilter =
			cfg.getStringList("inventory.customItem.filter.list").stream().map(Material::matchMaterial).filter(Objects::nonNull).collect(Collectors.toList());

		inventory.overviewBackgroundItem = matchMaterial(cfg.getString("inventory.overview.backgroundItem"));
		inventory.overviewCycleSortItem = matchMaterial(cfg.getString("inventory.overview.cycleSortItem"));
		inventory.overviewDeselectItem = matchMaterial(cfg.getString("inventory.overview.deselectItem"));
		inventory.overviewToggleGlobalsItem = matchMaterial(cfg.getString("inventory.overview.toggleGlobalsItem"));

		inventory.waypointDeathItem = matchMaterial(cfg.getString("inventory.waypoints.death.item"));
		inventory.waypointDeathBackgroundItem = matchMaterial(cfg.getString("inventory.waypoints.death.backgroundItem"));
		inventory.waypointDeathSelectItem = matchMaterial(cfg.getString("inventory.waypoints.death.selectItem"));
		inventory.waypointDeathTeleportItem = matchMaterial(cfg.getString("inventory.waypoints.death.teleportItem"));

		inventory.waypointPrivateDefaultItem = matchMaterial(cfg.getString("inventory.waypoints.private.defaultItem"));
		inventory.waypointPrivateBackgroundItem = matchMaterial(cfg.getString("inventory.waypoints.private.backgroundItem"));
		inventory.waypointPrivateSelectItem = matchMaterial(cfg.getString("inventory.waypoints.private.selectItem"));
		inventory.waypointPrivateDeleteItem = matchMaterial(cfg.getString("inventory.waypoints.private.deleteItem"));
		inventory.waypointPrivateRenameItem = matchMaterial(cfg.getString("inventory.waypoints.private.renameItem"));
		inventory.waypointPrivateMoveToFolderItem = matchMaterial(cfg.getString("inventory.waypoints.private.moveToFolderItem"));
		inventory.waypointPrivateTeleportItem = matchMaterial(cfg.getString("inventory.waypoints.private.teleportItem"));

		inventory.waypointPublicItem = matchMaterial(cfg.getString("inventory.waypoints.public.item"));
		inventory.waypointPublicBackgroundItem = matchMaterial(cfg.getString("inventory.waypoints.public.backgroundItem"));
		inventory.waypointPublicSelectItem = matchMaterial(cfg.getString("inventory.waypoints.public.selectItem"));
		inventory.waypointPublicDeleteItem = matchMaterial(cfg.getString("inventory.waypoints.public.deleteItem"));
		inventory.waypointPublicRenameItem = matchMaterial(cfg.getString("inventory.waypoints.public.renameItem"));
		inventory.waypointPublicTeleportItem = matchMaterial(cfg.getString("inventory.waypoints.public.teleportItem"));

		inventory.waypointPermissionItem = matchMaterial(cfg.getString("inventory.waypoints.permission.item"));
		inventory.waypointPermissionBackgroundItem = matchMaterial(cfg.getString("inventory.waypoints.permission.backgroundItem"));
		inventory.waypointPermissionSelectItem = matchMaterial(cfg.getString("inventory.waypoints.permission.selectItem"));
		inventory.waypointPermissionDeleteItem = matchMaterial(cfg.getString("inventory.waypoints.permission.deleteItem"));
		inventory.waypointPermissionRenameItem = matchMaterial(cfg.getString("inventory.waypoints.permission.renameItem"));
		inventory.waypointPermissionTeleportItem = matchMaterial(cfg.getString("inventory.waypoints.permission.teleportItem"));

		inventory.selectFolderBackgroundItem = matchMaterial(cfg.getString("inventory.selectFolder.backgroundItem"));
		inventory.selectFolderNoFolderItem = matchMaterial(cfg.getString("inventory.selectFolder.noFolderItem"));

		inventory.folderPrivateDefaultItem = matchMaterial(cfg.getString("inventory.folders.private.defaultItem"));
		inventory.folderPrivateBackgroundItem = matchMaterial(cfg.getString("inventory.folders.private.backgroundItem"));
		inventory.folderPrivateDeleteItem = matchMaterial(cfg.getString("inventory.folders.private.deleteItem"));
		inventory.folderPrivateRenameItem = matchMaterial(cfg.getString("inventory.folders.private.renameItem"));

		inventory.folderPublicItem = matchMaterial(cfg.getString("inventory.folders.public.item"));
		inventory.folderPublicBackgroundItem = matchMaterial(cfg.getString("inventory.folders.public.backgroundItem"));

		inventory.folderPermissionItem = matchMaterial(cfg.getString("inventory.folders.permission.item"));
		inventory.folderPermissionBackgroundItem = matchMaterial(cfg.getString("inventory.folders.permission.backgroundItem"));
		//</editor-fold>
	}

	public final static class WPConfigDisplays {

		private WPConfigDisplays() {}

		private boolean wrongWorldEnabled;
		private boolean wrongWorldActionBar;
		private int wrongWorldInterval;

		private boolean actionBarEnabled;
		private int actionBarInterval;
		private String actionBarIndicatorColor;
		private String actionBarNormalColor;
		private String actionBarSection;
		private String actionBarLeftArrow;
		private String actionBarRightArrow;
		private int actionBarAmountOfSections;
		private int actionBarRange;

		private boolean compassEnabled;
		private DefaultCompassLocationType compassDefaultLocationType;
		private Location compassDefaultLocation;

		private boolean blinkingBlockEnabled;
		private long blinkingBlockMinDistance;
		private long blinkingBlockMaxDistance;
		private List<BlockData> blinkingBlockBlocks;
		private int blinkingBlockInterval;

		private boolean beaconEnabled;
		private long beaconMinDistance;
		private long beaconMaxDistance;
		private BlockData beaconBaseBlock;
		private int beaconInterval;

		private boolean particlesEnabled;
		private int particlesInterval;
		private double particlesHeightOffset;
		private boolean particlesVerticalDirection;
		private int particlesAmount;
		private double particlesDistance;
		private Particle particlesParticle;

		public boolean isWrongWorldEnabled() {
			return wrongWorldEnabled;
		}

		public boolean isWrongWorldActionBar() {
			return wrongWorldActionBar;
		}

		public long getWrongWorldInterval() {
			return wrongWorldInterval;
		}

		public boolean isActionBarEnabled() {
			return actionBarEnabled;
		}

		public int getActionBarInterval() {
			return actionBarInterval;
		}

		public String getActionBarIndicatorColor() {
			return actionBarIndicatorColor;
		}

		public String getActionBarNormalColor() {
			return actionBarNormalColor;
		}

		public String getActionBarSection() {
			return actionBarSection;
		}

		public String getActionBarLeftArrow() {
			return actionBarLeftArrow;
		}

		public String getActionBarRightArrow() {
			return actionBarRightArrow;
		}

		public int getActionBarAmountOfSections() {
			return actionBarAmountOfSections;
		}

		public int getActionBarRange() {
			return actionBarRange;
		}

		public boolean isCompassEnabled() {
			return compassEnabled;
		}

		public DefaultCompassLocationType getCompassDefaultLocationType() {
			return compassDefaultLocationType;
		}

		public Location getCompassDefaultLocation() {
			return compassDefaultLocation;
		}

		public boolean isBlinkingBlockEnabled() {
			return blinkingBlockEnabled;
		}

		/**
		 * @return the squared min distance
		 */
		public long getBlinkingBlockMinDistance() {
			return blinkingBlockMinDistance;
		}

		/**
		 * This value is s
		 *
		 * @return the squared max distance
		 */
		public long getBlinkingBlockMaxDistance() {
			return blinkingBlockMaxDistance;
		}

		public List<BlockData> getBlinkingBlockBlocks() {
			return blinkingBlockBlocks;
		}

		public int getBlinkingBlockInterval() {
			return blinkingBlockInterval;
		}

		public boolean isBeaconEnabled() {
			return beaconEnabled;
		}

		public long getBeaconMinDistance() {
			return beaconMinDistance;
		}

		public long getBeaconMaxDistance() {
			return beaconMaxDistance;
		}

		public int getBeaconInterval() {
			return beaconInterval;
		}

		public BlockData getBeaconBaseBlock() {
			return beaconBaseBlock;
		}

		public boolean isParticlesEnabled() {
			return particlesEnabled;
		}

		public int getParticlesInterval() {
			return particlesInterval;
		}

		public double getParticlesHeightOffset() {
			return particlesHeightOffset;
		}

		public boolean isParticlesVerticalDirection() {
			return particlesVerticalDirection;
		}

		public int getParticlesAmount() {
			return particlesAmount;
		}

		public double getParticlesDistance() {
			return particlesDistance;
		}

		public Particle getParticlesParticle() {
			return particlesParticle;
		}
	}

	public final static class WPConfigInventory {

		private int maxDescriptionLineLength;

		private WPConfigInventory() {}

		private Material confirmMenuDescriptionItem;
		private Material confirmMenuYesItem;
		private Material confirmMenuNoItem;
		private Material confirmMenuBackgroundItem;

		private Material generalPreviousItem;
		private Material generalNextItem;
		private Material generalBackItem;

		private boolean customItemEnabled;
		private boolean customItemFilterIsBlacklist;
		private List<Material> customItemFilter;

		private Material overviewBackgroundItem;
		private Material overviewCycleSortItem;
		private Material overviewDeselectItem;
		private Material overviewToggleGlobalsItem;

		private Material waypointDeathItem;
		private Material waypointDeathBackgroundItem;
		private Material waypointDeathSelectItem;
		private Material waypointDeathTeleportItem;

		private Material waypointPrivateDefaultItem;
		private Material waypointPrivateBackgroundItem;
		private Material waypointPrivateSelectItem;
		private Material waypointPrivateDeleteItem;
		private Material waypointPrivateRenameItem;
		private Material waypointPrivateMoveToFolderItem;
		private Material waypointPrivateTeleportItem;

		private Material waypointPublicItem;
		private Material waypointPublicBackgroundItem;
		private Material waypointPublicSelectItem;
		private Material waypointPublicDeleteItem;
		private Material waypointPublicRenameItem;
		private Material waypointPublicTeleportItem;

		private Material waypointPermissionItem;
		private Material waypointPermissionBackgroundItem;
		private Material waypointPermissionSelectItem;
		private Material waypointPermissionDeleteItem;
		private Material waypointPermissionRenameItem;
		private Material waypointPermissionTeleportItem;

		private Material selectFolderBackgroundItem;
		private Material selectFolderNoFolderItem;

		private Material folderPrivateDefaultItem;
		private Material folderPrivateBackgroundItem;
		private Material folderPrivateDeleteItem;
		private Material folderPrivateRenameItem;

		private Material folderPublicItem;
		private Material folderPublicBackgroundItem;

		private Material folderPermissionItem;
		private Material folderPermissionBackgroundItem;

		public int getMaxDescriptionLineLength() {
			return maxDescriptionLineLength;
		}

		public Material getConfirmMenuDescriptionItem() {
			return confirmMenuDescriptionItem;
		}

		public Material getConfirmMenuYesItem() {
			return confirmMenuYesItem;
		}

		public Material getConfirmMenuNoItem() {
			return confirmMenuNoItem;
		}

		public Material getConfirmMenuBackgroundItem() {
			return confirmMenuBackgroundItem;
		}

		public Material getGeneralPreviousItem() {
			return generalPreviousItem;
		}

		public Material getGeneralNextItem() {
			return generalNextItem;
		}

		public Material getGeneralBackItem() {
			return generalBackItem;
		}

		public boolean isCustomItemEnabled() {
			return customItemEnabled;
		}

		public boolean isValidCustomItem(Material material) {
			return customItemEnabled && customItemFilterIsBlacklist != customItemFilter.contains(material);
		}

		public Material getOverviewCycleSortItem() {
			return overviewCycleSortItem;
		}

		public Material getOverviewBackgroundItem() {
			return overviewBackgroundItem;
		}

		public Material getOverviewDeselectItem() {
			return overviewDeselectItem;
		}

		public Material getOverviewToggleGlobalsItem() {
			return overviewToggleGlobalsItem;
		}

		public Material getWaypointDeathItem() {
			return waypointDeathItem;
		}

		public Material getWaypointDeathBackgroundItem() {
			return waypointDeathBackgroundItem;
		}

		public Material getWaypointDeathSelectItem() {
			return waypointDeathSelectItem;
		}

		public Material getWaypointDeathTeleportItem() {
			return waypointDeathTeleportItem;
		}

		public Material getWaypointPrivateDefaultItem() {
			return waypointPrivateDefaultItem;
		}

		public Material getWaypointPrivateBackgroundItem() {
			return waypointPrivateBackgroundItem;
		}

		public Material getWaypointPrivateSelectItem() {
			return waypointPrivateSelectItem;
		}

		public Material getWaypointPrivateDeleteItem() {
			return waypointPrivateDeleteItem;
		}

		public Material getWaypointPrivateRenameItem() {
			return waypointPrivateRenameItem;
		}

		public Material getWaypointPrivateMoveToFolderItem() {
			return waypointPrivateMoveToFolderItem;
		}

		public Material getWaypointPrivateTeleportItem() {
			return waypointPrivateTeleportItem;
		}

		public Material getWaypointPublicItem() {
			return waypointPublicItem;
		}

		public Material getWaypointPublicBackgroundItem() {
			return waypointPublicBackgroundItem;
		}

		public Material getWaypointPublicSelectItem() {
			return waypointPublicSelectItem;
		}

		public Material getWaypointPublicDeleteItem() {
			return waypointPublicDeleteItem;
		}

		public Material getWaypointPublicRenameItem() {
			return waypointPublicRenameItem;
		}

		public Material getWaypointPublicTeleportItem() {
			return waypointPublicTeleportItem;
		}

		public Material getWaypointPermissionItem() {
			return waypointPermissionItem;
		}

		public Material getWaypointPermissionBackgroundItem() {
			return waypointPermissionBackgroundItem;
		}

		public Material getWaypointPermissionSelectItem() {
			return waypointPermissionSelectItem;
		}

		public Material getWaypointPermissionDeleteItem() {
			return waypointPermissionDeleteItem;
		}

		public Material getWaypointPermissionRenameItem() {
			return waypointPermissionRenameItem;
		}

		public Material getWaypointPermissionTeleportItem() {
			return waypointPermissionTeleportItem;
		}

		public Material getSelectFolderBackgroundItem() {
			return selectFolderBackgroundItem;
		}

		public Material getSelectFolderNoFolderItem() {
			return selectFolderNoFolderItem;
		}

		public Material getFolderPrivateDefaultItem() {
			return folderPrivateDefaultItem;
		}

		public Material getFolderPrivateBackgroundItem() {
			return folderPrivateBackgroundItem;
		}

		public Material getFolderPrivateDeleteItem() {
			return folderPrivateDeleteItem;
		}

		public Material getFolderPrivateRenameItem() {
			return folderPrivateRenameItem;
		}

		public Material getFolderPublicItem() {
			return folderPublicItem;
		}

		public Material getFolderPublicBackgroundItem() {
			return folderPublicBackgroundItem;
		}

		public Material getFolderPermissionItem() {
			return folderPermissionItem;
		}

		public Material getFolderPermissionBackgroundItem() {
			return folderPermissionBackgroundItem;
		}
	}

	public enum DefaultCompassLocationType {
		SPAWN("spawn"), CONFIG("config"), PREVIOUS("previous"), INGAME("ingame"), INGAME_LOCK("ingame-lock");

		private String inConfig;

		DefaultCompassLocationType(String inConfig) {
			this.inConfig = inConfig;
		}

		public static DefaultCompassLocationType getFromConfig(String inConfig) {
			List<DefaultCompassLocationType> result =
				Arrays.stream(DefaultCompassLocationType.values()).filter(type -> type.inConfig.equalsIgnoreCase(inConfig)).collect(Collectors.toList());
			if (result.isEmpty())
				return SPAWN;
			return result.get(0);
		}
	}
}
