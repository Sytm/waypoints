package de.md5lukas.wp.command;

import de.md5lukas.wp.config.Config;
import de.md5lukas.wp.inventory.WaypointProvider;
import de.md5lukas.wp.storage.Waypoint;
import de.md5lukas.wp.storage.WaypointStorage;
import fr.minuskube.inv.SmartInventory;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static de.md5lukas.wp.config.Message.*;
import static de.md5lukas.wp.config.Messages.get;

public class WaypointCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
		if (commandSender instanceof Player) {
			if (commandSender.hasPermission("waypoints.command")) {
				Player player = (Player) commandSender;
				if (args.length == 0) {
					SmartInventory.builder().id("waypoints").provider(new WaypointProvider()).title(get(INV_TITLE)).size(4, 9).build().open(player);
				} else {
					switch (args[0].toLowerCase()) {
						case "set":
						case "add":
							if (Config.maxWaypoints > 0 && WaypointStorage.getWaypoints(player.getUniqueId()).size() >= Config.maxWaypoints) {
								player.sendMessage(get(CMD_WP_MAXWAYPOINTS));
								break;
							}
							if (args.length < 2) {
								player.sendMessage(get(CMD_WP_WRONGUSAGEADD));
								break;
							}
							WaypointStorage.getWaypoints(player.getUniqueId()).add(
									new Waypoint(StringUtils.join(args, ' ', 1, args.length), player.getLocation()));
							player.sendMessage(get(CMD_WP_ADDSUCCESS));
							break;
						default:
							player.sendMessage(get(CMD_WP_HELP));
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
