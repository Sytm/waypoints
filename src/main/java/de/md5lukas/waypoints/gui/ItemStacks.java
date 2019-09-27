package de.md5lukas.waypoints.gui;

import de.md5lukas.commons.inventory.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static de.md5lukas.waypoints.Messages.*;
import static de.md5lukas.waypoints.Waypoints.message;
import static de.md5lukas.waypoints.store.WPConfig.inventory;

public class ItemStacks {

	public static ItemStack getPreviousItem(Player p) {
		return new ItemBuilder(inventory().getGeneralPreviousItem()).name(message(INVENTORY_GENERAL_PREVIOUS_DISPLAY_NAME, p))
			.lore(message(INVENTORY_GENERAL_PREVIOUS_DESCRIPTION, p)).make();
	}

	public static ItemStack getNextItem(Player p) {
		return new ItemBuilder(inventory().getGeneralNextItem()).name(message(INVENTORY_GENERAL_NEXT_DISPLAY_NAME, p))
			.lore(message(INVENTORY_GENERAL_NEXT_DESCRIPTION, p)).make();
	}

	public static ItemStack getBackItem(Player p) {
		return new ItemBuilder(inventory().getGeneralBackItem()).name(message(INVENTORY_GENERAL_BACK_DISPLAY_NAME, p))
			.lore(message(INVENTORY_GENERAL_BACK_DESCRIPTION, p)).make();
	}

	public static ItemStack getOverviewBackgroundItem(Player p) {
		return new ItemBuilder(inventory().getOverviewBackgroundItem()).name(message(INVENTORY_OVERVIEW_BACKGROUND_DISPLAY_NAME, p))
			.lore(message(INVENTORY_OVERVIEW_BACKGROUND_DESCRIPTION, p)).make();
	}

	public static ItemStack getOverviewDeselectItem(Player p) {
		return new ItemBuilder(inventory().getOverviewDeselectItem()).name(message(INVENTORY_OVERVIEW_DESELECT_DISPLAY_NAME, p))
			.lore(message(INVENTORY_OVERVIEW_DESELECT_DESCRIPTION, p)).make();
	}

	public static ItemStack getOverviewToggleGlobalsShownItem(Player p) {
		return new ItemBuilder(inventory().getOverviewToggleGlobalsItem()).name(message(INVENTORY_OVERVIEW_TOGGLE_GLOBALS_SHOWN_DISPLAY_NAME, p))
			.lore(message(INVENTORY_OVERVIEW_TOGGLE_GLOBALS_SHOWN_DESCRIPTION, p)).make();
	}

	public static ItemStack getOverviewToggleGlobalsHiddenItem(Player p) {
		return new ItemBuilder(inventory().getOverviewToggleGlobalsItem()).name(message(INVENTORY_OVERVIEW_TOGGLE_GLOBALS_HIDDEN_DISPLAY_NAME, p))
			.lore(message(INVENTORY_OVERVIEW_TOGGLE_GLOBALS_HIDDEN_DESCRIPTION, p)).make();
	}

	public static ItemStack getWaypointDeathBackgroundItem(Player p) {
		return new ItemBuilder(inventory().getWaypointDeathBackgroundItem()).name(message(INVENTORY_WAYPOINT_DEATH_BACKGROUND_DISPLAY_NAME, p))
			.lore(message(INVENTORY_WAYPOINT_DEATH_BACKGROUND_DESCRIPTION, p)).make();
	}

	public static ItemStack getWaypointDeathSelectItem(Player p) {
		return new ItemBuilder(inventory().getWaypointDeathSelectItem()).name(message(INVENTORY_WAYPOINT_DEATH_SELECT_DISPLAY_NAME, p))
			.lore(message(INVENTORY_WAYPOINT_DEATH_SELECT_DESCRIPTION, p)).make();
	}

	public static ItemStack getWaypointDeathTeleportItem(Player p) {
		return new ItemBuilder(inventory().getWaypointDeathTeleportItem()).name(message(INVENTORY_WAYPOINT_DEATH_TELEPORT_DISPLAY_NAME, p))
			.lore(message(INVENTORY_WAYPOINT_DEATH_TELEPORT_DESCRIPTION, p)).make();
	}

	public static ItemStack getWaypointPrivateBackgroundItem(Player p) {
		return new ItemBuilder(inventory().getWaypointPrivateBackgroundItem()).name(message(INVENTORY_WAYPOINT_PRIVATE_BACKGROUND_DISPLAY_NAME, p))
			.lore(message(INVENTORY_WAYPOINT_PRIVATE_BACKGROUND_DESCRIPTION, p)).make();
	}

	public static ItemStack getWaypointPrivateSelectItem(Player p) {
		return new ItemBuilder(inventory().getWaypointPrivateSelectItem()).name(message(INVENTORY_WAYPOINT_PRIVATE_SELECT_DISPLAY_NAME, p))
			.lore(message(INVENTORY_WAYPOINT_PRIVATE_SELECT_DESCRIPTION, p)).make();
	}

	public static ItemStack getWaypointPrivateDeleteItem(Player p) {
		return new ItemBuilder(inventory().getWaypointPrivateDeleteItem()).name(message(INVENTORY_WAYPOINT_PRIVATE_DELETE_DISPLAY_NAME, p))
			.lore(message(INVENTORY_WAYPOINT_PRIVATE_DELETE_DESCRIPTION, p)).make();
	}

	public static ItemStack getWaypointPrivateRenameItem(Player p) {
		return new ItemBuilder(inventory().getWaypointPrivateRenameItem()).name(message(INVENTORY_WAYPOINT_PRIVATE_RENAME_DISPLAY_NAME, p))
			.lore(message(INVENTORY_WAYPOINT_PRIVATE_RENAME_DESCRIPTION, p)).make();
	}

	public static ItemStack getWaypointPrivateMoveToFolderItem(Player p) {
		return new ItemBuilder(inventory().getWaypointPrivateMoveToFolderItem()).name(message(INVENTORY_WAYPOINT_PRIVATE_MOVE_TO_FOLDER_DISPLAY_NAME, p))
			.lore(message(INVENTORY_WAYPOINT_PRIVATE_MOVE_TO_FOLDER_DESCRIPTION, p)).make();
	}

	public static ItemStack getWaypointPrivateTeleportItem(Player p) {
		return new ItemBuilder(inventory().getWaypointPrivateTeleportItem()).name(message(INVENTORY_WAYPOINT_PRIVATE_TELEPORT_DISPLAY_NAME, p))
			.lore(message(INVENTORY_WAYPOINT_PRIVATE_TELEPORT_DESCRIPTION, p)).make();
	}


	public static ItemStack getWaypointPublicBackgroundItem(Player p) {
		return new ItemBuilder(inventory().getWaypointPublicBackgroundItem()).name(message(INVENTORY_WAYPOINT_PUBLIC_BACKGROUND_DISPLAY_NAME, p))
			.lore(message(INVENTORY_WAYPOINT_PUBLIC_BACKGROUND_DESCRIPTION, p)).make();
	}

	public static ItemStack getWaypointPublicSelectItem(Player p) {
		return new ItemBuilder(inventory().getWaypointPublicSelectItem()).name(message(INVENTORY_WAYPOINT_PUBLIC_SELECT_DISPLAY_NAME, p))
			.lore(message(INVENTORY_WAYPOINT_PUBLIC_SELECT_DESCRIPTION, p)).make();
	}

	public static ItemStack getWaypointPublicDeleteItem(Player p) {
		return new ItemBuilder(inventory().getWaypointPublicDeleteItem()).name(message(INVENTORY_WAYPOINT_PUBLIC_DELETE_DISPLAY_NAME, p))
			.lore(message(INVENTORY_WAYPOINT_PUBLIC_DELETE_DESCRIPTION, p)).make();
	}

	public static ItemStack getWaypointPublicRenameItem(Player p) {
		return new ItemBuilder(inventory().getWaypointPublicRenameItem()).name(message(INVENTORY_WAYPOINT_PUBLIC_RENAME_DISPLAY_NAME, p))
			.lore(message(INVENTORY_WAYPOINT_PUBLIC_RENAME_DESCRIPTION, p)).make();
	}

	public static ItemStack getWaypointPublicTeleportItem(Player p) {
		return new ItemBuilder(inventory().getWaypointPublicTeleportItem()).name(message(INVENTORY_WAYPOINT_PUBLIC_TELEPORT_DISPLAY_NAME, p))
			.lore(message(INVENTORY_WAYPOINT_PUBLIC_TELEPORT_DESCRIPTION, p)).make();
	}

	public static ItemStack getWaypointPermissionBackgroundItem(Player p) {
		return new ItemBuilder(inventory().getWaypointPermissionBackgroundItem()).name(message(INVENTORY_WAYPOINT_PERMISSION_BACKGROUND_DISPLAY_NAME, p))
			.lore(message(INVENTORY_WAYPOINT_PERMISSION_BACKGROUND_DESCRIPTION, p)).make();
	}

	public static ItemStack getWaypointPermissionSelectItem(Player p) {
		return new ItemBuilder(inventory().getWaypointPermissionSelectItem()).name(message(INVENTORY_WAYPOINT_PERMISSION_SELECT_DISPLAY_NAME, p))
			.lore(message(INVENTORY_WAYPOINT_PERMISSION_SELECT_DESCRIPTION, p)).make();
	}

	public static ItemStack getWaypointPermissionDeleteItem(Player p) {
		return new ItemBuilder(inventory().getWaypointPermissionDeleteItem()).name(message(INVENTORY_WAYPOINT_PERMISSION_DELETE_DISPLAY_NAME, p))
			.lore(message(INVENTORY_WAYPOINT_PERMISSION_DELETE_DESCRIPTION, p)).make();
	}

	public static ItemStack getWaypointPermissionRenameItem(Player p) {
		return new ItemBuilder(inventory().getWaypointPermissionRenameItem()).name(message(INVENTORY_WAYPOINT_PERMISSION_RENAME_DISPLAY_NAME, p))
			.lore(message(INVENTORY_WAYPOINT_PERMISSION_RENAME_DESCRIPTION, p)).make();
	}

	public static ItemStack getWaypointPermissionTeleportItem(Player p) {
		return new ItemBuilder(inventory().getWaypointPermissionTeleportItem()).name(message(INVENTORY_WAYPOINT_PERMISSION_TELEPORT_DISPLAY_NAME, p))
			.lore(message(INVENTORY_WAYPOINT_PERMISSION_TELEPORT_DESCRIPTION, p)).make();
	}

	public static ItemStack getSelectFolderBackgroundItem(Player p) {
		return new ItemBuilder(inventory().getSelectFolderBackgroundItem()).name(message(INVENTORY_SELECT_FOLDER_BACKGROUND_DISPLAY_NAME, p))
			.lore(message(INVENTORY_SELECT_FOLDER_BACKGROUND_DESCRIPTION, p)).make();
	}

	public static ItemStack getSelectFolderNoFolderItem(Player p) {
		return new ItemBuilder(inventory().getSelectFolderNoFolderItem()).name(message(INVENTORY_SELECT_FOLDER_NO_FOLDER_DISPLAY_NAME, p))
			.lore(message(INVENTORY_SELECT_FOLDER_NO_FOLDER_DESCRIPTION, p)).make();
	}

	public static ItemStack getFolderPrivateBackgroundItem(Player p) {
		return new ItemBuilder(inventory().getFolderPrivateBackgroundItem()).name(message(INVENTORY_FOLDER_PRIVATE_BACKGROUND_DISPLAY_NAME, p))
			.lore(message(INVENTORY_FOLDER_PRIVATE_BACKGROUND_DESCRIPTION, p)).make();
	}

	public static ItemStack getFolderPrivateDeleteItem(Player p) {
		return new ItemBuilder(inventory().getFolderPrivateDeleteItem()).name(message(INVENTORY_FOLDER_PRIVATE_DELETE_DISPLAY_NAME, p))
			.lore(message(INVENTORY_FOLDER_PRIVATE_DELETE_DESCRIPTION, p)).make();
	}

	public static ItemStack getFolderPrivateRenameItem(Player p) {
		return new ItemBuilder(inventory().getFolderPrivateRenameItem()).name(message(INVENTORY_FOLDER_PRIVATE_RENAME_DISPLAY_NAME, p))
			.lore(message(INVENTORY_FOLDER_PRIVATE_RENAME_DESCRIPTION, p)).make();
	}

	public static ItemStack getFolderPublicBackgroundItem(Player p) {
		return new ItemBuilder(inventory().getFolderPrivateBackgroundItem()).name(message(INVENTORY_FOLDER_PUBLIC_BACKGROUND_DISPLAY_NAME, p))
			.lore(message(INVENTORY_FOLDER_PUBLIC_BACKGROUND_DESCRIPTION, p)).make();
	}

	public static ItemStack getFolderPermissionBackgroundItem(Player p) {
		return new ItemBuilder(inventory().getFolderPrivateBackgroundItem()).name(message(INVENTORY_FOLDER_PERMISSION_BACKGROUND_DISPLAY_NAME, p))
			.lore(message(INVENTORY_FOLDER_PERMISSION_BACKGROUND_DESCRIPTION, p)).make();
	}
}
