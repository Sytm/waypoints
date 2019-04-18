package de.md5lukas.wp.config;

public enum Message {

	PREFIX("prefix"), NOTAPLAYER("notaplayer"), NOPERMISSION("nopermission"),

	/* de.md5lukas.wp.command.WaypointCommand */
	CMD_WP_WRONGUSAGEADD("command.waypoints.add"),
	CMD_WP_HELP("command.waypoints.help"),
	CMD_WP_ADDSUCCESS("command.waypoints.add.success"),
	CMD_WP_MAXWAYPOINTS("command.waypoints.add.maxreached"),
	CMD_WP_COMPASS_SUCCESS("command.waypoints.compass.success"),
	CMD_WP_COMPASS_ERROR("command.waypoints.compass.error"),
	/* de.md5lukas.wp.inventory.WaypointProvider */
	INV_TITLE("inventory.title"),
	INV_EMPTYBACKGROUND("inventory.emptybackground"),
	INV_PREV_PAGE("inventory.previouspage"),
	INV_NEXT_PAGE("inventory.nextpage"),
	INV_DISABLE_WAYPOINTS("inventory.deselectwaypoints"),
	INV_WAYPOINT_LORE("inventory.waypoint.description"),//%world%, %x%, %y%, %z%, %distance%
	INV_WAYPOINT_NAME("inventory.waypoint.name"),
	INV_WAYPOINT_SELECT("inventory.waypoint.select"),
	INV_WAYPOINT_DELETE("inventory.waypoint.delete"),
	INV_BACK("inventory.back"),
	/* de.md5lukas.wp.PointerManager*/
	AB_WRONGWORLD("actionbar.incorrectworld"); // %currentworld%, %correctworld%

	private String inFilePath;

	Message(String inFilePath) {
		this.inFilePath = inFilePath;
	}

	public String getInFilePath() {
		return inFilePath;
	}
}
