package de.md5lukas.waypoints;

public enum Messages {

	// ---------------------- General ----------------------

	GENERAL_NOT_A_PLAYER("general.notAPlayer"),
	GENERAL_NO_PERMISSION("general.noPermission"),
	GENERAL_WAYPOINT_NOT_FOUND("general.waypointNotFound"),
	GENERAL_FOLDER_NOT_FOUND("general.folderNotFound"),
	GENERAL_NOT_A_VALID_UUID("general.notAValidUuid"),

	// ---------------------- Misc chat messages ----------------------

	DISPLAY_WRONG_WORLD("display.wrongWorld"), // %currentworld%, %correctworld%

	CHAT_ACTION_UPDATE_ITEM_WAYPOINT_PRIVATE("chatAction.updateItem.waypoint.private"),
	CHAT_ACTION_UPDATE_ITEM_FOLDER_PRIVATE("chatAction.updateItem.folder.private"),

	CHAT_ACTION_RENAME_WAYPOINT_PRIVATE("chatAction.rename.waypoint.private"),
	CHAT_ACTION_RENAME_WAYPOINT_PUBLIC("chatAction.rename.waypoint.public"),
	CHAT_ACTION_RENAME_WAYPOINT_PERMISSION("chatAction.rename.waypoint.permission"),
	CHAT_ACTION_RENAME_FOLDER_PRIVATE("chatAction.rename.folder.private"),

	//region Commands
	COMMAND_HELP_TITLE("command.help.title"),
	COMMAND_HELP_HELP("command.help.help"),
	COMMAND_HELP_SET_PRIVATE("command.help.set.private"),
	COMMAND_HELP_SET_PUBLIC("command.help.set.public"),
	COMMAND_HELP_SET_PERMISSION("command.help.set.permission"),
	COMMAND_HELP_COMPASS("command.help.compass"),
	COMMAND_HELP_OTHER("command.help.other"),
	COMMAND_HELP_UPDATE_ITEM("command.help.updateItem"),
	COMMAND_HELP_RENAME_NORMAL("command.help.rename.normal"),
	COMMAND_HELP_RENAME_WAYPOINT_ONLY("command.help.rename.waypointOnly"),
	COMMAND_HELP_RENAME_FOLDER_ONLY("command.help.rename.folderOnly"),

	COMMAND_COMPASS_DISABLED("command.compass.disabled"),
	COMMAND_COMPASS_LOCKED("command.compass.locked"),
	COMMAND_COMPASS_SET_SUCCESS("command.compass.setSuccess"),

	COMMAND_SET_PRIVATE_WRONG_USAGE("command.set.private.wrongUsage"),
	COMMAND_SET_PRIVATE_NAME_DUPLICATE("command.set.private.nameDuplicate"),
	COMMAND_SET_PRIVATE_LIMIT_REACHED("command.set.private.limitReached"),
	COMMAND_SET_PRIVATE_SUCCESS("command.set.private.success"),

	COMMAND_SET_PUBLIC_WRONG_USAGE("command.set.public.wrongUsage"),
	COMMAND_SET_PUBLIC_NAME_DUPLICATE("command.set.public.nameDuplicate"),
	COMMAND_SET_PUBLIC_SUCCESS("command.set.public.success"),

	COMMAND_SET_PERMISSION_WRONG_USAGE("command.set.permission.wrongUsage"),
	COMMAND_SET_PERMISSION_NAME_DUPLICATE("command.set.permission.nameDuplicate"),
	COMMAND_SET_PERMISSION_SUCCESS("command.set.permission.success"),

	COMMAND_CREATE_FOLDER_WRONG_USAGE("command.createFolder.wrongUsage"),
	COMMAND_CREATE_FOLDER_NAME_DUPLICATE("command.createFolder.nameDuplicate"),
	COMMAND_CREATE_FOLDER_SUCCESS("command.createFolder.success"),

	COMMAND_OTHER_WRONG_USAGE("command.other.wrongUsage"),
	COMMAND_OTHER_NOT_A_VALID_UUID_OR_PLAYER("command.other.notAValidUuidOrPlayerName"),
	COMMAND_OTHER_UUID_NOT_FOUND("command.other.uuidNotFound"),
	COMMAND_OTHER_PLAYER_NAME_NOT_FOUND("command.other.playerNameNotFound"),

	COMMAND_UPDATE_ITEM_DISABLED("command.updateItem.disabled"),
	COMMAND_UPDATE_ITEM_NOT_A_VALID_ITEM("command.updateItem.notAValidItem"),
	COMMAND_UPDATE_ITEM_WRONG_USAGE("command.updateItem.wrongUsage"),
	COMMAND_UPDATE_ITEM_WAYPOINT_SUCCESS("command.updateItem.waypoint.success"),
	COMMAND_UPDATE_ITEM_FOLDER_SUCCESS("command.updateItem.folder.success"),

	COMMAND_RENAME_DISABLED("command.rename.disabled"),
	COMMAND_RENAME_WAYPOINT_PRIVATE_DISABLED("command.rename.waypoint.private.disabled"),
	COMMAND_RENAME_WAYPOINT_PUBLIC_DISABLED("command.rename.waypoint.public.disabled"),
	COMMAND_RENAME_WAYPOINT_PERMISSION_DISABLED("command.rename.waypoint.permission.disabled"),
	COMMAND_RENAME_FOLDER_DISABLED("command.rename.folder.disabled"),
	COMMAND_RENAME_WRONG_USAGE("command.rename.wrongUsage"),
	COMMAND_RENAME_WAYPOINT_PRIVATE_NAME_DUPLICATE("command.rename.waypoint.private.nameDuplicate"),
	COMMAND_RENAME_WAYPOINT_PUBLIC_NAME_DUPLICATE("command.rename.waypoint.public.nameDuplicate"),
	COMMAND_RENAME_WAYPOINT_PERMISSION_NAME_DUPLICATE("command.rename.waypoint.permission.nameDuplicate"),
	COMMAND_RENAME_WAYPOINT_SUCCESS("command.rename.waypoint.success"),
	COMMAND_RENAME_FOLDER_NAME_DUPLICATE("command.rename.folder.nameDuplicate"),
	COMMAND_RENAME_FOLDER_SUCCESS("command.rename.folder.success"),

	COMMAND_NOT_FOUND("command.notFound"),
	//endregion


	//region Inventory
	INVENTORY_GENERAL_PREVIOUS_DISPLAY_NAME("inventory.general.previous.displayName"),
	INVENTORY_GENERAL_PREVIOUS_DESCRIPTION("inventory.general.previous.description"),
	INVENTORY_GENERAL_NEXT_DISPLAY_NAME("inventory.general.next.displayName"),
	INVENTORY_GENERAL_NEXT_DESCRIPTION("inventory.general.next.description"),
	INVENTORY_GENERAL_BACK_DISPLAY_NAME("inventory.general.back.displayName"),
	INVENTORY_GENERAL_BACK_DESCRIPTION("inventory.general.back.description"),

	INVENTORY_CYCLE_SORT_DISPLAY_NAME("inventory.cycleSort.displayName"),
	INVENTORY_CYCLE_SORT_DESCRIPTION("inventory.cycleSort.description"),
	INVENTORY_CYCLE_SORT_ACTIVE("inventory.cycleSort.active"), // %name%
	INVENTORY_CYCLE_SORT_INACTIVE("inventory.cycleSort.inactive"), // %name%
	INVENTORY_CYCLE_SORT_MODE_NAME_ASC("inventory.cycleSort.mode.name.ascending"),
	INVENTORY_CYCLE_SORT_MODE_NAME_DESC("inventory.cycleSort.mode.name.descending"),
	INVENTORY_CYCLE_SORT_MODE_CREATED_ASC("inventory.cycleSort.mode.createdAt.ascending"),
	INVENTORY_CYCLE_SORT_MODE_CREATED_DESC("inventory.cycleSort.mode.createdAt.descending"),
	INVENTORY_CYCLE_SORT_MODE_TYPE("inventory.cycleSort.mode.type"),

	INVENTORY_OVERVIEW_BACKGROUND_DISPLAY_NAME("inventory.overview.background.displayName"),
	INVENTORY_OVERVIEW_BACKGROUND_DESCRIPTION("inventory.overview.background.description"),
	INVENTORY_OVERVIEW_DESELECT_DISPLAY_NAME("inventory.overview.deselect.displayName"),
	INVENTORY_OVERVIEW_DESELECT_DESCRIPTION("inventory.overview.deselect.description"),
	INVENTORY_OVERVIEW_TOGGLE_GLOBALS_SHOWN_DISPLAY_NAME("inventory.overview.toggleGlobals.shown.displayName"),
	INVENTORY_OVERVIEW_TOGGLE_GLOBALS_SHOWN_DESCRIPTION("inventory.overview.toggleGlobals.shown.description"),
	INVENTORY_OVERVIEW_TOGGLE_GLOBALS_HIDDEN_DISPLAY_NAME("inventory.overview.toggleGlobals.hidden.displayName"),
	INVENTORY_OVERVIEW_TOGGLE_GLOBALS_HIDDEN_DESCRIPTION("inventory.overview.toggleGlobals.hidden.description"),

	INVENTORY_WAYPOINT_DISTANCE_OTHER_WORLD("inventory.waypoint.distance.otherWorld"),

	INVENTORY_WAYPOINT_DEATH_DISPLAY_NAME("inventory.waypoint.death.displayName"),
	INVENTORY_WAYPOINT_DEATH_DESCRIPTION("inventory.waypoint.death.description"), // %world%, %x%, %y%, %z%, %blockX%, %blockY%, %blockZ%, %distance%
	INVENTORY_WAYPOINT_DEATH_BACKGROUND_DISPLAY_NAME("inventory.waypoint.death.background.displayName"),
	INVENTORY_WAYPOINT_DEATH_BACKGROUND_DESCRIPTION("inventory.waypoint.death.background.description"),
	INVENTORY_WAYPOINT_DEATH_SELECT_DISPLAY_NAME("inventory.waypoint.death.select.displayName"),
	INVENTORY_WAYPOINT_DEATH_SELECT_DESCRIPTION("inventory.waypoint.death.select.description"),
	INVENTORY_WAYPOINT_DEATH_TELEPORT_DISPLAY_NAME("inventory.waypoint.death.teleport.displayName"),
	INVENTORY_WAYPOINT_DEATH_TELEPORT_DESCRIPTION("inventory.waypoint.death.teleport.description"),

	INVENTORY_WAYPOINT_PRIVATE_DISPLAY_NAME("inventory.waypoint.private.displayName"), // %name%
	INVENTORY_WAYPOINT_PRIVATE_DESCRIPTION("inventory.waypoint.private.description"), // %world%, %x%, %y%, %z%, %blockX%, %blockY%, %blockZ%, %distance%
	INVENTORY_WAYPOINT_PRIVATE_BACKGROUND_DISPLAY_NAME("inventory.waypoint.private.background.displayName"),
	INVENTORY_WAYPOINT_PRIVATE_BACKGROUND_DESCRIPTION("inventory.waypoint.private.background.description"),
	INVENTORY_WAYPOINT_PRIVATE_SELECT_DISPLAY_NAME("inventory.waypoint.private.select.displayName"),
	INVENTORY_WAYPOINT_PRIVATE_SELECT_DESCRIPTION("inventory.waypoint.private.select.description"),
	INVENTORY_WAYPOINT_PRIVATE_DELETE_DISPLAY_NAME("inventory.waypoint.private.delete.displayName"),
	INVENTORY_WAYPOINT_PRIVATE_DELETE_DESCRIPTION("inventory.waypoint.private.delete.description"),
	INVENTORY_WAYPOINT_PRIVATE_RENAME_DISPLAY_NAME("inventory.waypoint.private.rename.displayName"),
	INVENTORY_WAYPOINT_PRIVATE_RENAME_DESCRIPTION("inventory.waypoint.private.rename.description"),
	INVENTORY_WAYPOINT_PRIVATE_MOVE_TO_FOLDER_DISPLAY_NAME("inventory.waypoint.private.moveToFolder.displayName"),
	INVENTORY_WAYPOINT_PRIVATE_MOVE_TO_FOLDER_DESCRIPTION("inventory.waypoint.private.moveToFolder.description"),
	INVENTORY_WAYPOINT_PRIVATE_TELEPORT_DISPLAY_NAME("inventory.waypoint.private.teleport.displayName"),
	INVENTORY_WAYPOINT_PRIVATE_TELEPORT_DESCRIPTION("inventory.waypoint.private.teleport.description"),

	INVENTORY_WAYPOINT_PUBLIC_DISPLAY_NAME("inventory.waypoint.public.displayName"), // %name%
	INVENTORY_WAYPOINT_PUBLIC_DESCRIPTION("inventory.waypoint.public.description"), // %world%, %x%, %y%, %z%, %blockX%, %blockY%, %blockZ%, %distance%
	INVENTORY_WAYPOINT_PUBLIC_BACKGROUND_DISPLAY_NAME("inventory.waypoint.public.background.displayName"),
	INVENTORY_WAYPOINT_PUBLIC_BACKGROUND_DESCRIPTION("inventory.waypoint.public.background.description"),
	INVENTORY_WAYPOINT_PUBLIC_SELECT_DISPLAY_NAME("inventory.waypoint.public.select.displayName"),
	INVENTORY_WAYPOINT_PUBLIC_SELECT_DESCRIPTION("inventory.waypoint.public.select.description"),
	INVENTORY_WAYPOINT_PUBLIC_DELETE_DISPLAY_NAME("inventory.waypoint.public.delete.displayName"),
	INVENTORY_WAYPOINT_PUBLIC_DELETE_DESCRIPTION("inventory.waypoint.public.delete.description"),
	INVENTORY_WAYPOINT_PUBLIC_RENAME_DISPLAY_NAME("inventory.waypoint.public.rename.displayName"),
	INVENTORY_WAYPOINT_PUBLIC_RENAME_DESCRIPTION("inventory.waypoint.public.rename.description"),
	INVENTORY_WAYPOINT_PUBLIC_TELEPORT_DISPLAY_NAME("inventory.waypoint.public.teleport.displayName"),
	INVENTORY_WAYPOINT_PUBLIC_TELEPORT_DESCRIPTION("inventory.waypoint.public.teleport.description"),

	INVENTORY_WAYPOINT_PERMISSION_DISPLAY_NAME("inventory.waypoint.permission.displayName"), // %name%
	INVENTORY_WAYPOINT_PERMISSION_DESCRIPTION("inventory.waypoint.permission.description"), // %world%, %x%, %y%, %z%, %blockX%, %blockY%, %blockZ%, %distance%
	INVENTORY_WAYPOINT_PERMISSION_BACKGROUND_DISPLAY_NAME("inventory.waypoint.permission.background.displayName"),
	INVENTORY_WAYPOINT_PERMISSION_BACKGROUND_DESCRIPTION("inventory.waypoint.permission.background.description"),
	INVENTORY_WAYPOINT_PERMISSION_SELECT_DISPLAY_NAME("inventory.waypoint.permission.select.displayName"),
	INVENTORY_WAYPOINT_PERMISSION_SELECT_DESCRIPTION("inventory.waypoint.permission.select.description"),
	INVENTORY_WAYPOINT_PERMISSION_DELETE_DISPLAY_NAME("inventory.waypoint.permission.delete.displayName"),
	INVENTORY_WAYPOINT_PERMISSION_DELETE_DESCRIPTION("inventory.waypoint.permission.delete.description"),
	INVENTORY_WAYPOINT_PERMISSION_RENAME_DISPLAY_NAME("inventory.waypoint.permission.rename.displayName"),
	INVENTORY_WAYPOINT_PERMISSION_RENAME_DESCRIPTION("inventory.waypoint.permission.rename.description"),
	INVENTORY_WAYPOINT_PERMISSION_TELEPORT_DISPLAY_NAME("inventory.waypoint.permission.teleport.displayName"),
	INVENTORY_WAYPOINT_PERMISSION_TELEPORT_DESCRIPTION("inventory.waypoint.permission.teleport.description"),

	INVENTORY_SELECT_FOLDER_BACKGROUND_DISPLAY_NAME("inventory.selectFolder.background.displayName"),
	INVENTORY_SELECT_FOLDER_BACKGROUND_DESCRIPTION("inventory.selectFolder.background.description"),
	INVENTORY_SELECT_FOLDER_NO_FOLDER_DISPLAY_NAME("inventory.selectFolder.noFolder.displayName"),
	INVENTORY_SELECT_FOLDER_NO_FOLDER_DESCRIPTION("inventory.selectFolder.noFolder.description"),

	INVENTORY_FOLDER_PRIVATE_DISPLAY_NAME("inventory.folder.private.displayName"), // %name%
	INVENTORY_FOLDER_PRIVATE_DESCRIPTION("inventory.folder.private.description"), // %amount%
	INVENTORY_FOLDER_PRIVATE_BACKGROUND_DISPLAY_NAME("inventory.folder.private.background.displayName"),
	INVENTORY_FOLDER_PRIVATE_BACKGROUND_DESCRIPTION("inventory.folder.private.background.description"),
	INVENTORY_FOLDER_PRIVATE_DELETE_DISPLAY_NAME("inventory.folder.private.delete.displayName"),
	INVENTORY_FOLDER_PRIVATE_DELETE_DESCRIPTION("inventory.folder.private.delete.description"),
	INVENTORY_FOLDER_PRIVATE_RENAME_DISPLAY_NAME("inventory.folder.private.rename.displayName"),
	INVENTORY_FOLDER_PRIVATE_RENAME_DESCRIPTION("inventory.folder.private.rename.description"),

	INVENTORY_FOLDER_PUBLIC_DISPLAY_NAME("inventory.folder.public.displayName"),
	INVENTORY_FOLDER_PUBLIC_DESCRIPTION("inventory.folder.public.description"), // %amount%
	INVENTORY_FOLDER_PUBLIC_BACKGROUND_DISPLAY_NAME("inventory.folder.public.background.displayName"),
	INVENTORY_FOLDER_PUBLIC_BACKGROUND_DESCRIPTION("inventory.folder.public.background.description"),

	INVENTORY_FOLDER_PERMISSION_DISPLAY_NAME("inventory.folder.permission.displayName"),
	INVENTORY_FOLDER_PERMISSION_DESCRIPTION("inventory.folder.permission.description"), // %amount%
	INVENTORY_FOLDER_PERMISSION_BACKGROUND_DISPLAY_NAME("inventory.folder.permission.background.displayName"),
	INVENTORY_FOLDER_PERMISSION_BACKGROUND_DESCRIPTION("inventory.folder.permission.background.description"),

	INVENTORY_CONFIRM_MENU_BACKGROUND_DISPLAY_NAME("inventory.confirmMenu.background.displayName"),
	INVENTORY_CONFIRM_MENU_BACKGROUND_DESCRIPTION("inventory.confirmMenu.background.description"),

	INVENTORY_CONFIRM_MENU_WAYPOINT_PRIVATE_DELETE_DESCRIPTION_DISPLAY_NAME("inventory.confirmMenu.waypoint.private.delete.title.displayName"),
	INVENTORY_CONFIRM_MENU_WAYPOINT_PRIVATE_DELETE_DESCRIPTION_DESCRIPTION("inventory.confirmMenu.waypoint.private.delete.title.description"),
	INVENTORY_CONFIRM_MENU_WAYPOINT_PRIVATE_DELETE_YES_DISPLAY_NAME("inventory.confirmMenu.waypoint.private.delete.yes.displayName"),
	INVENTORY_CONFIRM_MENU_WAYPOINT_PRIVATE_DELETE_YES_DESCRIPTION("inventory.confirmMenu.waypoint.private.delete.yes.description"),
	INVENTORY_CONFIRM_MENU_WAYPOINT_PRIVATE_DELETE_NO_DISPLAY_NAME("inventory.confirmMenu.waypoint.private.delete.no.displayName"),
	INVENTORY_CONFIRM_MENU_WAYPOINT_PRIVATE_DELETE_NO_DESCRIPTION("inventory.confirmMenu.waypoint.private.delete.no.description"),

	INVENTORY_CONFIRM_MENU_WAYPOINT_PUBLIC_DELETE_DESCRIPTION_DISPLAY_NAME("inventory.confirmMenu.waypoint.public.delete.title.displayName"),
	INVENTORY_CONFIRM_MENU_WAYPOINT_PUBLIC_DELETE_DESCRIPTION_DESCRIPTION("inventory.confirmMenu.waypoint.public.delete.title.description"),
	INVENTORY_CONFIRM_MENU_WAYPOINT_PUBLIC_DELETE_YES_DISPLAY_NAME("inventory.confirmMenu.waypoint.public.delete.yes.displayName"),
	INVENTORY_CONFIRM_MENU_WAYPOINT_PUBLIC_DELETE_YES_DESCRIPTION("inventory.confirmMenu.waypoint.public.delete.yes.description"),
	INVENTORY_CONFIRM_MENU_WAYPOINT_PUBLIC_DELETE_NO_DISPLAY_NAME("inventory.confirmMenu.waypoint.public.delete.no.displayName"),
	INVENTORY_CONFIRM_MENU_WAYPOINT_PUBLIC_DELETE_NO_DESCRIPTION("inventory.confirmMenu.waypoint.public.delete.no.description"),

	INVENTORY_CONFIRM_MENU_WAYPOINT_PERMISSION_DELETE_DESCRIPTION_DISPLAY_NAME("inventory.confirmMenu.waypoint.permission.delete.title.displayName"),
	INVENTORY_CONFIRM_MENU_WAYPOINT_PERMISSION_DELETE_DESCRIPTION_DESCRIPTION("inventory.confirmMenu.waypoint.permission.delete.title.description"),
	INVENTORY_CONFIRM_MENU_WAYPOINT_PERMISSION_DELETE_YES_DISPLAY_NAME("inventory.confirmMenu.waypoint.permission.delete.yes.displayName"),
	INVENTORY_CONFIRM_MENU_WAYPOINT_PERMISSION_DELETE_YES_DESCRIPTION("inventory.confirmMenu.waypoint.permission.delete.yes.description"),
	INVENTORY_CONFIRM_MENU_WAYPOINT_PERMISSION_DELETE_NO_DISPLAY_NAME("inventory.confirmMenu.waypoint.permission.delete.no.displayName"),
	INVENTORY_CONFIRM_MENU_WAYPOINT_PERMISSION_DELETE_NO_DESCRIPTION("inventory.confirmMenu.waypoint.permission.delete.no.description"),

	INVENTORY_CONFIRM_MENU_FOLDER_PRIVATE_DELETE_DESCRIPTION_DISPLAY_NAME("inventory.confirmMenu.folder.private.delete.title.displayName"),
	INVENTORY_CONFIRM_MENU_FOLDER_PRIVATE_DELETE_DESCRIPTION_DESCRIPTION("inventory.confirmMenu.folder.private.delete.title.description"),
	INVENTORY_CONFIRM_MENU_FOLDER_PRIVATE_DELETE_YES_DISPLAY_NAME("inventory.confirmMenu.folder.private.delete.yes.displayName"),
	INVENTORY_CONFIRM_MENU_FOLDER_PRIVATE_DELETE_YES_DESCRIPTION("inventory.confirmMenu.folder.private.delete.yes.description"),
	INVENTORY_CONFIRM_MENU_FOLDER_PRIVATE_DELETE_NO_DISPLAY_NAME("inventory.confirmMenu.folder.private.delete.no.displayName"),
	INVENTORY_CONFIRM_MENU_FOLDER_PRIVATE_DELETE_NO_DESCRIPTION("inventory.confirmMenu.folder.private.delete.no.description"),
	//endregion
	;

	private String path;

	Messages(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return path;
	}
}
