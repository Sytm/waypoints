/*
 *     Waypoints
 *     Copyright (C) 2019  Lukas Planz
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

package de.md5lukas.wp.config;

@SuppressWarnings("ALL")
public enum Message {

	NOTAPLAYER("notaplayer"), NOPERMISSION("nopermission"), PLAYERDOESNOTEXIST("playerdoesnotexist"), // %name%

	// de.md5lukas.wp.command.WaypointCommand
	CMD_WP_HELP_TITLE("command.waypoints.help.title"),
	CMD_WP_HELP_INVENTORY("command.waypoints.help.inventory"),
	CMD_WP_HELP_ADD("command.waypoints.help.add"),
	CMD_WP_HELP_DEFCOMPASS("command.waypoints.help.defcompass"),
	CMD_WP_HELP_ADMINOTHER("command.waypoints.help.adminother"),
	CMD_WP_HELP_ADDGLOBALWAYPOINT("command.waypoints.help.globalwaypoint"),
	CMD_WP_HELP_ADDPERMISSIONWAYPOINT("command.waypoints.help.permissionwaypoint"),
	// add
	CMD_WP_ADD_WRONGUSAGE("command.waypoints.add.wrongusage"),
	CMD_WP_ADD_SUCCESS("command.waypoints.add.success"),
	CMD_WP_ADD_MAXWAYPOINTS("command.waypoints.add.maxreached"),
	// defcompass
	CMD_WP_COMPASS_SUCCESS("command.waypoints.compass.success"),
	CMD_WP_COMPASS_ERROR("command.waypoints.compass.error"),
	// add global
	CMD_WP_GLOBALADD_WRONGUSAGE("command.waypoints.globaladd.wrongusage"),
	CMD_WP_GLOBALADD_SUCCESS("command.waypoints.globaladd.success"),
	// add permission
	CMD_WP_PERMISSIONADD_WRONGUSAGE("command.waypoints.permissionadd.wrongusage"),
	CMD_WP_PERMISSIONADD_SUCCESS("command.waypoints.permissionadd.success"),
	// admin other
	CMD_WP_ADMINOTHER_WRONGUSAGE("command.waypoints.adminother.wrongusage"),
	CMD_WP_ADMINOTHER_NOTPLAYERUUID("command.waypoints.adminother.neitheruuidorplayername"),
	CMD_WP_ADMINOTHER_CANNOTADMINSELF("command.waypoints.adminother.cannotadminself"),
	CMD_WP_ADMINOTHER_PLAYERNOWAYPOINTS("command.waypoints.adminother.playerhasnowaypoints"),

	// de.md5lukas.wp.inventory.WaypointProvider
	INV_TITLE("inventory.title"),
	INV_TITLE_OTHER("inventory.title.other"),
	INV_EMPTYBACKGROUND("inventory.emptybackground"),
	INV_PREV_PAGE("inventory.previouspage"),
	INV_NEXT_PAGE("inventory.nextpage"),
	INV_DISABLE_WAYPOINTS("inventory.deselectwaypoints"),
	INV_WAYPOINT_LORE("inventory.waypoint.description"),//%world%, %x%, %y%, %z%, %distance%
	INV_WAYPOINT_NAME("inventory.waypoint.name"),
	INV_WAYPOINT_NAME_GLOBAL("inventory.waypoint.name.global"),
	INV_WAYPOINT_NAME_PERMISSION("inventory.waypoint.name.permission"),
	INV_WAYPOINT_SELECT("inventory.waypoint.select"),
	INV_WAYPOINT_DELETE("inventory.waypoint.delete"),
	INV_WAYPOINT_TELEPORT("inventory.waypoint.teleport"),
	INV_BACK("inventory.back"),

	// de.md5lukas.wp.PointerManager
	AB_WRONGWORLD("actionbar.incorrectworld"), // %currentworld%, %correctworld%
	AB_WRONGWORLD_SHORT("actionbar.incorrectworld.short"),
	AB_CROUCH_TOHIGH("actionbar.crouch.tohigh"), // %amount%
	AB_CROUCH_TOHIGH_SINGLE("actionbar.crouch.tohigh.single"),
	AB_CROUCH_TOLOW("actionbar.crouch.tolow"), // %amount%
	AB_CROUCH_TOLOW_SINGLE("actionbar.crouch.tolow.single"),
	AB_CROUCH_DIFFUNDERONE("actionbar.crouch.near"),
	AB_CROUCH_TEMPLATE("actionbar.crouch.template"); // %distance3d%, %distance2d%, %yoffset%,

	private String inFilePath;

	Message(String inFilePath) {
		this.inFilePath = inFilePath;
	}

	public String getInFilePath() {
		return inFilePath;
	}
}
