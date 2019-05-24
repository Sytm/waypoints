package de.md5lukas.wp.command;

import de.md5lukas.wp.config.Config;
import de.md5lukas.wp.inventory.WaypointProvider;
import de.md5lukas.wp.storage.Waypoint;
import de.md5lukas.wp.storage.WaypointStorage;
import de.md5lukas.wp.util.StringHelper;
import de.md5lukas.wp.util.UUIDUtils;
import fr.minuskube.inv.SmartInventory;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static de.md5lukas.wp.config.Message.*;
import static de.md5lukas.wp.config.Messages.get;

public class WaypointCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
		if (commandSender instanceof Player) {
			if (commandSender.hasPermission("waypoints.command")) {
				Player player = (Player) commandSender;
				if (args.length == 0) {
					SmartInventory.builder().id("waypoints").provider(new WaypointProvider(player.getUniqueId()))
							.id("waypoints").title(get(INV_TITLE)).size(4, 9).build().open(player);
				} else {
					switch (args[0].toLowerCase()) {
						case "set":
						case "s":
						case "add":
						case "a":
							if (Config.maxWaypoints > 0 && WaypointStorage.getWaypoints(player.getUniqueId()).size() >= Config.maxWaypoints) {
								player.sendMessage(get(CMD_WP_ADD_MAXWAYPOINTS));
								break;
							}
							if (args.length < 2) {
								player.sendMessage(get(CMD_WP_ADD_WRONGUSAGE));
								break;
							}
							WaypointStorage.getWaypoints(player.getUniqueId()).add(
									new Waypoint(StringUtils.join(args, ' ', 1, args.length), player.getLocation()));
							player.sendMessage(get(CMD_WP_ADD_SUCCESS));
							break;
						case "addglobal":
						case "ag":
						case "setglobal":
						case "sg":
							if (player.hasPermission("waypoints.admin")) {
								if (args.length < 2) {
									player.sendMessage(get(CMD_WP_GLOBALADD_WRONGUSAGE));
									break;
								}
								WaypointStorage.getGlobalWaypoints().add(
										new Waypoint(StringUtils.join(args, ' ', 1, args.length), player.getLocation(), true));
								player.sendMessage(get(CMD_WP_GLOBALADD_SUCCESS));
							} else {
								player.sendMessage(get(NOPERMISSION));
							}
							break;
						case "addpermission":
						case "ap":
						case "setpermission":
						case "sp":
							if (player.hasPermission("waypoints.admin")) {
								String permission = args[1];
								String name = StringUtils.join(args, ' ', 2, args.length);
								WaypointStorage.getPermissionWaypoints().add(new Waypoint(name, permission, player.getLocation(), true));
								player.sendMessage(get(CMD_WP_PERMISSIONADD_SUCCESS));
							} else {
								player.sendMessage(get(NOPERMISSION));
							}
							if (args.length < 3) {
								player.sendMessage(get(CMD_WP_PERMISSIONADD_WRONGUSAGE));
								break;
							}
							break;
						case "adminother":
						case "ao":
							if (player.hasPermission("waypoints.admin")) {
								if (args.length < 2) {
									player.sendMessage(get(CMD_WP_ADMINOTHER_WRONGUSAGE));
									break;
								}
								UUID target;

								if (UUIDUtils.UUID_PATTERN.matcher(args[1]).matches()) {
									target = UUID.fromString(args[1]);
								} else if (StringHelper.MCUSERNAMEPATTERN.matcher(args[1]).matches()) {
									target = UUIDUtils.getUUID(args[1]);
									if (target == null) {
										player.sendMessage(get(PLAYERDOESNOTEXIST).replace("%name%", args[1]));
										break;
									}
								} else {
									player.sendMessage(get(CMD_WP_ADMINOTHER_NOTPLAYERUUID));
									break;
								}

								if (player.getUniqueId().equals(target)) {
									player.sendMessage(get(CMD_WP_ADMINOTHER_CANNOTADMINSELF));
									break;
								}

								if (WaypointStorage.getWaypoints(target).isEmpty()) {
									player.sendMessage(get(CMD_WP_ADMINOTHER_PLAYERNOWAYPOINTS));
									break;
								}

								SmartInventory.builder().id("waypoints").provider(new WaypointProvider(target))
										.id("waypoints").title(get(INV_TITLE_OTHER)
										.replace("%name%", UUIDUtils.getName(target))).size(4, 9).build().open(player);
							} else {
								player.sendMessage(get(NOPERMISSION));
							}

							break;
						case "defcompass":
							if (player.hasPermission("waypoints.admin")) {
								if (Config.saveCompassLocation(player.getLocation())) {
									player.sendMessage(get(CMD_WP_COMPASS_SUCCESS));
								} else {
									player.sendMessage(get(CMD_WP_COMPASS_ERROR));
								}
							} else {
								player.sendMessage(get(NOPERMISSION));
							}
							break;
						default:
							player.sendMessage(get(CMD_WP_HELP_TITLE));
							player.sendMessage(get(CMD_WP_HELP_INVENTORY));
							player.sendMessage(get(CMD_WP_HELP_ADD));
							if (player.hasPermission("waypoints.admin")) {
								player.sendMessage(get(CMD_WP_HELP_ADDGLOBALWAYPOINT));
								player.sendMessage(get(CMD_WP_HELP_ADDPERMISSIONWAYPOINT));
								player.sendMessage(get(CMD_WP_HELP_ADMINOTHER));
								player.sendMessage(get(CMD_WP_HELP_DEFCOMPASS));
							}
							break;
					}
				}
			} else {
				commandSender.sendMessage(get(NOPERMISSION));
			}
		} else {
			commandSender.sendMessage(get(NOTAPLAYER));
		}
		return true;
	}
}
