/*
 *     Waypoints2, A plugin for spigot to add waypoints functionality
 *     Copyright (C) 2019-2020 Lukas Planz
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

package de.md5lukas.waypoints.command;

import de.md5lukas.commons.UUIDUtils;
import de.md5lukas.waypoints.Waypoints;
import de.md5lukas.waypoints.data.waypoint.PermissionWaypoint;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import de.md5lukas.waypoints.display.WaypointDisplay;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static de.md5lukas.waypoints.Messages.*;

public class WaypointScriptCommand implements CommandExecutor {

    /*
     * /waypointscript clearWaypoint <player-name>
     * /waypointscript selectWaypoint <player-name> <waypoint-id>
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("waypoints.scripting")) {
            if (args.length == 0) {
                COMMAND_SCRIPTING_HELP_TITLE.send(sender);
                COMMAND_SCRIPTING_HELP_CLEAR_WAYPOINT.send(sender);
                COMMAND_SCRIPTING_HELP_SELECT_WAYPOINT.send(sender);
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "clearwaypoint":
                    if (args.length >= 2) {
                        clearWaypoint(sender, args[1]);
                    } else {
                        COMMAND_SCRIPTING_CLEAR_WAYPOINT_WRONG_USAGE.send(sender);
                    }
                    break;
                case "selectwaypoint":
                    if (args.length >= 3) {
                        selectWaypoint(sender, args[1], args[2]);
                    } else {
                        COMMAND_SCRIPTING_SELECT_WAYPOINT_WRONG_USAGE.send(sender);
                    }
                    break;
                default:
                    COMMAND_NOT_FOUND.send(sender);
                    break;
            }
        } else {
            GENERAL_NO_PERMISSION.send(sender);
        }
        return true;
    }

    private void clearWaypoint(CommandSender sender, String playerName) {
        Player p = Bukkit.getPlayer(playerName);
        if (p == null) {
            GENERAL_NOT_A_PLAYER.send(sender);
            return;
        }
        WaypointDisplay.getAll().disable(p);
    }

    private void selectWaypoint(CommandSender sender, String playerName, String waypointId) {
        Player p = Bukkit.getPlayer(playerName);
        if (p == null) {
            GENERAL_NOT_A_PLAYER.send(sender);
            return;
        }
        if (!UUIDUtils.isUUID(waypointId)) {
            GENERAL_NOT_A_VALID_UUID.send(sender);
            return;
        }
        UUID id = UUID.fromString(waypointId);
        Predicate<Waypoint> search = wp -> wp.getID().equals(id);
        Optional<Waypoint> result = Waypoints.getGlobalStore().getPublicFolder().findWaypoint(search);
        if (!result.isPresent()) {
            result = Waypoints.getGlobalStore().getPermissionFolder().findWaypoint(search);
            if (!result.isPresent()) {
                GENERAL_WAYPOINT_NOT_FOUND.send(sender);
                return;
            }
        }
        Waypoint wp = result.get();
        if (wp instanceof PermissionWaypoint && !p.hasPermission(((PermissionWaypoint) wp).getPermission())) {
            return;
        }
        WaypointDisplay.getAll().show(p, result.get());
    }
}
