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

package de.md5lukas.waypoints.command;

import de.md5lukas.commons.StringHelper;
import de.md5lukas.commons.UUIDUtils;
import de.md5lukas.waypoints.Waypoints;
import de.md5lukas.waypoints.data.WPPlayerData;
import de.md5lukas.waypoints.data.folder.Folder;
import de.md5lukas.waypoints.data.waypoint.PermissionWaypoint;
import de.md5lukas.waypoints.data.waypoint.PrivateWaypoint;
import de.md5lukas.waypoints.data.waypoint.PublicWaypoint;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import de.md5lukas.waypoints.gui.GUIManager;
import de.md5lukas.waypoints.store.WPConfig;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

import static de.md5lukas.waypoints.Messages.*;
import static de.md5lukas.waypoints.Waypoints.message;

public class WaypointsCommand implements CommandExecutor {

	/*
	 * /wp - GUI
	 * /wp help - Help
	 * /wp set <Name> - Private
	 * /wp setPublic <name> - Public
	 * /wp setPermission <permission> <name> - Compass
	 * /wp createFolder <Name>
	 * /wp compass - Compass
	 * /wp other <UUID|Name>
	 * /wp updateItem <waypoint|folder> <ID>
	 * /wp rename <privatewaypoint|publicwaypoint|permissionwaypoint|folder> <ID> <NewName>
	 *
	 * */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (!p.hasPermission("waypoints.command")) {
				p.sendMessage(message(GENERAL_NO_PERMISSION, p));
				return true;
			}
			if (args.length == 0) {
				GUIManager.openGUI(p);
				return true;
			}
			switch (args[0].toLowerCase()) {
				case "help": {
					if (!p.hasPermission("waypoints.help")) {
						p.sendMessage(message(GENERAL_NO_PERMISSION, p));
						return true;
					}
					p.sendMessage(message(COMMAND_HELP_TITLE, p));
					p.sendMessage(message(COMMAND_HELP_HELP, p));
					if (p.hasPermission("waypoints.set.private"))
						p.sendMessage(message(COMMAND_HELP_SET_PRIVATE, p));
					if (p.hasPermission("waypoints.set.public"))
						p.sendMessage(message(COMMAND_HELP_SET_PUBLIC, p));
					if (p.hasPermission("waypoints.set.public"))
						p.sendMessage(message(COMMAND_HELP_SET_PERMISSION, p));
					if (p.hasPermission("waypoints.compass"))
						p.sendMessage(message(COMMAND_HELP_COMPASS, p));
					if (p.hasPermission("waypoints.other"))
						p.sendMessage(message(COMMAND_HELP_OTHER, p));
					if (WPConfig.inventory().isCustomItemEnabled())
						p.sendMessage(message(COMMAND_HELP_UPDATE_ITEM, p));
					if ((WPConfig.allowRenamingWaypointsPrivate() || WPConfig.allowRenamingWaypointsPublic()
						|| WPConfig.allowRenamingWaypointsPermission()) && WPConfig.allowRenamingFoldersPrivate())
						p.sendMessage(message(COMMAND_HELP_RENAME_NORMAL, p));
					else if (WPConfig.allowRenamingWaypointsPrivate() || WPConfig.allowRenamingWaypointsPublic() || WPConfig.allowRenamingWaypointsPermission())
						p.sendMessage(message(COMMAND_HELP_RENAME_WAYPOINT_ONLY, p));
					else if (WPConfig.allowRenamingFoldersPrivate())
						p.sendMessage(message(COMMAND_HELP_RENAME_FOLDER_ONLY, p));
					break;
				}
				case "compass": {
					if (!p.hasPermission("waypoints.compass")) {
						p.sendMessage(message(GENERAL_NO_PERMISSION, p));
						return true;
					}
					switch (WPConfig.displays().getCompassDefaultLocationType()) {
						case INGAME:
							Waypoints.getGlobalStore().setCompassTarget(p.getLocation());
							Waypoints.getGlobalStore().save(true);
							p.sendMessage(message(COMMAND_COMPASS_SET_SUCCESS, p));
							break;
						case INGAME_LOCK:
							p.sendMessage(message(COMMAND_COMPASS_LOCKED, p));
							break;
						default:
							p.sendMessage(message(COMMAND_COMPASS_DISABLED, p));
							break;
					}
					break;
				}
				case "set": {
					if (!p.hasPermission("waypoints.set.private")) {
						p.sendMessage(message(GENERAL_NO_PERMISSION, p));
						return true;
					}
					if (args.length <= 1) {
						p.sendMessage(message(COMMAND_SET_PRIVATE_WRONG_USAGE, p));
						return true;
					}
					WPPlayerData data = WPPlayerData.getPlayerData(p.getUniqueId());
					String name = StringHelper.buildStringFromArray(args, 1);
					if (!WPConfig.allowDuplicateWaypointNamesPrivate()) {
						if (data.findWaypoint(wp -> wp.getName().equalsIgnoreCase(name)).isPresent()) {
							p.sendMessage(message(COMMAND_SET_PRIVATE_NAME_DUPLICATE, p));
							return true;
						}
					}
					if (WPConfig.getWaypointLimit() > 0) {
						if (data.countWaypoints() >= WPConfig.getWaypointLimit()) {
							p.sendMessage(message(COMMAND_SET_PRIVATE_LIMIT_REACHED, p));
							return true;
						}
					}
					data.addWaypoint(new PrivateWaypoint(name, p.getLocation()));
					p.sendMessage(message(COMMAND_SET_PRIVATE_SUCCESS, p));
					break;
				}
				case "setpub":
				case "setpublic": {
					if (!p.hasPermission("waypoints.set.public")) {
						p.sendMessage(message(GENERAL_NO_PERMISSION, p));
						return true;
					}
					if (args.length <= 1) {
						p.sendMessage(message(COMMAND_SET_PUBLIC_WRONG_USAGE, p));
						return true;
					}
					String name = StringHelper.buildStringFromArray(args, 1);
					if (!WPConfig.allowDuplicateWaypointNamesPublic()) {
						for (Waypoint wp : Waypoints.getGlobalStore().getPublicFolder().getWaypoints(p)) {
							if (wp.getName().equalsIgnoreCase(name)) {
								p.sendMessage(message(COMMAND_SET_PUBLIC_NAME_DUPLICATE, p));
								return true;
							}
						}
					}
					Waypoints.getGlobalStore().getPublicFolder().addWaypoint(new PublicWaypoint(name, p.getLocation()));
					p.sendMessage(message(COMMAND_SET_PUBLIC_SUCCESS, p));
					break;
				}
				case "setperm":
				case "setpermission": {
					if (!p.hasPermission("waypoints.set.permission")) {
						p.sendMessage(message(GENERAL_NO_PERMISSION, p));
						return true;
					}
					if (args.length <= 2) {
						p.sendMessage(message(COMMAND_SET_PERMISSION_WRONG_USAGE, p));
						return true;
					}
					String permission = args[1];
					String name = StringHelper.buildStringFromArray(args, 2);
					if (!WPConfig.allowDuplicateWaypointNamesPermission()) {
						for (Waypoint wp : Waypoints.getGlobalStore().getPermissionFolder().getWaypoints(p)) {
							if (wp.getName().equalsIgnoreCase(name)) {
								p.sendMessage(message(COMMAND_SET_PERMISSION_NAME_DUPLICATE, p));
								return true;
							}
						}
					}
					Waypoints.getGlobalStore().getPermissionFolder().addWaypoint(new PermissionWaypoint(name, p.getLocation(), permission));
					p.sendMessage(message(COMMAND_SET_PERMISSION_SUCCESS, p));
					break;
				}
				case "cf":
				case "createfolder": {
					if (args.length <= 1) {
						p.sendMessage(message(COMMAND_CREATE_FOLDER_WRONG_USAGE, p));
						return true;
					}
					WPPlayerData data = WPPlayerData.getPlayerData(p.getUniqueId());
					String name = StringHelper.buildStringFromArray(args, 1);
					if (!WPConfig.allowDuplicateFolderPrivateNames()) {
						if (data.findFolder(f -> f.getName().equalsIgnoreCase(name)).isPresent()) {
							p.sendMessage(message(COMMAND_CREATE_FOLDER_NAME_DUPLICATE, p));
							return true;
						}
					}
					data.addFolder(name);
					p.sendMessage(message(COMMAND_CREATE_FOLDER_SUCCESS, p));
					break;
				}
				case "other": {
					if (args.length <= 1) {
						p.sendMessage(message(COMMAND_OTHER_WRONG_USAGE, p));
						return true;
					}
					if (StringHelper.MC_USERNAME_PATTERN.matcher(args[2]).matches()) {
						UUIDUtils.getUUID(args[2], uuid -> {
							if (uuid == null) {
								p.sendMessage(message(COMMAND_OTHER_PLAYER_NAME_NOT_FOUND, p));
								return;
							}
							GUIManager.openGUI(p, uuid);
						});
					} else if (UUIDUtils.isUUID(args[2])) {
						UUID uuid = UUID.fromString(args[2]);
						UUIDUtils.getName(uuid, name -> {
							if (name == null) {
								p.sendMessage(message(COMMAND_OTHER_UUID_NOT_FOUND, p));
								return;
							}
							GUIManager.openGUI(p, uuid);
						});
					} else {
						p.sendMessage(message(COMMAND_OTHER_NOT_A_VALID_UUID_OR_PLAYER, p));
						return true;
					}
					break;
				}
				case "updateitem": {
					if (!WPConfig.inventory().isCustomItemEnabled()) {
						p.sendMessage(message(COMMAND_UPDATE_ITEM_DISABLED, p));
						return true;
					}
					if (args.length <= 2) {
						p.sendMessage(message(COMMAND_UPDATE_ITEM_WRONG_USAGE, p));
						return true;
					}
					if (!UUIDUtils.isUUID(args[2])) {
						p.sendMessage(message(GENERAL_NOT_A_VALID_UUID, p));
						return true;
					}
					Material mat = p.getInventory().getItemInMainHand().getType();
					UUID uuid = UUID.fromString(args[2]);
					if (!WPConfig.inventory().isValidCustomItem(mat)) {
						p.sendMessage(message(COMMAND_UPDATE_ITEM_NOT_A_VALID_ITEM, p));
						return true;
					}
					if ("waypoint".equalsIgnoreCase(args[1])) {
						WPPlayerData playerData = WPPlayerData.getPlayerData(p.getUniqueId());
						Optional<Waypoint> waypoint = playerData.findWaypoint(wp -> wp.getID().equals(uuid));
						if (waypoint.isPresent()) {
							waypoint.get().setMaterial(mat);
							p.sendMessage(message(COMMAND_UPDATE_ITEM_WAYPOINT_SUCCESS, p));
						} else {
							p.sendMessage(message(GENERAL_WAYPOINT_NOT_FOUND, p));
						}
					} else if ("folder".equalsIgnoreCase(args[1])) {
						WPPlayerData playerData = WPPlayerData.getPlayerData(p.getUniqueId());
						Optional<Folder> folder = playerData.findFolder(f -> f.getID().equals(uuid));
						if (folder.isPresent()) {
							folder.get().setMaterial(mat);
							p.sendMessage(message(COMMAND_UPDATE_ITEM_FOLDER_SUCCESS, p));
						} else {
							p.sendMessage(message(GENERAL_FOLDER_NOT_FOUND, p));
						}
					} else {
						p.sendMessage(message(COMMAND_UPDATE_ITEM_WRONG_USAGE, p));
					}
					break;
				}
				case "rename": {
					if (!WPConfig.allowRenamingWaypointsPrivate() && !WPConfig.allowRenamingWaypointsPublic()
						&& !WPConfig.allowRenamingWaypointsPermission() && !WPConfig.allowRenamingFoldersPrivate()) {
						p.sendMessage(message(COMMAND_RENAME_DISABLED, p));
						return true;
					}
					if (args.length <= 3) {
						p.sendMessage(message(COMMAND_RENAME_WRONG_USAGE, p));
						return true;
					}
					if (!UUIDUtils.isUUID(args[2])) {
						p.sendMessage(message(GENERAL_NOT_A_VALID_UUID, p));
						return true;
					}
					UUID uuid = UUID.fromString(args[2]);
					String newName = StringHelper.buildStringFromArray(args, 3);
					switch (args[1].toLowerCase()) {
						case "privatewaypoint": {
							if (!WPConfig.allowRenamingWaypointsPrivate()) {
								p.sendMessage(message(COMMAND_RENAME_WAYPOINT_PRIVATE_DISABLED, p));
								return true;
							}
							WPPlayerData data = WPPlayerData.getPlayerData(p.getUniqueId());
							if (!WPConfig.allowDuplicateWaypointNamesPrivate()) {
								if (data.findWaypoint(wp -> newName.equalsIgnoreCase(wp.getName())).isPresent()) {
									p.sendMessage(message(COMMAND_RENAME_WAYPOINT_PRIVATE_NAME_DUPLICATE, p));
									return true;
								}
							}
							Optional<Waypoint> waypoint = data.findWaypoint(wp -> uuid.equals(wp.getID()));
							if (!waypoint.isPresent()) {
								p.sendMessage(message(GENERAL_WAYPOINT_NOT_FOUND, p));
								return true;
							}
							waypoint.get().setName(newName);
							p.sendMessage(message(COMMAND_RENAME_WAYPOINT_SUCCESS, p));
							break;
						}
						case "publicwaypoint": {
							if (!WPConfig.allowRenamingWaypointsPublic()) {
								p.sendMessage(message(COMMAND_RENAME_WAYPOINT_PUBLIC_DISABLED, p));
								return true;
							}
							if (!WPConfig.allowDuplicateWaypointNamesPublic()) {
								if (Waypoints.getGlobalStore().getPublicFolder().findWaypoint(wp -> newName.equalsIgnoreCase(wp.getName())).isPresent()) {
									p.sendMessage(message(COMMAND_RENAME_WAYPOINT_PUBLIC_NAME_DUPLICATE, p));
									return true;
								}
							}
							Optional<Waypoint> waypoint = Waypoints.getGlobalStore().getPublicFolder().findWaypoint(wp -> uuid.equals(wp.getID()));
							if (!waypoint.isPresent()) {
								p.sendMessage(message(GENERAL_WAYPOINT_NOT_FOUND, p));
								return true;
							}
							waypoint.get().setName(newName);
							p.sendMessage(message(COMMAND_RENAME_WAYPOINT_SUCCESS, p));
							break;
						}
						case "permissionwaypoint": {
							if (!WPConfig.allowRenamingWaypointsPermission()) {
								p.sendMessage(message(COMMAND_RENAME_WAYPOINT_PERMISSION_DISABLED, p));
								return true;
							}
							if (!WPConfig.allowDuplicateWaypointNamesPermission()) {
								if (Waypoints.getGlobalStore().getPermissionFolder().findWaypoint(wp -> newName.equalsIgnoreCase(wp.getName())).isPresent()) {
									p.sendMessage(message(COMMAND_RENAME_WAYPOINT_PERMISSION_NAME_DUPLICATE, p));
									return true;
								}
							}
							Optional<Waypoint> waypoint = Waypoints.getGlobalStore().getPermissionFolder().findWaypoint(wp -> uuid.equals(wp.getID()));
							if (!waypoint.isPresent()) {
								p.sendMessage(message(GENERAL_WAYPOINT_NOT_FOUND, p));
								return true;
							}
							waypoint.get().setName(newName);
							p.sendMessage(message(COMMAND_RENAME_WAYPOINT_SUCCESS, p));
							break;
						}
						case "folder": {
							WPPlayerData data = WPPlayerData.getPlayerData(p.getUniqueId());
							if (!WPConfig.allowDuplicateFolderPrivateNames()) {
								if (data.findFolder(f -> f.getName().equalsIgnoreCase(newName)).isPresent()) {
									p.sendMessage(message(COMMAND_RENAME_FOLDER_NAME_DUPLICATE, p));
									return true;
								}
							}
							Optional<Folder> folder = data.findFolder(f -> f.getID().equals(uuid));
							if (!folder.isPresent()) {
								p.sendMessage(message(GENERAL_FOLDER_NOT_FOUND, p));
								return true;
							}
							folder.get().setName(newName);
							p.sendMessage(message(COMMAND_RENAME_FOLDER_SUCCESS, p));
							break;
						}
						default:
							p.sendMessage(message(COMMAND_RENAME_WRONG_USAGE, p));
							break;
					}
					break;
				}
				default:
					p.sendMessage(message(COMMAND_NOT_FOUND, p));
					break;
			}
		} else {
			sender.sendMessage(message(GENERAL_NOT_A_PLAYER, sender));
		}
		return true;
	}
}
