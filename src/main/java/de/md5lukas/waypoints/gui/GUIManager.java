/*
 *     Waypoints2, A plugin for spigot to add waypoints functionality
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

package de.md5lukas.waypoints.gui;

import de.md5lukas.commons.UUIDUtils;
import fr.minuskube.inv.SmartInventory;
import org.bukkit.entity.Player;

import java.util.UUID;

import static de.md5lukas.waypoints.Messages.INVENTORY_TITLE_OTHER;
import static de.md5lukas.waypoints.Messages.INVENTORY_TITLE_OWN;

public class GUIManager {

	public static void openGUI(Player player) {
		SmartInventory.builder().id(player.getUniqueId().toString()).size(5, 9)
			.provider(new WaypointProvider(player.getUniqueId())).title(INVENTORY_TITLE_OWN.getRaw(player)).build().open(player);
	}

	public static void openGUI(Player player, UUID target) {
		if (player.getUniqueId().equals(target)) {
			openGUI(player);
			return;
		}
		SmartInventory.builder().id(player.getUniqueId() + "|" + target).size(5, 9)
			.provider(new WaypointProvider(target)).title(INVENTORY_TITLE_OTHER.getRaw(player).replace("%name%", UUIDUtils.getName(target))).build().open(player);
	}
}
