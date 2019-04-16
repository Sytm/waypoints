package de.md5lukas.wp.command;

import de.md5lukas.wp.inventory.WaypointProvider;
import de.md5lukas.wp.storage.Waypoint;
import de.md5lukas.wp.storage.WaypointStorage;
import de.md5lukas.wp.config.Messages;
import fr.minuskube.inv.SmartInventory;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WaypointCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
		if (commandSender instanceof Player) {
			if (commandSender.hasPermission("waypoints.command")) {
				Player player = (Player) commandSender;
				if (args.length == 0) {
					SmartInventory.builder().id("waypoints").provider(new WaypointProvider()).size(4, 9).build().open(player);
				} else {
					switch (args[0].toLowerCase()) {
						case "set":
						case "add":
							if (args.length < 2) {
								player.sendMessage(Messages.PREFIX + "§cDu musst einen Namen für den Waypoint nennen! Beispiel: §7/waypoints add Mein' Zuhause");
								break;
							}
							WaypointStorage.getWaypoints(player.getUniqueId()).add(
									new Waypoint(StringUtils.join(args, ' ', 1, args.length), player.getLocation()));
							player.sendMessage(Messages.PREFIX + "§7Waypoint erfolgreich hinzugefügt!");
							break;
						default:
							player.sendMessage(Messages.PREFIX + "§7Folgende Befehle sind verfügbar:");
							player.sendMessage("§7 - §e/waypoints§7 Öffnet das Inventar");
							player.sendMessage("§7 - §e/waypoints add <Name> §7Fügt einen neuen Waypoint hinzu");
							break;
					}
				}
			} else {
				commandSender.sendMessage(Messages.NOPERMISSION);
			}
		} else {
			commandSender.sendMessage(Messages.NOTAPLAYER);
		}
		return true;
	}
}
