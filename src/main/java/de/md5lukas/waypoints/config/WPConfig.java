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

import de.md5lukas.waypoints.config.displays.DisplayConfig;
import de.md5lukas.waypoints.config.general.GeneralConfig;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.bukkit.Material.matchMaterial;

public class WPConfig {

    private static DisplayConfig displayConfig;
    private static GeneralConfig generalConfig;

    public static DisplayConfig getDisplayConfig() {
        return displayConfig;
    }

    public static GeneralConfig getGeneralConfig() {
        return generalConfig;
    }

    private static WPConfigInventory inventory;

    public static WPConfigInventory inventory() {
        return inventory;
    }

    @SuppressWarnings("ConstantConditions")
    public static void loadConfig(FileConfiguration cfg) {
        generalConfig = new GeneralConfig(cfg.getConfigurationSection("general"));
        displayConfig = new DisplayConfig(cfg.getConfigurationSection("displays"));
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
}
