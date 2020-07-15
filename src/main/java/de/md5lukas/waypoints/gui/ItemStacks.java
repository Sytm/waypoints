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

package de.md5lukas.waypoints.gui;

import de.md5lukas.commons.inventory.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static de.md5lukas.waypoints.Messages.*;
import static de.md5lukas.waypoints.config.WPConfig.getInventoryConfig;

class ItemStacks {

    static ItemStack getPreviousItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getPreviousItem()).name(INVENTORY_GENERAL_PREVIOUS_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_GENERAL_PREVIOUS_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getNextItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getNextItem()).name(INVENTORY_GENERAL_NEXT_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_GENERAL_NEXT_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getBackItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getBackItem()).name(INVENTORY_GENERAL_BACK_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_GENERAL_BACK_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getOverviewBackgroundItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getDefaultOverviewMenuConfig().getBackgroundItem())
                .name(INVENTORY_OVERVIEW_BACKGROUND_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_OVERVIEW_BACKGROUND_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getOverviewDeselectItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getDefaultOverviewMenuConfig().getDeselectItem()).name(INVENTORY_OVERVIEW_DESELECT_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_OVERVIEW_DESELECT_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getOverviewToggleGlobalsShownItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getDefaultOverviewMenuConfig().getToggleGlobalsItem())
                .name(INVENTORY_OVERVIEW_TOGGLE_GLOBALS_SHOWN_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_OVERVIEW_TOGGLE_GLOBALS_SHOWN_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getOverviewToggleGlobalsHiddenItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getDefaultOverviewMenuConfig().getToggleGlobalsItem())
                .name(INVENTORY_OVERVIEW_TOGGLE_GLOBALS_HIDDEN_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_OVERVIEW_TOGGLE_GLOBALS_HIDDEN_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getOverviewSetWaypointItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getDefaultOverviewMenuConfig().getSetWaypointItem())
                .name(INVENTORY_OVERVIEW_SET_WAYPOINT_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_OVERVIEW_SET_WAYPOINT_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getOverviewCreateFolderItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getDefaultOverviewMenuConfig().getCreateFolderItem())
                .name(INVENTORY_OVERVIEW_CREATE_FOLDER_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_OVERVIEW_CREATE_FOLDER_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointDeathBackgroundItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getDeathWaypointMenuConfig().getBackgroundItem())
                .name(INVENTORY_WAYPOINT_DEATH_BACKGROUND_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_DEATH_BACKGROUND_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointDeathSelectItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getDeathWaypointMenuConfig().getSelectItem()).name(INVENTORY_WAYPOINT_DEATH_SELECT_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_DEATH_SELECT_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointDeathTeleportItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getDeathWaypointMenuConfig().getTeleportItem())
                .name(INVENTORY_WAYPOINT_DEATH_TELEPORT_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_DEATH_TELEPORT_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointDeathTeleportXpPointsItem(Player p, long points) {
        return new ItemBuilder(getInventoryConfig().getDeathWaypointMenuConfig().getTeleportItem())
                .name(INVENTORY_WAYPOINT_DEATH_TELEPORT_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_DEATH_TELEPORT_PAYMENT_XP_POINTS_DESCRIPTION.getRaw(p).replace("%points%", Long.toString(points)),
                        getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointDeathTeleportXpLevelsItem(Player p, long levels) {
        return new ItemBuilder(getInventoryConfig().getDeathWaypointMenuConfig().getTeleportItem())
                .name(INVENTORY_WAYPOINT_DEATH_TELEPORT_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_DEATH_TELEPORT_PAYMENT_XP_LEVELS_DESCRIPTION.getRaw(p).replace("%levels%", Long.toString(levels)),
                        getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointDeathTeleportVaultItem(Player p, long money) {
        return new ItemBuilder(getInventoryConfig().getDeathWaypointMenuConfig().getTeleportItem())
                .name(INVENTORY_WAYPOINT_DEATH_TELEPORT_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_DEATH_TELEPORT_PAYMENT_VAULT_DESCRIPTION.getRaw(p).replace("%money%", Long.toString(money)),
                        getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPrivateBackgroundItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getPrivateWaypointMenuConfig().getBackgroundItem())
                .name(INVENTORY_WAYPOINT_PRIVATE_BACKGROUND_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PRIVATE_BACKGROUND_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPrivateSelectItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getPrivateWaypointMenuConfig().getSelectItem())
                .name(INVENTORY_WAYPOINT_PRIVATE_SELECT_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PRIVATE_SELECT_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPrivateDeleteItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getPrivateWaypointMenuConfig().getDeleteItem())
                .name(INVENTORY_WAYPOINT_PRIVATE_DELETE_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PRIVATE_DELETE_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPrivateRenameItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getPrivateWaypointMenuConfig().getRenameItem())
                .name(INVENTORY_WAYPOINT_PRIVATE_RENAME_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PRIVATE_RENAME_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPrivateMoveToFolderItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getPrivateWaypointMenuConfig().getMoveToFolderItem())
                .name(INVENTORY_WAYPOINT_PRIVATE_MOVE_TO_FOLDER_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PRIVATE_MOVE_TO_FOLDER_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPrivateTeleportItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getPrivateWaypointMenuConfig().getTeleportItem())
                .name(INVENTORY_WAYPOINT_PRIVATE_TELEPORT_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PRIVATE_TELEPORT_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPrivateTeleportXpPointsItem(Player p, long points) {
        return new ItemBuilder(getInventoryConfig().getPrivateWaypointMenuConfig().getTeleportItem())
                .name(INVENTORY_WAYPOINT_PRIVATE_TELEPORT_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PRIVATE_TELEPORT_PAYMENT_XP_POINTS_DESCRIPTION.getRaw(p).replace("%points%", Long.toString(points)),
                        getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPrivateTeleportXpLevelsItem(Player p, long levels) {
        return new ItemBuilder(getInventoryConfig().getPrivateWaypointMenuConfig().getTeleportItem())
                .name(INVENTORY_WAYPOINT_PRIVATE_TELEPORT_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PRIVATE_TELEPORT_PAYMENT_XP_LEVELS_DESCRIPTION.getRaw(p).replace("%levels%", Long.toString(levels)),
                        getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPrivateTeleportVaultItem(Player p, long money) {
        return new ItemBuilder(getInventoryConfig().getPrivateWaypointMenuConfig().getTeleportItem())
                .name(INVENTORY_WAYPOINT_PRIVATE_TELEPORT_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PRIVATE_TELEPORT_PAYMENT_VAULT_DESCRIPTION.getRaw(p).replace("%money%", Long.toString(money)),
                        getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPrivateSelectBeaconColor(Player p) {
        return new ItemBuilder(getInventoryConfig().getPrivateWaypointMenuConfig().getSelectBeaconColorItem())
                .name(INVENTORY_WAYPOINT_PRIVATE_SELECT_BEACON_COLOR_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PRIVATE_SELECT_BEACON_COLOR_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }


    static ItemStack getWaypointPublicBackgroundItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getPublicWaypointMenuConfig().getBackgroundItem())
                .name(INVENTORY_WAYPOINT_PUBLIC_BACKGROUND_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PUBLIC_BACKGROUND_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPublicSelectItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getPublicWaypointMenuConfig().getSelectItem()).name(INVENTORY_WAYPOINT_PUBLIC_SELECT_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PUBLIC_SELECT_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPublicDeleteItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getPublicWaypointMenuConfig().getDeleteItem()).name(INVENTORY_WAYPOINT_PUBLIC_DELETE_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PUBLIC_DELETE_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPublicRenameItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getPublicWaypointMenuConfig().getRenameItem()).name(INVENTORY_WAYPOINT_PUBLIC_RENAME_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PUBLIC_RENAME_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPublicTeleportItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getPublicWaypointMenuConfig().getTeleportItem())
                .name(INVENTORY_WAYPOINT_PUBLIC_TELEPORT_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PUBLIC_TELEPORT_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPublicTeleportXpPointsItem(Player p, long points) {
        return new ItemBuilder(getInventoryConfig().getPublicWaypointMenuConfig().getTeleportItem())
                .name(INVENTORY_WAYPOINT_PUBLIC_TELEPORT_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PUBLIC_TELEPORT_PAYMENT_XP_POINTS_DESCRIPTION.getRaw(p).replace("%points%", Long.toString(points)),
                        getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPublicTeleportXpLevelsItem(Player p, long levels) {
        return new ItemBuilder(getInventoryConfig().getPublicWaypointMenuConfig().getTeleportItem())
                .name(INVENTORY_WAYPOINT_PUBLIC_TELEPORT_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PUBLIC_TELEPORT_PAYMENT_XP_LEVELS_DESCRIPTION.getRaw(p).replace("%levels%", Long.toString(levels)),
                        getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPublicTeleportVaultItem(Player p, long money) {
        return new ItemBuilder(getInventoryConfig().getPublicWaypointMenuConfig().getTeleportItem())
                .name(INVENTORY_WAYPOINT_PUBLIC_TELEPORT_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PUBLIC_TELEPORT_PAYMENT_VAULT_DESCRIPTION.getRaw(p).replace("%money%", Long.toString(money)),
                        getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPublicSelectBeaconColor(Player p) {
        return new ItemBuilder(getInventoryConfig().getPublicWaypointMenuConfig().getSelectBeaconColorItem())
                .name(INVENTORY_WAYPOINT_PUBLIC_SELECT_BEACON_COLOR_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PUBLIC_SELECT_BEACON_COLOR_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }


    static ItemStack getWaypointPermissionBackgroundItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getPermissionWaypointMenuConfig().getBackgroundItem())
                .name(INVENTORY_WAYPOINT_PERMISSION_BACKGROUND_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PERMISSION_BACKGROUND_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPermissionSelectItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getPermissionWaypointMenuConfig().getSelectItem())
                .name(INVENTORY_WAYPOINT_PERMISSION_SELECT_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PERMISSION_SELECT_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPermissionDeleteItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getPermissionWaypointMenuConfig().getDeleteItem())
                .name(INVENTORY_WAYPOINT_PERMISSION_DELETE_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PERMISSION_DELETE_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPermissionRenameItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getPermissionWaypointMenuConfig().getRenameItem())
                .name(INVENTORY_WAYPOINT_PERMISSION_RENAME_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PERMISSION_RENAME_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPermissionTeleportItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getPermissionWaypointMenuConfig().getTeleportItem())
                .name(INVENTORY_WAYPOINT_PERMISSION_TELEPORT_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PERMISSION_TELEPORT_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPermissionTeleportXpPointsItem(Player p, long points) {
        return new ItemBuilder(getInventoryConfig().getPermissionWaypointMenuConfig().getTeleportItem())
                .name(INVENTORY_WAYPOINT_PERMISSION_TELEPORT_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PERMISSION_TELEPORT_PAYMENT_XP_POINTS_DESCRIPTION.getRaw(p).replace("%points%", Long.toString(points)),
                        getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPermissionTeleportXpLevelsItem(Player p, long levels) {
        return new ItemBuilder(getInventoryConfig().getPermissionWaypointMenuConfig().getTeleportItem())
                .name(INVENTORY_WAYPOINT_PERMISSION_TELEPORT_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PERMISSION_TELEPORT_PAYMENT_XP_LEVELS_DESCRIPTION.getRaw(p).replace("%levels%", Long.toString(levels)),
                        getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPermissionTeleportVaultItem(Player p, long money) {
        return new ItemBuilder(getInventoryConfig().getPermissionWaypointMenuConfig().getTeleportItem())
                .name(INVENTORY_WAYPOINT_PERMISSION_TELEPORT_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PERMISSION_TELEPORT_PAYMENT_VAULT_DESCRIPTION.getRaw(p).replace("%money%", Long.toString(money)),
                        getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getWaypointPermissionSelectBeaconColor(Player p) {
        return new ItemBuilder(getInventoryConfig().getPermissionWaypointMenuConfig().getSelectBeaconColorItem())
                .name(INVENTORY_WAYPOINT_PERMISSION_SELECT_BEACON_COLOR_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_WAYPOINT_PERMISSION_SELECT_BEACON_COLOR_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }


    static ItemStack getSelectWaypointTypeBackgroundItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getSelectWaypointTypeMenuConfig().getBackgroundItem())
                .name(INVENTORY_SELECT_WAYPOINT_TYPE_BACKGROUND_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_SELECT_WAYPOINT_TYPE_BACKGROUND_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getSelectWaypointTypeTitleItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getSelectWaypointTypeMenuConfig().getTitleItem())
                .name(INVENTORY_SELECT_WAYPOINT_TYPE_TITLE_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_SELECT_WAYPOINT_TYPE_TITLE_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getSelectWaypointTypePrivateItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getSelectWaypointTypeMenuConfig().getPrivateItem())
                .name(INVENTORY_SELECT_WAYPOINT_TYPE_PRIVATE_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_SELECT_WAYPOINT_TYPE_PRIVATE_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getSelectWaypointTypePublicItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getSelectWaypointTypeMenuConfig().getPublicItem())
                .name(INVENTORY_SELECT_WAYPOINT_TYPE_PUBLIC_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_SELECT_WAYPOINT_TYPE_PUBLIC_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getSelectWaypointTypePermissionItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getSelectWaypointTypeMenuConfig().getPermissionItem())
                .name(INVENTORY_SELECT_WAYPOINT_TYPE_PERMISSION_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_SELECT_WAYPOINT_TYPE_PERMISSION_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getSelectFolderBackgroundItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getSelectFolderMenuConfig().getBackgroundItem())
                .name(INVENTORY_SELECT_FOLDER_BACKGROUND_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_SELECT_FOLDER_BACKGROUND_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getSelectFolderNoFolderItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getSelectFolderMenuConfig().getNoFolderItem())
                .name(INVENTORY_SELECT_FOLDER_NO_FOLDER_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_SELECT_FOLDER_NO_FOLDER_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getSelectBeaconColorBackgroundItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getSelectBeaconColorMenuConfig().getBackgroundItem())
                .name(INVENTORY_SELECT_BEACON_COLOR_BACKGROUND_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_SELECT_BEACON_COLOR_BACKGROUND_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getSelectBeaconColorPreviousItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getPreviousItem()).name(INVENTORY_SELECT_BEACON_COLOR_PREVIOUS_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_SELECT_BEACON_COLOR_PREVIOUS_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getSelectBeaconColorNextItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getNextItem()).name(INVENTORY_SELECT_BEACON_COLOR_NEXT_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_SELECT_BEACON_COLOR_NEXT_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getFolderPrivateBackgroundItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getPrivateFolderMenuConfig().getBackgroundItem())
                .name(INVENTORY_FOLDER_PRIVATE_BACKGROUND_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_FOLDER_PRIVATE_BACKGROUND_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getFolderPrivateDeleteItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getPrivateFolderMenuConfig().getDeleteItem()).name(INVENTORY_FOLDER_PRIVATE_DELETE_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_FOLDER_PRIVATE_DELETE_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getFolderPrivateRenameItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getPrivateFolderMenuConfig().getRenameItem()).name(INVENTORY_FOLDER_PRIVATE_RENAME_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_FOLDER_PRIVATE_RENAME_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getFolderPublicBackgroundItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getPrivateFolderMenuConfig().getBackgroundItem())
                .name(INVENTORY_FOLDER_PUBLIC_BACKGROUND_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_FOLDER_PUBLIC_BACKGROUND_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }

    static ItemStack getFolderPermissionBackgroundItem(Player p) {
        return new ItemBuilder(getInventoryConfig().getPrivateFolderMenuConfig().getBackgroundItem())
                .name(INVENTORY_FOLDER_PERMISSION_BACKGROUND_DISPLAY_NAME.getRaw(p))
                .lore(INVENTORY_FOLDER_PERMISSION_BACKGROUND_DESCRIPTION.getRaw(p), getInventoryConfig().getMaxDescriptionLineLength()).make();
    }
}
