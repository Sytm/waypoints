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
