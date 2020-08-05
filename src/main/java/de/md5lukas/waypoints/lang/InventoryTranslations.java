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

package de.md5lukas.waypoints.lang;

import de.md5lukas.i18n.language.LanguageStorage;
import de.md5lukas.i18n.translations.ItemTranslation;
import de.md5lukas.i18n.translations.Translation;
import de.md5lukas.waypoints.config.WPConfig;
import de.md5lukas.waypoints.config.inventory.InventoryConfig;
import de.md5lukas.waypoints.display.BlockColor;

public class InventoryTranslations {

    InventoryTranslations(LanguageStorage ls) {
        InventoryConfig ic = WPConfig.getInventoryConfig();

        TITLE_OWN = new Translation(ls, "inventory.title.own");
        TITLE_OTHER = new Translation(ls, "inventory.title.other");
        CYCLE_SORT_ACTIVE = new Translation(ls, "inventory.cycleSort.active"); // %name%
        CYCLE_SORT_INACTIVE = new Translation(ls, "inventory.cycleSort.inactive"); // %name%
        CYCLE_SORT_MODE_NAME_ASC = new Translation(ls, "inventory.cycleSort.mode.name.ascending");
        CYCLE_SORT_MODE_NAME_DESC = new Translation(ls, "inventory.cycleSort.mode.name.descending");
        CYCLE_SORT_MODE_CREATED_ASC = new Translation(ls, "inventory.cycleSort.mode.createdAt.ascending");
        CYCLE_SORT_MODE_CREATED_DESC = new Translation(ls, "inventory.cycleSort.mode.createdAt.descending");
        CYCLE_SORT_MODE_TYPE = new Translation(ls, "inventory.cycleSort.mode.type");
        WAYPOINT_DISTANCE_OTHER_WORLD = new Translation(ls, "inventory.waypoint.distance.otherWorld");
        ANVIL_GUI_ENTER_NAME_HERE = new Translation(ls, "inventory.anvilGUI.enterNameHere");
        ANVIL_GUI_ENTER_PERMISSION_HERE = new Translation(ls, "inventory.anvilGUI.enterPermissionHere");


        GENERAL_PREVIOUS = new ItemTranslation(ls, ic::getPreviousItem, "inventory.general.previous");
        GENERAL_NEXT = new ItemTranslation(ls, ic::getNextItem, "inventory.general.next");
        GENERAL_BACK = new ItemTranslation(ls, ic::getBackItem, "inventory.general.back");

        CYCLE_SORT = new ItemTranslation(ls, () -> ic.getDefaultOverviewMenuConfig().getCycleSortItem(), "inventory.cycleSort");


        OVERVIEW_BACKGROUND = new ItemTranslation(ls, () -> ic.getDefaultOverviewMenuConfig().getBackgroundItem(), "inventory.overview.background");
        OVERVIEW_DESELECT = new ItemTranslation(ls, () -> ic.getDefaultOverviewMenuConfig().getDeselectItem(), "inventory.overview.deselect");
        OVERVIEW_TOGGLE_GLOBALS_SHOWN = new ItemTranslation(ls, () -> ic.getDefaultOverviewMenuConfig().getToggleGlobalsItem(),
                "inventory.overview.toggleGlobals.shown");
        OVERVIEW_TOGGLE_GLOBALS_HIDDEN = new ItemTranslation(ls, () -> ic.getDefaultOverviewMenuConfig().getToggleGlobalsItem(),
                "inventory.overview.toggleGlobals.hidden");
        OVERVIEW_SET_WAYPOINT = new ItemTranslation(ls, () -> ic.getDefaultOverviewMenuConfig().getSetWaypointItem(), "inventory.overview.setWaypoint");
        OVERVIEW_CREATE_FOLDER = new ItemTranslation(ls, () -> ic.getDefaultOverviewMenuConfig().getCreateFolderItem(), "inventory.overview.createFolder");

        SELECT_WAYPOINT_TYPE_BACKGROUND = new ItemTranslation(ls, () -> ic.getSelectWaypointTypeMenuConfig().getBackgroundItem(),
                "inventory.selectWaypointType.background");
        SELECT_WAYPOINT_TYPE_TITLE = new ItemTranslation(ls, () -> ic.getSelectWaypointTypeMenuConfig().getTitleItem(), "inventory.selectWaypointType.title");
        SELECT_WAYPOINT_TYPE_PRIVATE = new ItemTranslation(ls, () -> ic.getSelectWaypointTypeMenuConfig().getPrivateItem(),
                "inventory.selectWaypointType.private");
        SELECT_WAYPOINT_TYPE_PUBLIC = new ItemTranslation(ls, () -> ic.getSelectWaypointTypeMenuConfig().getPublicItem(),
                "inventory.selectWaypointType.public");
        SELECT_WAYPOINT_TYPE_PERMISSION = new ItemTranslation(ls, () -> ic.getSelectWaypointTypeMenuConfig().getPermissionItem(),
                "inventory.selectWaypointType.permission");


        WAYPOINT_DEATH = new ItemTranslation(ls, () -> ic.getDeathWaypointMenuConfig().getItem(), "inventory.waypoint.death");
        WAYPOINT_DEATH_BACKGROUND = new ItemTranslation(ls, () -> ic.getDeathWaypointMenuConfig().getBackgroundItem(), "inventory.waypoint.death.background");
        WAYPOINT_DEATH_SELECT = new ItemTranslation(ls, () -> ic.getDeathWaypointMenuConfig().getSelectItem(), "inventory.waypoint.death.select");
        WAYPOINT_DEATH_TELEPORT = new ItemTranslation(ls, () -> ic.getDeathWaypointMenuConfig().getTeleportItem(), "inventory.waypoint.death.teleport");
        WAYPOINT_DEATH_TELEPORT_XP_POINTS = new ItemTranslation(ls, () -> ic.getDeathWaypointMenuConfig().getTeleportItem(),
                "inventory.waypoint.death.teleport.displayName",
                "inventory.waypoint.death.teleport.payment.xpPoints.description");
        WAYPOINT_DEATH_TELEPORT_XP_LEVELS = new ItemTranslation(ls, () -> ic.getDeathWaypointMenuConfig().getTeleportItem(),
                "inventory.waypoint.death.teleport.displayName",
                "inventory.waypoint.death.teleport.payment.xpLevels.description");
        WAYPOINT_DEATH_TELEPORT_VAULT = new ItemTranslation(ls, () -> ic.getDeathWaypointMenuConfig().getTeleportItem(),
                "inventory.waypoint.death.teleport.displayName",
                "inventory.waypoint.death.teleport.payment.vault.description");

        WAYPOINT_PRIVATE = new ItemTranslation(ls, () -> ic.getPrivateWaypointMenuConfig().getDefaultItem(), "inventory.waypoint.private"); // %name%
        WAYPOINT_PRIVATE_BACKGROUND = new ItemTranslation(ls, () -> ic.getPrivateWaypointMenuConfig().getBackgroundItem(),
                "inventory.waypoint.private.background");
        WAYPOINT_PRIVATE_SELECT = new ItemTranslation(ls, () -> ic.getPrivateWaypointMenuConfig().getSelectItem(), "inventory.waypoint.private.select");
        WAYPOINT_PRIVATE_DELETE = new ItemTranslation(ls, () -> ic.getPrivateWaypointMenuConfig().getDeleteItem(), "inventory.waypoint.private.delete");
        WAYPOINT_PRIVATE_RENAME = new ItemTranslation(ls, () -> ic.getPrivateWaypointMenuConfig().getRenameItem(), "inventory.waypoint.private.rename");
        WAYPOINT_PRIVATE_MOVE_TO_FOLDER = new ItemTranslation(ls, () -> ic.getPrivateWaypointMenuConfig().getMoveToFolderItem(),
                "inventory.waypoint.private.moveToFolder");
        WAYPOINT_PRIVATE_TELEPORT = new ItemTranslation(ls, () -> ic.getPrivateWaypointMenuConfig().getTeleportItem(), "inventory.waypoint.private.teleport");
        WAYPOINT_PRIVATE_TELEPORT_XP_POINTS = new ItemTranslation(ls, () -> ic.getPrivateWaypointMenuConfig().getTeleportItem(),
                "inventory.waypoint.private.teleport.displayName",
                "inventory.waypoint.private.teleport.payment.xpPoints.description");
        WAYPOINT_PRIVATE_TELEPORT_XP_LEVELS = new ItemTranslation(ls, () -> ic.getPrivateWaypointMenuConfig().getTeleportItem(),
                "inventory.waypoint.private.teleport.displayName",
                "inventory.waypoint.private.teleport.payment.xpLevels.description");
        WAYPOINT_PRIVATE_TELEPORT_VAULT = new ItemTranslation(ls, () -> ic.getPrivateWaypointMenuConfig().getTeleportItem(),
                "inventory.waypoint.private.teleport.displayName",
                "inventory.waypoint.private.teleport.payment.vault.description");
        WAYPOINT_PRIVATE_SELECT_BEACON_COLOR = new ItemTranslation(ls, () -> ic.getPrivateWaypointMenuConfig().getSelectBeaconColorItem(),
                "inventory.waypoint.private.selectBeaconColor");


        WAYPOINT_PUBLIC = new ItemTranslation(ls, () -> ic.getPublicWaypointMenuConfig().getItem(), "inventory.waypoint.public"); // %name%
        WAYPOINT_PUBLIC_BACKGROUND = new ItemTranslation(ls, () -> ic.getPublicWaypointMenuConfig().getBackgroundItem(),
                "inventory.waypoint.public.background");
        WAYPOINT_PUBLIC_SELECT = new ItemTranslation(ls, () -> ic.getPublicWaypointMenuConfig().getSelectItem(), "inventory.waypoint.public.select");
        WAYPOINT_PUBLIC_DELETE = new ItemTranslation(ls, () -> ic.getPublicWaypointMenuConfig().getDeleteItem(), "inventory.waypoint.public.delete");
        WAYPOINT_PUBLIC_RENAME = new ItemTranslation(ls, () -> ic.getPublicWaypointMenuConfig().getRenameItem(), "inventory.waypoint.public.rename");
        WAYPOINT_PUBLIC_TELEPORT = new ItemTranslation(ls, () -> ic.getPublicWaypointMenuConfig().getTeleportItem(), "inventory.waypoint.public.teleport");
        WAYPOINT_PUBLIC_TELEPORT_XP_POINTS = new ItemTranslation(ls, () -> ic.getPublicWaypointMenuConfig().getTeleportItem(),
                "inventory.waypoint.public.teleport.displayName",
                "inventory.waypoint.public.teleport.payment.xpPoints.description");
        WAYPOINT_PUBLIC_TELEPORT_XP_LEVELS = new ItemTranslation(ls, () -> ic.getPublicWaypointMenuConfig().getTeleportItem(),
                "inventory.waypoint.public.teleport.displayName",
                "inventory.waypoint.public.teleport.payment.xpLevels.description");
        WAYPOINT_PUBLIC_TELEPORT_VAULT = new ItemTranslation(ls, () -> ic.getPublicWaypointMenuConfig().getTeleportItem(),
                "inventory.waypoint.public.teleport.displayName",
                "inventory.waypoint.public.teleport.payment.vault.description");
        WAYPOINT_PUBLIC_SELECT_BEACON_COLOR = new ItemTranslation(ls, () -> ic.getPublicWaypointMenuConfig().getSelectBeaconColorItem(),
                "inventory.waypoint.public.selectBeaconColor");

        WAYPOINT_PERMISSION = new ItemTranslation(ls, () -> ic.getPermissionWaypointMenuConfig().getItem(), "inventory.waypoint.permission"); // %name%
        WAYPOINT_PERMISSION_BACKGROUND = new ItemTranslation(ls, () -> ic.getPermissionWaypointMenuConfig().getBackgroundItem(),
                "inventory.waypoint.permission.background");
        WAYPOINT_PERMISSION_SELECT = new ItemTranslation(ls, () -> ic.getPermissionWaypointMenuConfig().getSelectItem(),
                "inventory.waypoint.permission.select");
        WAYPOINT_PERMISSION_DELETE = new ItemTranslation(ls, () -> ic.getPermissionWaypointMenuConfig().getDeleteItem(),
                "inventory.waypoint.permission.delete");
        WAYPOINT_PERMISSION_RENAME = new ItemTranslation(ls, () -> ic.getPermissionWaypointMenuConfig().getRenameItem(),
                "inventory.waypoint.permission.rename");
        WAYPOINT_PERMISSION_TELEPORT = new ItemTranslation(ls, () -> ic.getPermissionWaypointMenuConfig().getTeleportItem(),
                "inventory.waypoint.permission.teleport");
        WAYPOINT_PERMISSION_TELEPORT_XP_POINTS = new ItemTranslation(ls, () -> ic.getPermissionWaypointMenuConfig().getTeleportItem(),
                "inventory.waypoint.permission.teleport.displayName",
                "inventory.waypoint.permission.teleport.payment.xpPoints.description");
        WAYPOINT_PERMISSION_TELEPORT_XP_LEVELS = new ItemTranslation(ls, () -> ic.getPermissionWaypointMenuConfig().getTeleportItem(),
                "inventory.waypoint.permission.teleport.displayName",
                "inventory.waypoint.permission.teleport.payment.xpLevels.description");
        WAYPOINT_PERMISSION_TELEPORT_VAULT = new ItemTranslation(ls, () -> ic.getPermissionWaypointMenuConfig().getTeleportItem(),
                "inventory.waypoint.permission.teleport.displayName",
                "inventory.waypoint.permission.teleport.payment.vault.description");
        WAYPOINT_PERMISSION_SELECT_BEACON_COLOR = new ItemTranslation(ls, () -> ic.getPermissionWaypointMenuConfig().getSelectBeaconColorItem(),
                "inventory.waypoint.permission.selectBeaconColor");


        SELECT_BEACON_COLOR_BACKGROUND = new ItemTranslation(ls, () -> ic.getSelectBeaconColorMenuConfig().getBackgroundItem(),
                "inventory.selectBeaconColor.background");
        SELECT_BEACON_COLOR_CLEAR = new ItemTranslation(ls, BlockColor.CLEAR::getMaterial, "inventory.selectBeaconColor.clear");
        BlockColor.CLEAR.setItemTranslation(SELECT_BEACON_COLOR_CLEAR);
        SELECT_BEACON_COLOR_LIGHT_GRAY = new ItemTranslation(ls, BlockColor.LIGHT_GRAY::getMaterial, "inventory.selectBeaconColor.lightGray");
        BlockColor.LIGHT_GRAY.setItemTranslation(SELECT_BEACON_COLOR_LIGHT_GRAY);
        SELECT_BEACON_COLOR_GRAY = new ItemTranslation(ls, BlockColor.GRAY::getMaterial, "inventory.selectBeaconColor.gray");
        BlockColor.GRAY.setItemTranslation(SELECT_BEACON_COLOR_GRAY);
        SELECT_BEACON_COLOR_PINK = new ItemTranslation(ls, BlockColor.PINK::getMaterial, "inventory.selectBeaconColor.pink");
        BlockColor.PINK.setItemTranslation(SELECT_BEACON_COLOR_PINK);
        SELECT_BEACON_COLOR_LIME = new ItemTranslation(ls, BlockColor.LIME::getMaterial, "inventory.selectBeaconColor.lime");
        BlockColor.LIME.setItemTranslation(SELECT_BEACON_COLOR_LIME);
        SELECT_BEACON_COLOR_YELLOW = new ItemTranslation(ls, BlockColor.YELLOW::getMaterial, "inventory.selectBeaconColor.yellow");
        BlockColor.YELLOW.setItemTranslation(SELECT_BEACON_COLOR_YELLOW);
        SELECT_BEACON_COLOR_LIGHT_BLUE = new ItemTranslation(ls, BlockColor.LIGHT_BLUE::getMaterial, "inventory.selectBeaconColor.lightBlue");
        BlockColor.LIGHT_BLUE.setItemTranslation(SELECT_BEACON_COLOR_LIGHT_BLUE);
        SELECT_BEACON_COLOR_MAGENTA = new ItemTranslation(ls, BlockColor.MAGENTA::getMaterial, "inventory.selectBeaconColor.magenta");
        BlockColor.MAGENTA.setItemTranslation(SELECT_BEACON_COLOR_MAGENTA);
        SELECT_BEACON_COLOR_ORANGE = new ItemTranslation(ls, BlockColor.ORANGE::getMaterial, "inventory.selectBeaconColor.orange");
        BlockColor.ORANGE.setItemTranslation(SELECT_BEACON_COLOR_ORANGE);
        SELECT_BEACON_COLOR_WHITE = new ItemTranslation(ls, BlockColor.WHITE::getMaterial, "inventory.selectBeaconColor.white");
        BlockColor.WHITE.setItemTranslation(SELECT_BEACON_COLOR_WHITE);
        SELECT_BEACON_COLOR_BLACK = new ItemTranslation(ls, BlockColor.BLACK::getMaterial, "inventory.selectBeaconColor.black");
        BlockColor.BLACK.setItemTranslation(SELECT_BEACON_COLOR_BLACK);
        SELECT_BEACON_COLOR_RED = new ItemTranslation(ls, BlockColor.RED::getMaterial, "inventory.selectBeaconColor.red");
        BlockColor.RED.setItemTranslation(SELECT_BEACON_COLOR_RED);
        SELECT_BEACON_COLOR_GREEN = new ItemTranslation(ls, BlockColor.GREEN::getMaterial, "inventory.selectBeaconColor.green");
        BlockColor.GREEN.setItemTranslation(SELECT_BEACON_COLOR_GREEN);
        SELECT_BEACON_COLOR_BROWN = new ItemTranslation(ls, BlockColor.BROWN::getMaterial, "inventory.selectBeaconColor.brown");
        BlockColor.BROWN.setItemTranslation(SELECT_BEACON_COLOR_BROWN);
        SELECT_BEACON_COLOR_BLUE = new ItemTranslation(ls, BlockColor.BLUE::getMaterial, "inventory.selectBeaconColor.blue");
        BlockColor.BLUE.setItemTranslation(SELECT_BEACON_COLOR_BLUE);
        SELECT_BEACON_COLOR_CYAN = new ItemTranslation(ls, BlockColor.CYAN::getMaterial, "inventory.selectBeaconColor.cyan");
        BlockColor.CYAN.setItemTranslation(SELECT_BEACON_COLOR_CYAN);
        SELECT_BEACON_COLOR_PURPLE = new ItemTranslation(ls, BlockColor.PURPLE::getMaterial, "inventory.selectBeaconColor.purple");
        BlockColor.PURPLE.setItemTranslation(SELECT_BEACON_COLOR_PURPLE);

        SELECT_FOLDER_BACKGROUND = new ItemTranslation(ls, () -> ic.getSelectFolderMenuConfig().getBackgroundItem(), "inventory.selectFolder.background");
        SELECT_FOLDER_NO_FOLDER = new ItemTranslation(ls, () -> ic.getSelectFolderMenuConfig().getNoFolderItem(), "inventory.selectFolder.noFolder");

        FOLDER_PRIVATE = new ItemTranslation(ls, () -> ic.getPrivateFolderMenuConfig().getDefaultItem(), "inventory.folder.private"); // %name%
        FOLDER_PRIVATE_BACKGROUND = new ItemTranslation(ls, () -> ic.getPrivateFolderMenuConfig().getBackgroundItem(), "inventory.folder.private.background");
        FOLDER_PRIVATE_DELETE = new ItemTranslation(ls, () -> ic.getPrivateFolderMenuConfig().getDeleteItem(), "inventory.folder.private.delete");
        FOLDER_PRIVATE_RENAME = new ItemTranslation(ls, () -> ic.getPrivateFolderMenuConfig().getRenameItem(), "inventory.folder.private.rename");

        FOLDER_PUBLIC = new ItemTranslation(ls, () -> ic.getPublicOverviewMenuConfig().getItem(), "inventory.folder.public");
        FOLDER_PUBLIC_BACKGROUND = new ItemTranslation(ls, () -> ic.getPublicOverviewMenuConfig().getBackgroundItem(), "inventory.folder.public.background");

        FOLDER_PERMISSION = new ItemTranslation(ls, () -> ic.getPermissionOverviewMenuConfig().getItem(), "inventory.folder.permission");
        FOLDER_PERMISSION_BACKGROUND = new ItemTranslation(ls, () -> ic.getPermissionOverviewMenuConfig().getBackgroundItem(),
                "inventory.folder.permission.background");

        CONFIRM_MENU_BACKGROUND = new ItemTranslation(ls, () -> ic.getConfirmMenuConfig().getBackgroundItem(), "inventory.confirmMenu.background");

        CONFIRM_MENU_WAYPOINT_PRIVATE_DELETE_DESCRIPTION = new ItemTranslation(ls, () -> ic.getConfirmMenuConfig().getDescriptionItem(),
                "inventory.confirmMenu.waypoint.private.delete.title");
        CONFIRM_MENU_WAYPOINT_PRIVATE_DELETE_YES = new ItemTranslation(ls, () -> ic.getConfirmMenuConfig().getYesItem(),
                "inventory.confirmMenu.waypoint.private.delete.yes");
        CONFIRM_MENU_WAYPOINT_PRIVATE_DELETE_NO = new ItemTranslation(ls, () -> ic.getConfirmMenuConfig().getNoItem(),
                "inventory.confirmMenu.waypoint.private.delete.no");

        CONFIRM_MENU_WAYPOINT_PUBLIC_DELETE_DESCRIPTION = new ItemTranslation(ls, () -> ic.getConfirmMenuConfig().getDescriptionItem(),
                "inventory.confirmMenu.waypoint.public.delete.title");
        CONFIRM_MENU_WAYPOINT_PUBLIC_DELETE_YES = new ItemTranslation(ls, () -> ic.getConfirmMenuConfig().getYesItem(),
                "inventory.confirmMenu.waypoint.public.delete.yes");
        CONFIRM_MENU_WAYPOINT_PUBLIC_DELETE_NO = new ItemTranslation(ls, () -> ic.getConfirmMenuConfig().getNoItem(),
                "inventory.confirmMenu.waypoint.public.delete.no");

        CONFIRM_MENU_WAYPOINT_PERMISSION_DELETE_DESCRIPTION = new ItemTranslation(ls, () -> ic.getConfirmMenuConfig().getDescriptionItem(),
                "inventory.confirmMenu.waypoint.permission.delete.title");
        CONFIRM_MENU_WAYPOINT_PERMISSION_DELETE_YES = new ItemTranslation(ls, () -> ic.getConfirmMenuConfig().getYesItem(),
                "inventory.confirmMenu.waypoint.permission.delete.yes");
        CONFIRM_MENU_WAYPOINT_PERMISSION_DELETE_NO = new ItemTranslation(ls, () -> ic.getConfirmMenuConfig().getNoItem(),
                "inventory.confirmMenu.waypoint.permission.delete.no");

        CONFIRM_MENU_FOLDER_PRIVATE_DELETE_DESCRIPTION = new ItemTranslation(ls, () -> ic.getConfirmMenuConfig().getDescriptionItem(),
                "inventory.confirmMenu.folder.private.delete.title");
        CONFIRM_MENU_FOLDER_PRIVATE_DELETE_YES = new ItemTranslation(ls, () -> ic.getConfirmMenuConfig().getYesItem(),
                "inventory.confirmMenu.folder.private.delete.yes");
        CONFIRM_MENU_FOLDER_PRIVATE_DELETE_NO = new ItemTranslation(ls, () -> ic.getConfirmMenuConfig().getNoItem(),
                "inventory.confirmMenu.folder.private.delete.no");
    }

    public final Translation
            TITLE_OWN,
            TITLE_OTHER,
            CYCLE_SORT_ACTIVE,
            CYCLE_SORT_INACTIVE,
            CYCLE_SORT_MODE_NAME_ASC,
            CYCLE_SORT_MODE_NAME_DESC,
            CYCLE_SORT_MODE_CREATED_ASC,
            CYCLE_SORT_MODE_CREATED_DESC,
            CYCLE_SORT_MODE_TYPE,
            WAYPOINT_DISTANCE_OTHER_WORLD,
            ANVIL_GUI_ENTER_NAME_HERE,
            ANVIL_GUI_ENTER_PERMISSION_HERE;

    public final ItemTranslation

            GENERAL_PREVIOUS,
            GENERAL_NEXT,
            GENERAL_BACK,
            CYCLE_SORT,
            OVERVIEW_BACKGROUND,
            OVERVIEW_DESELECT,
            OVERVIEW_TOGGLE_GLOBALS_SHOWN,
            OVERVIEW_TOGGLE_GLOBALS_HIDDEN,
            OVERVIEW_SET_WAYPOINT,
            OVERVIEW_CREATE_FOLDER,
            SELECT_WAYPOINT_TYPE_BACKGROUND,
            SELECT_WAYPOINT_TYPE_TITLE,
            SELECT_WAYPOINT_TYPE_PRIVATE,
            SELECT_WAYPOINT_TYPE_PUBLIC,
            SELECT_WAYPOINT_TYPE_PERMISSION,
            WAYPOINT_DEATH,
            WAYPOINT_DEATH_BACKGROUND,
            WAYPOINT_DEATH_SELECT,
            WAYPOINT_DEATH_TELEPORT,
            WAYPOINT_DEATH_TELEPORT_XP_POINTS,
            WAYPOINT_DEATH_TELEPORT_XP_LEVELS,
            WAYPOINT_DEATH_TELEPORT_VAULT,
            WAYPOINT_PRIVATE,
            WAYPOINT_PRIVATE_BACKGROUND,
            WAYPOINT_PRIVATE_SELECT,
            WAYPOINT_PRIVATE_DELETE,
            WAYPOINT_PRIVATE_RENAME,
            WAYPOINT_PRIVATE_MOVE_TO_FOLDER,
            WAYPOINT_PRIVATE_TELEPORT,
            WAYPOINT_PRIVATE_TELEPORT_XP_POINTS,
            WAYPOINT_PRIVATE_TELEPORT_XP_LEVELS,
            WAYPOINT_PRIVATE_TELEPORT_VAULT,
            WAYPOINT_PRIVATE_SELECT_BEACON_COLOR,
            WAYPOINT_PUBLIC,
            WAYPOINT_PUBLIC_BACKGROUND,
            WAYPOINT_PUBLIC_SELECT,
            WAYPOINT_PUBLIC_DELETE,
            WAYPOINT_PUBLIC_RENAME,
            WAYPOINT_PUBLIC_TELEPORT,
            WAYPOINT_PUBLIC_TELEPORT_XP_POINTS,
            WAYPOINT_PUBLIC_TELEPORT_XP_LEVELS,
            WAYPOINT_PUBLIC_TELEPORT_VAULT,
            WAYPOINT_PUBLIC_SELECT_BEACON_COLOR,
            WAYPOINT_PERMISSION,
            WAYPOINT_PERMISSION_BACKGROUND,
            WAYPOINT_PERMISSION_SELECT,
            WAYPOINT_PERMISSION_DELETE,
            WAYPOINT_PERMISSION_RENAME,
            WAYPOINT_PERMISSION_TELEPORT,
            WAYPOINT_PERMISSION_TELEPORT_XP_POINTS,
            WAYPOINT_PERMISSION_TELEPORT_XP_LEVELS,
            WAYPOINT_PERMISSION_TELEPORT_VAULT,
            WAYPOINT_PERMISSION_SELECT_BEACON_COLOR,
            SELECT_BEACON_COLOR_BACKGROUND,
            SELECT_BEACON_COLOR_CLEAR,
            SELECT_BEACON_COLOR_LIGHT_GRAY,
            SELECT_BEACON_COLOR_GRAY,
            SELECT_BEACON_COLOR_PINK,
            SELECT_BEACON_COLOR_LIME,
            SELECT_BEACON_COLOR_YELLOW,
            SELECT_BEACON_COLOR_LIGHT_BLUE,
            SELECT_BEACON_COLOR_MAGENTA,
            SELECT_BEACON_COLOR_ORANGE,
            SELECT_BEACON_COLOR_WHITE,
            SELECT_BEACON_COLOR_BLACK,
            SELECT_BEACON_COLOR_RED,
            SELECT_BEACON_COLOR_GREEN,
            SELECT_BEACON_COLOR_BROWN,
            SELECT_BEACON_COLOR_BLUE,
            SELECT_BEACON_COLOR_CYAN,
            SELECT_BEACON_COLOR_PURPLE,
            SELECT_FOLDER_BACKGROUND,
            SELECT_FOLDER_NO_FOLDER,
            FOLDER_PRIVATE,
            FOLDER_PRIVATE_BACKGROUND,
            FOLDER_PRIVATE_DELETE,
            FOLDER_PRIVATE_RENAME,
            FOLDER_PUBLIC,
            FOLDER_PUBLIC_BACKGROUND,
            FOLDER_PERMISSION,
            FOLDER_PERMISSION_BACKGROUND,
            CONFIRM_MENU_BACKGROUND,
            CONFIRM_MENU_WAYPOINT_PRIVATE_DELETE_DESCRIPTION,
            CONFIRM_MENU_WAYPOINT_PRIVATE_DELETE_YES,
            CONFIRM_MENU_WAYPOINT_PRIVATE_DELETE_NO,
            CONFIRM_MENU_WAYPOINT_PUBLIC_DELETE_DESCRIPTION,
            CONFIRM_MENU_WAYPOINT_PUBLIC_DELETE_YES,
            CONFIRM_MENU_WAYPOINT_PUBLIC_DELETE_NO,
            CONFIRM_MENU_WAYPOINT_PERMISSION_DELETE_DESCRIPTION,
            CONFIRM_MENU_WAYPOINT_PERMISSION_DELETE_YES,
            CONFIRM_MENU_WAYPOINT_PERMISSION_DELETE_NO,
            CONFIRM_MENU_FOLDER_PRIVATE_DELETE_DESCRIPTION,
            CONFIRM_MENU_FOLDER_PRIVATE_DELETE_YES,
            CONFIRM_MENU_FOLDER_PRIVATE_DELETE_NO;

}
