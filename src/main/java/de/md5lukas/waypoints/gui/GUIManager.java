/*
 *     Waypoints2, A plugin for spigot to add waypoints functionality
 *     Copyright (C) 2020  Lukas Planz
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
import de.md5lukas.waypoints.Messages;
import fr.minuskube.inv.SmartInventory;
import org.bukkit.entity.Player;

import java.util.UUID;

import static de.md5lukas.waypoints.Messages.INVENTORY_TITLE_OTHER;
import static de.md5lukas.waypoints.Messages.INVENTORY_TITLE_OWN;

public class GUIManager {

    public static void openGUI(Player player) {
        if (player.hasPermission("waypoints.gui.open")) {
            SmartInventory.builder().id(player.getUniqueId().toString()).size(5, 9)
                    .provider(new WaypointProvider(player.getUniqueId())).title(INVENTORY_TITLE_OWN.getRaw(player)).build().open(player);
        } else {
            Messages.GENERAL_NO_PERMISSION.send(player);
        }
    }

    public static void openGUI(Player player, UUID target) {
        if (player.getUniqueId().equals(target)) {
            openGUI(player);
            return;
        }
        SmartInventory.builder().id(player.getUniqueId() + "|" + target).size(5, 9)
                .provider(new WaypointProvider(target)).title(INVENTORY_TITLE_OTHER.getRaw(player).replace("%name%", UUIDUtils.getName(target))).build().open(player);
    }

    static void openGUI(Player player, UUID target, WaypointProvider provider) {
        SmartInventory.Builder builder = SmartInventory.builder().size(5, 9).provider(provider);
        if (player.getUniqueId().equals(target)) {
            builder.id(player.getUniqueId().toString()).title(INVENTORY_TITLE_OWN.getRaw(player));
        } else {
            builder.id(player.getUniqueId() + "|" + target).title(INVENTORY_TITLE_OTHER.getRaw(player).replace("%name%", UUIDUtils.getName(target)));
        }
        builder.build().open(player);
    }
}
