/*
 *     Waypoints2, A plugin for spigot to add waypoints functionality
 *     Copyright (C) 2019-2020 Lukas Planz
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

package de.md5lukas.waypoints.config;

import de.md5lukas.commons.language.Languages;
import de.md5lukas.waypoints.Messages;
import de.md5lukas.waypoints.config.displays.*;
import de.md5lukas.waypoints.data.waypoint.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.bukkit.Material.matchMaterial;

public class WPConfig {

    private static Map<String, Map<String, String>> worldNameAliases;
    private static WPConfigInventory inventory;
    private static int waypointLimit, folderLimit;
    private static boolean deathWaypointEnabled;

    private static OpenUsingCompass openUsingCompass;

    private static DisplaysActiveWhen displaysActiveWhen;
    private static List<Material> displaysActiveWhenRequiredMaterials;

    private static boolean teleportCountFree;
    private static long teleportCooldown;
    private static long teleportStandStillTime;
    private static Map<String, TeleportConfig> teleportConfigs;

    private static boolean allowDuplicateFolderPrivateNames, allowDuplicateWaypointNamesPrivate, allowDuplicateWaypointNamesPublic, allowDuplicateWaypointNamesPermission;
    private static boolean allowRenamingWaypointsPrivate, allowRenamingWaypointsPublic, allowRenamingWaypointsPermission, allowRenamingFoldersPrivate;

    private static boolean anvilGUICreationEnabled, anvilGUIRenamingEnabled;


    public static WPConfigInventory inventory() {
        return inventory;
    }

    public static String translateWorldName(String world, String language) {
        if (!worldNameAliases.containsKey(language))
            language = Languages.getDefaultLanguage();
        if (!worldNameAliases.containsKey(language))
            return world;
        String translated = worldNameAliases.get(language).get(world);
        return translated == null ? world : translated;
    }

    public static String translateWorldName(String world, CommandSender sender) {
        return translateWorldName(world, Languages.getLanguage(sender));
    }

    public static void checkWorldTranslations() {
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        StringBuilder message = new StringBuilder();
        for (String lang : worldNameAliases.keySet()) {
            boolean firstLang = true;
            for (World w : Bukkit.getWorlds()) {
                if (!worldNameAliases.get(lang).containsKey(w.getName())) {
                    if (firstLang) {
                        firstLang = false;
                        message.append("\n&c").append(lang).append(": &e").append(w.getName());
                    } else {
                        message.append(", ").append(w.getName());
                    }
                }
            }
        }
        String messageString = message.toString();
        console.sendMessage(Messages.CHAT_WARNING_WORLD_TRANSLATIONS_MISSING.getRaw(console) + messageString);
        Bukkit.getOnlinePlayers().stream().map(p -> (Player) p).filter(p ->
                p.hasPermission("waypoints.admin")).forEach(p -> p.sendMessage(Messages.CHAT_WARNING_WORLD_TRANSLATIONS_MISSING.getRaw(p) + messageString));
    }

    public static int getWaypointLimit() {
        return waypointLimit;
    }

    public static int getFolderLimit() {
        return folderLimit;
    }

    public static boolean isDeathWaypointEnabled() {
        return deathWaypointEnabled;
    }

    public static OpenUsingCompass getOpenUsingCompass() {
        return openUsingCompass;
    }

    public static DisplaysActiveWhen getDisplaysActiveWhen() {
        return displaysActiveWhen;
    }

    public static boolean getTeleportCountFree() {
        return teleportCountFree;
    }

    public static long getTeleportCooldown() {
        return teleportCooldown;
    }

    public static long getTeleportStandStillTime() {
        return teleportStandStillTime;
    }

    public static TeleportConfig getTeleportSettings(Class<? extends Waypoint> clazz) {
        return teleportConfigs.get(clazz.getSimpleName());
    }

    public static boolean isVaultRequired() {
        return teleportConfigs.values().stream().anyMatch(settings -> TeleportConfig.TeleportPaymentMethod.VAULT.equals(settings.getPaymentMethod()));
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

    public static boolean isAnvilGUICreationEnabled() {
        return anvilGUICreationEnabled;
    }

    public static boolean isAnvilGUIRenamingEnabled() {
        return anvilGUIRenamingEnabled;
    }

    @SuppressWarnings("ConstantConditions")
    public static void loadConfig(FileConfiguration cfg) {
        //<editor-fold defaultstate="collapsed" desc="General">
        waypointLimit = cfg.getInt("general.waypointLimit");
        folderLimit = cfg.getInt("general.folderLimit");

        deathWaypointEnabled = cfg.getBoolean("general.deathWaypointEnabled");

        openUsingCompass = OpenUsingCompass.getFromConfig(cfg.getString("general.openUsingCompass"));

        displaysActiveWhen = DisplaysActiveWhen.getFromConfig(cfg.getString("general.displaysActiveWhen.enabled"));

        displaysActiveWhenRequiredMaterials = cfg.getStringList("general.displaysActiveWhen.requiredItems").stream().map(Material::matchMaterial)
                .collect(Collectors.toList());

        teleportCountFree = cfg.getBoolean("general.teleport.countFreeTeleportations");
        TimeUnit teleportCdTU = TimeUnit.valueOf(cfg.getString("general.teleport.condition.cooldown.timeUnit").toUpperCase());
        teleportCooldown = teleportCdTU.toMillis(cfg.getLong("general.teleport.condition.cooldown.value"));
        teleportStandStillTime = cfg.getLong("general.teleport.condition.standStillTime");
        teleportConfigs = new HashMap<>();
        teleportConfigs.put(DeathWaypoint.class.getSimpleName(), new TeleportConfig(cfg.getConfigurationSection("general.teleport.waypoint.death")));
        teleportConfigs.put(PrivateWaypoint.class.getSimpleName(), new TeleportConfig(cfg.getConfigurationSection("general.teleport.waypoint.private")));
        teleportConfigs.put(PublicWaypoint.class.getSimpleName(), new TeleportConfig(cfg.getConfigurationSection("general.teleport.waypoint.public")));
        teleportConfigs
                .put(PermissionWaypoint.class.getSimpleName(), new TeleportConfig(cfg.getConfigurationSection("general.teleport.waypoint.permission")));

        allowDuplicateFolderPrivateNames = cfg.getBoolean("general.allowDuplicatePrivateFolderNames");
        allowDuplicateWaypointNamesPrivate = cfg.getBoolean("general.allowDuplicateWaypointNames.private");
        allowDuplicateWaypointNamesPublic = cfg.getBoolean("general.allowDuplicateWaypointNames.public");
        allowDuplicateWaypointNamesPermission = cfg.getBoolean("general.allowDuplicateWaypointNames.permission");

        allowRenamingFoldersPrivate = cfg.getBoolean("general.allowRenamingPrivateFolders");
        allowRenamingWaypointsPrivate = cfg.getBoolean("general.allowRenamingWaypoints.private");
        allowRenamingWaypointsPublic = cfg.getBoolean("general.allowRenamingWaypoints.public");
        allowRenamingWaypointsPermission = cfg.getBoolean("general.allowRenamingWaypoints.permission");

        anvilGUICreationEnabled = cfg.getBoolean("general.anvilGUI.creation");
        anvilGUIRenamingEnabled = cfg.getBoolean("general.anvilGUI.renaming");

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
        displayConfigs = new DisplayConfigs(cfg.getConfigurationSection("displays"));
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
                cfg.getStringList("inventory.customItem.filter.list").stream().map(Material::matchMaterial).filter(Objects::nonNull)
                        .collect(Collectors.toList());

        inventory.overviewBackgroundItem = matchMaterial(cfg.getString("inventory.overview.backgroundItem"));
        inventory.overviewCycleSortItem = matchMaterial(cfg.getString("inventory.overview.cycleSortItem"));
        inventory.overviewDeselectItem = matchMaterial(cfg.getString("inventory.overview.deselectItem"));
        inventory.overviewToggleGlobalsItem = matchMaterial(cfg.getString("inventory.overview.toggleGlobalsItem"));
        inventory.overviewSetWaypointItem = matchMaterial(cfg.getString("inventory.overview.setWaypointItem"));
        inventory.overviewCreateFolderItem = matchMaterial(cfg.getString("inventory.overview.createFolderItem"));

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
        inventory.waypointPrivateSelectBeaconColorItem = matchMaterial(cfg.getString("inventory.waypoints.private.selectBeaconColorItem"));

        inventory.waypointPublicItem = matchMaterial(cfg.getString("inventory.waypoints.public.item"));
        inventory.waypointPublicBackgroundItem = matchMaterial(cfg.getString("inventory.waypoints.public.backgroundItem"));
        inventory.waypointPublicSelectItem = matchMaterial(cfg.getString("inventory.waypoints.public.selectItem"));
        inventory.waypointPublicDeleteItem = matchMaterial(cfg.getString("inventory.waypoints.public.deleteItem"));
        inventory.waypointPublicRenameItem = matchMaterial(cfg.getString("inventory.waypoints.public.renameItem"));
        inventory.waypointPublicTeleportItem = matchMaterial(cfg.getString("inventory.waypoints.public.teleportItem"));
        inventory.waypointPublicSelectBeaconColorItem = matchMaterial(cfg.getString("inventory.waypoints.public.selectBeaconColorItem"));

        inventory.waypointPermissionItem = matchMaterial(cfg.getString("inventory.waypoints.permission.item"));
        inventory.waypointPermissionBackgroundItem = matchMaterial(cfg.getString("inventory.waypoints.permission.backgroundItem"));
        inventory.waypointPermissionSelectItem = matchMaterial(cfg.getString("inventory.waypoints.permission.selectItem"));
        inventory.waypointPermissionDeleteItem = matchMaterial(cfg.getString("inventory.waypoints.permission.deleteItem"));
        inventory.waypointPermissionRenameItem = matchMaterial(cfg.getString("inventory.waypoints.permission.renameItem"));
        inventory.waypointPermissionTeleportItem = matchMaterial(cfg.getString("inventory.waypoints.permission.teleportItem"));
        inventory.waypointPermissionSelectBeaconColorItem = matchMaterial(cfg.getString("inventory.waypoints.permission.selectBeaconColorItem"));

        inventory.selectBeaconColorBackgroundItem = matchMaterial(cfg.getString("inventory.selectBeaconColor.backgroundItem"));
        inventory.selectBeaconColorPreviousItem = matchMaterial(cfg.getString("inventory.selectBeaconColor.previousItem"));
        inventory.selectBeaconColorNextItem = matchMaterial(cfg.getString("inventory.selectBeaconColor.nextItem"));

        inventory.selectWaypointTypeBackgroundItem = matchMaterial(cfg.getString("inventory.selectWaypointType.backgroundItem"));
        inventory.selectWaypointTypeTitleItem = matchMaterial(cfg.getString("inventory.selectWaypointType.titleItem"));
        inventory.selectWaypointTypePrivateItem = matchMaterial(cfg.getString("inventory.selectWaypointType.privateItem"));
        inventory.selectWaypointTypePublicItem = matchMaterial(cfg.getString("inventory.selectWaypointType.publicItem"));
        inventory.selectWaypointTypePermissionItem = matchMaterial(cfg.getString("inventory.selectWaypointType.permissionItem"));

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

    private static DisplayConfigs displayConfigs;

    public static DisplayConfigs getDisplayConfigs() {
        return displayConfigs;
    }

    public final static class DisplayConfigs {

        private final ActionBarConfig actionBarConfig;
        private final BeaconConfig beaconConfig;
        private final BlinkingBlockConfig blinkingBlockConfig;
        private final CompassConfig compassConfig;
        private final ParticleConfig particleConfig;
        private final WrongWorldConfig wrongWorldConfig;

        public DisplayConfigs(ConfigurationSection cfg) {
            this.actionBarConfig = new ActionBarConfig();
            this.beaconConfig = new BeaconConfig();
            this.blinkingBlockConfig = new BlinkingBlockConfig();
            this.compassConfig = new CompassConfig();
            this.particleConfig = new ParticleConfig();
            this.wrongWorldConfig = new WrongWorldConfig();
            load(cfg);
        }

        @SuppressWarnings("ConstantConditions")
        public void load(ConfigurationSection cfg) {
            this.actionBarConfig.load(cfg.getConfigurationSection("actionBar"));
            this.beaconConfig.load(cfg.getConfigurationSection("beacon"));
            this.blinkingBlockConfig.load(cfg.getConfigurationSection("blinkingBlock"));
            this.compassConfig.load(cfg.getConfigurationSection("compass"));
            this.particleConfig.load(cfg.getConfigurationSection("particles"));
            this.wrongWorldConfig.load(cfg.getConfigurationSection("wrongWorld"));
        }

        public ActionBarConfig getActionBarConfig() {
            return actionBarConfig;
        }

        public BeaconConfig getBeaconConfig() {
            return beaconConfig;
        }

        public BlinkingBlockConfig getBlinkingBlockConfig() {
            return blinkingBlockConfig;
        }

        public CompassConfig getCompassConfig() {
            return compassConfig;
        }

        public ParticleConfig getParticleConfig() {
            return particleConfig;
        }

        public WrongWorldConfig getWrongWorldConfig() {
            return wrongWorldConfig;
        }
    }

    public final static class WPConfigInventory {

        private int maxDescriptionLineLength;

        private WPConfigInventory() {
        }

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
        private Material overviewSetWaypointItem;
        private Material overviewCreateFolderItem;

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
        private Material waypointPrivateSelectBeaconColorItem;

        private Material waypointPublicItem;
        private Material waypointPublicBackgroundItem;
        private Material waypointPublicSelectItem;
        private Material waypointPublicDeleteItem;
        private Material waypointPublicRenameItem;
        private Material waypointPublicTeleportItem;
        private Material waypointPublicSelectBeaconColorItem;

        private Material waypointPermissionItem;
        private Material waypointPermissionBackgroundItem;
        private Material waypointPermissionSelectItem;
        private Material waypointPermissionDeleteItem;
        private Material waypointPermissionRenameItem;
        private Material waypointPermissionTeleportItem;
        private Material waypointPermissionSelectBeaconColorItem;

        private Material selectBeaconColorBackgroundItem;
        private Material selectBeaconColorPreviousItem;
        private Material selectBeaconColorNextItem;

        private Material selectWaypointTypeBackgroundItem;
        private Material selectWaypointTypeTitleItem;
        private Material selectWaypointTypePrivateItem;
        private Material selectWaypointTypePublicItem;
        private Material selectWaypointTypePermissionItem;

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

        public Material getOverviewSetWaypointItem() {
            return overviewSetWaypointItem;
        }

        public Material getOverviewCreateFolderItem() {
            return overviewCreateFolderItem;
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

        public Material getWaypointPrivateSelectBeaconColorItem() {
            return waypointPrivateSelectBeaconColorItem;
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

        public Material getWaypointPublicSelectBeaconColorItem() {
            return waypointPublicSelectBeaconColorItem;
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

        public Material getWaypointPermissionSelectBeaconColorItem() {
            return waypointPermissionSelectBeaconColorItem;
        }

        public Material getSelectBeaconColorBackgroundItem() {
            return selectBeaconColorBackgroundItem;
        }

        public Material getSelectBeaconColorNextItem() {
            return selectBeaconColorNextItem;
        }

        public Material getSelectBeaconColorPreviousItem() {
            return selectBeaconColorPreviousItem;
        }

        public Material getSelectWaypointTypeBackgroundItem() {
            return selectWaypointTypeBackgroundItem;
        }

        public Material getSelectWaypointTypeTitleItem() {
            return selectWaypointTypeTitleItem;
        }

        public Material getSelectWaypointTypePrivateItem() {
            return selectWaypointTypePrivateItem;
        }

        public Material getSelectWaypointTypePublicItem() {
            return selectWaypointTypePublicItem;
        }

        public Material getSelectWaypointTypePermissionItem() {
            return selectWaypointTypePermissionItem;
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

    public enum OpenUsingCompass {
        LEFT("left"), RIGHT("right"), FALSE("false");

        private String inConfig;

        OpenUsingCompass(String inConfig) {
            this.inConfig = inConfig;
        }

        public boolean isValidAction(Action action) {
            switch (action) {
                case LEFT_CLICK_AIR:
                case LEFT_CLICK_BLOCK:
                    return this == LEFT;
                case RIGHT_CLICK_AIR:
                case RIGHT_CLICK_BLOCK:
                    return this == RIGHT;
                case PHYSICAL:
                default:
                    return false;
            }
        }

        public static OpenUsingCompass getFromConfig(String inConfig) {
            return Arrays.stream(values()).filter(variant -> variant.inConfig.equalsIgnoreCase(inConfig)).findFirst()
                    .orElse(FALSE); // TODO replace with enum matcher when new md5-commons is in place
        }
    }

    public enum DisplaysActiveWhen {
        ITEM_IN_INVENTORY("itemInInventory"), ITEM_IN_HOTBAR("itemInHotbar"), ITEM_IN_HAND("itemInHand"), FALSE("false");

        private String inConfig;

        DisplaysActiveWhen(String inConfig) {
            this.inConfig = inConfig;
        }

        public boolean testPlayer(Player player) {
            switch (this) {
                case ITEM_IN_INVENTORY:
                    return Arrays.stream(player.getInventory().getContents()).filter(Objects::nonNull).anyMatch(stack -> {
                        for (Material material : WPConfig.displaysActiveWhenRequiredMaterials) {
                            if (material == stack.getType()) {
                                return true;
                            }
                        }
                        return false;
                    });
                case ITEM_IN_HOTBAR:
                    for (int i = 0; i < 9; i++) {
                        for (Material material : WPConfig.displaysActiveWhenRequiredMaterials) {
                            ItemStack stack = player.getInventory().getItem(i);
                            if (stack != null && material == stack.getType()) {
                                return true;
                            }
                        }
                    }
                    return false;
                case ITEM_IN_HAND:
                    PlayerInventory inventory = player.getInventory();
                    Material mainHand = inventory.getItemInMainHand().getType();
                    Material offHand = inventory.getItemInOffHand().getType();
                    for (Material material : WPConfig.displaysActiveWhenRequiredMaterials) {
                        if (material == mainHand || material == offHand) {
                            return true;
                        }
                    }
                    return false;
                default:
                    return true;
            }
        }

        public static DisplaysActiveWhen getFromConfig(String inConfig) {
            return Arrays.stream(values()).filter(variant -> variant.inConfig.equalsIgnoreCase(inConfig)).findFirst()
                    .orElse(FALSE); // TODO replace with enum matcher when new md5-commons is in place
        }
    }
}
