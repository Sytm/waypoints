package de.md5lukas.wp.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WaypointTabcompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> result = new ArrayList<>();
		if (!(sender instanceof Player))
			return result;
		if (sender.hasPermission("waypoints.command")) {
			switch (args.length) {
				case 0:
					result.add("add");
					if (sender.hasPermission("waypoints.admin")) {
						result.add("defCompass");
						result.add("addGlobal");
						result.add("addPermission");
						result.add("adminother");
					}
					break;
				case 1:
					StringUtil.copyPartialMatches(args[0], Collections.singletonList("add"), result);
					if (sender.hasPermission("waypoints.admin")) {
						StringUtil.copyPartialMatches(args[0], Arrays.asList("defCompass", "addGlobal", "addPermission", "adminother"), result);
					}
					break;
				case 2:
					switch (args[0].toLowerCase()) {
						case "adminother":
						case "ao":
							StringUtil.copyPartialMatches(args[1], onlinePlayers((Player) sender), result);
							break;
						default:
							break;
					}
					break;
				default:
					break;
			}
		}
		return result;
	}

	private List<String> onlinePlayers(Player player) {
		return Bukkit.getOnlinePlayers().stream().filter(p -> !player.getUniqueId().equals(p.getUniqueId())).map(Player::getDisplayName).collect(Collectors.toList());
	}
}
