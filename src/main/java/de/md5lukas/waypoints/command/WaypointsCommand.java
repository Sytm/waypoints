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

package de.md5lukas.waypoints.command;

import de.md5lukas.commons.StringHelper;
import de.md5lukas.commons.UUIDUtils;
import de.md5lukas.waypoints.Waypoints;
import de.md5lukas.waypoints.config.WPConfig;
import de.md5lukas.waypoints.data.WPPlayerData;
import de.md5lukas.waypoints.data.folder.Folder;
import de.md5lukas.waypoints.data.folder.PermissionFolder;
import de.md5lukas.waypoints.data.folder.PublicFolder;
import de.md5lukas.waypoints.data.waypoint.PermissionWaypoint;
import de.md5lukas.waypoints.data.waypoint.PrivateWaypoint;
import de.md5lukas.waypoints.data.waypoint.PublicWaypoint;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import de.md5lukas.waypoints.gui.GUIManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

import static de.md5lukas.waypoints.Waypoints.getTranslations;

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
                getTranslations().GENERAL_NO_PERMISSION.send(p);
                return true;
            }
            if (args.length == 0) {
                GUIManager.openGUI(p);
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "help": {
                    if (!p.hasPermission("waypoints.help")) {
                        getTranslations().GENERAL_NO_PERMISSION.send(p);
                        return true;
                    }
                    getTranslations().COMMAND_HELP_TITLE.send(p);
                    getTranslations().COMMAND_HELP_HELP.send(p);
                    if (p.hasPermission("waypoints.set.private"))
                        getTranslations().COMMAND_HELP_SET_PRIVATE.send(p);
                    if (p.hasPermission("waypoints.set.public"))
                        getTranslations().COMMAND_HELP_SET_PUBLIC.send(p);
                    if (p.hasPermission("waypoints.set.public"))
                        getTranslations().COMMAND_HELP_SET_PERMISSION.send(p);
                    if (p.hasPermission("waypoints.compass"))
                        getTranslations().COMMAND_HELP_COMPASS.send(p);
                    if (p.hasPermission("waypoints.other"))
                        getTranslations().COMMAND_HELP_OTHER.send(p);
                    if (WPConfig.getGeneralConfig().isCustomItemEnabled())
                        getTranslations().COMMAND_HELP_UPDATE_ITEM.send(p);
                    break;
                }
                case "compass": {
                    if (!p.hasPermission("waypoints.compass")) {
                        getTranslations().GENERAL_NO_PERMISSION.send(p);
                        return true;
                    }
                    switch (WPConfig.getDisplayConfig().getCompassConfig().getDefaultLocationType()) {
                        case INGAME:
                            Waypoints.getGlobalStore().setCompassTarget(p.getLocation());
                            Waypoints.getGlobalStore().save(true);
                            getTranslations().COMMAND_COMPASS_SET_SUCCESS.send(p);
                            break;
                        case INGAME_LOCK:
                            getTranslations().COMMAND_COMPASS_LOCKED.send(p);
                            break;
                        default:
                            getTranslations().COMMAND_COMPASS_DISABLED.send(p);
                            break;
                    }
                    break;
                }
                case "set": {
                    if (!p.hasPermission("waypoints.set.private")) {
                        getTranslations().GENERAL_NO_PERMISSION.send(p);
                        return true;
                    }
                    if (args.length <= 1) {
                        getTranslations().COMMAND_SET_PRIVATE_WRONG_USAGE.send(p);
                        return true;
                    }
                    String name = StringHelper.buildStringFromArray(args, 1);
                    setPrivateWaypoint(p, name);
                    break;
                }
                case "setpub":
                case "setpublic": {
                    if (!p.hasPermission("waypoints.set.public")) {
                        getTranslations().GENERAL_NO_PERMISSION.send(p);
                        return true;
                    }
                    if (args.length <= 1) {
                        getTranslations().COMMAND_SET_PUBLIC_WRONG_USAGE.send(p);
                        return true;
                    }
                    String name = StringHelper.buildStringFromArray(args, 1);
                    setPublicWaypoint(p, name);
                    break;
                }
                case "setperm":
                case "setpermission": {
                    if (!p.hasPermission("waypoints.set.permission")) {
                        getTranslations().GENERAL_NO_PERMISSION.send(p);
                        return true;
                    }
                    if (args.length <= 2) {
                        getTranslations().COMMAND_SET_PERMISSION_WRONG_USAGE.send(p);
                        return true;
                    }
                    String permission = args[1];
                    String name = StringHelper.buildStringFromArray(args, 2);
                    setPermissionWaypoint(p, permission, name);
                    break;
                }
                case "cf":
                case "createfolder": {
                    if (args.length <= 1) {
                        getTranslations().COMMAND_CREATE_FOLDER_WRONG_USAGE.send(p);
                        return true;
                    }
                    String name = StringHelper.buildStringFromArray(args, 1);
                    createFolder(p, name);
                    break;
                }
                case "other": {
                    if (args.length <= 1) {
                        getTranslations().COMMAND_OTHER_WRONG_USAGE.send(p);
                        return true;
                    }
                    if (StringHelper.MC_USERNAME_PATTERN.matcher(args[1]).matches()) {
                        UUIDUtils.getUUID(args[1], uuid -> {
                            if (uuid == null) {
                                getTranslations().COMMAND_OTHER_PLAYER_NAME_NOT_FOUND.send(p);
                                return;
                            }
                            GUIManager.openGUI(p, uuid);
                        });
                    } else if (UUIDUtils.isUUID(args[1])) {
                        UUID uuid = UUID.fromString(args[1]);
                        UUIDUtils.getName(uuid, name -> {
                            if (name == null) {
                                getTranslations().COMMAND_OTHER_UUID_NOT_FOUND.send(p);
                                return;
                            }
                            GUIManager.openGUI(p, uuid);
                        });
                    } else {
                        getTranslations().COMMAND_OTHER_NOT_A_VALID_UUID_OR_PLAYER.send(p);
                        return true;
                    }
                    break;
                }
                case "updateitem": {
                    if (!WPConfig.getGeneralConfig().isCustomItemEnabled()) {
                        getTranslations().COMMAND_UPDATE_ITEM_DISABLED.send(p);
                        return true;
                    }
                    if (args.length <= 2) {
                        getTranslations().COMMAND_UPDATE_ITEM_WRONG_USAGE.send(p);
                        return true;
                    }
                    if (!UUIDUtils.isUUID(args[2])) {
                        getTranslations().GENERAL_NOT_A_VALID_UUID.send(p);
                        return true;
                    }
                    Material mat = p.getInventory().getItemInMainHand().getType();
                    UUID uuid = UUID.fromString(args[2]);
                    if (!WPConfig.getGeneralConfig().isValidCustomItem(mat)) {
                        getTranslations().COMMAND_UPDATE_ITEM_NOT_A_VALID_ITEM.send(p);
                        return true;
                    }
                    switch (args[1].toLowerCase()) {
                        case "waypointprivate": {
                            WPPlayerData playerData = WPPlayerData.getPlayerData(p.getUniqueId());
                            Optional<Waypoint> waypoint = playerData.findWaypoint(wp -> wp.getID().equals(uuid));
                            if (waypoint.isPresent()) {
                                waypoint.get().setMaterial(mat);
                                getTranslations().COMMAND_UPDATE_ITEM_WAYPOINT_SUCCESS.send(p);
                            } else {
                                getTranslations().GENERAL_WAYPOINT_NOT_FOUND.send(p);
                            }
                            break;
                        }
                        case "waypointpublic": {
                            PublicFolder pf = Waypoints.getGlobalStore().getPublicFolder();
                            Optional<Waypoint> waypoint = pf.findWaypoint(wp -> wp.getID().equals(uuid));
                            if (waypoint.isPresent()) {
                                waypoint.get().setMaterial(mat);
                                getTranslations().COMMAND_UPDATE_ITEM_WAYPOINT_SUCCESS.send(p);
                            } else {
                                getTranslations().GENERAL_WAYPOINT_NOT_FOUND.send(p);
                            }
                            break;
                        }
                        case "waypointpermission": {
                            PermissionFolder pf = Waypoints.getGlobalStore().getPermissionFolder();
                            Optional<Waypoint> waypoint = pf.findWaypoint(wp -> wp.getID().equals(uuid));
                            if (waypoint.isPresent()) {
                                waypoint.get().setMaterial(mat);
                                getTranslations().COMMAND_UPDATE_ITEM_WAYPOINT_SUCCESS.send(p);
                            } else {
                                getTranslations().GENERAL_WAYPOINT_NOT_FOUND.send(p);
                            }
                            break;
                        }
                        case "folder": {
                            WPPlayerData playerData = WPPlayerData.getPlayerData(p.getUniqueId());
                            Optional<Folder> folder = playerData.findFolder(f -> f.getID().equals(uuid));
                            if (folder.isPresent()) {
                                folder.get().setMaterial(mat);
                                getTranslations().COMMAND_UPDATE_ITEM_FOLDER_SUCCESS.send(p);
                            } else {
                                getTranslations().GENERAL_FOLDER_NOT_FOUND.send(p);
                            }
                            break;
                        }
                        default: {
                            getTranslations().COMMAND_UPDATE_ITEM_WRONG_USAGE.send(p);
                            break;
                        }
                    }
                    break;
                }
                case "rename": {
                    if (!WPConfig.getGeneralConfig().getRenamingConfig().isAllowRenamingWaypointsPrivate() && !WPConfig.getGeneralConfig().getRenamingConfig()
                            .isAllowRenamingWaypointsPublic()
                            && !WPConfig.getGeneralConfig().getRenamingConfig().isAllowRenamingWaypointsPermission() && !WPConfig.getGeneralConfig()
                            .getRenamingConfig().isAllowRenamingFoldersPrivate()) {
                        getTranslations().COMMAND_RENAME_DISABLED.send(p);
                        return true;
                    }
                    if (args.length <= 3) {
                        getTranslations().COMMAND_RENAME_WRONG_USAGE.send(p);
                        return true;
                    }
                    if (!UUIDUtils.isUUID(args[2])) {
                        getTranslations().GENERAL_NOT_A_VALID_UUID.send(p);
                        return true;
                    }
                    UUID uuid = UUID.fromString(args[2]);
                    String newName = StringHelper.buildStringFromArray(args, 3);
                    switch (args[1].toLowerCase()) {
                        case "privatewaypoint": {
                            if (!WPConfig.getGeneralConfig().getRenamingConfig().isAllowRenamingWaypointsPrivate()) {
                                getTranslations().COMMAND_RENAME_WAYPOINT_PRIVATE_DISABLED.send(p);
                                return true;
                            }
                            WPPlayerData data = WPPlayerData.getPlayerData(p.getUniqueId());
                            if (!WPConfig.getGeneralConfig().getDuplicateConfig().isAllowDuplicateNamesWaypointPrivate()) {
                                if (data.findWaypoint(wp -> newName.equalsIgnoreCase(wp.getName())).isPresent()) {
                                    getTranslations().COMMAND_RENAME_WAYPOINT_PRIVATE_NAME_DUPLICATE.send(p);
                                    return true;
                                }
                            }
                            Optional<Waypoint> waypoint = data.findWaypoint(wp -> uuid.equals(wp.getID()));
                            if (!waypoint.isPresent()) {
                                getTranslations().GENERAL_WAYPOINT_NOT_FOUND.send(p);
                                return true;
                            }
                            waypoint.get().setName(newName);
                            getTranslations().COMMAND_RENAME_WAYPOINT_SUCCESS.send(p);
                            break;
                        }
                        case "publicwaypoint": {
                            if (!p.hasPermission("waypoints.rename.public")) {
                                getTranslations().GENERAL_NO_PERMISSION.send(p);
                                return true;
                            }
                            if (!WPConfig.getGeneralConfig().getRenamingConfig().isAllowRenamingWaypointsPublic()) {
                                getTranslations().COMMAND_RENAME_WAYPOINT_PUBLIC_DISABLED.send(p);
                                return true;
                            }
                            if (!WPConfig.getGeneralConfig().getDuplicateConfig().isAllowDuplicateNamesWaypointPublic()) {
                                if (Waypoints.getGlobalStore().getPublicFolder().findWaypoint(wp -> newName.equalsIgnoreCase(wp.getName())).isPresent()) {
                                    getTranslations().COMMAND_RENAME_WAYPOINT_PUBLIC_NAME_DUPLICATE.send(p);
                                    return true;
                                }
                            }
                            Optional<Waypoint> waypoint = Waypoints.getGlobalStore().getPublicFolder().findWaypoint(wp -> uuid.equals(wp.getID()));
                            if (!waypoint.isPresent()) {
                                getTranslations().GENERAL_WAYPOINT_NOT_FOUND.send(p);
                                return true;
                            }
                            waypoint.get().setName(newName);
                            getTranslations().COMMAND_RENAME_WAYPOINT_SUCCESS.send(p);
                            break;
                        }
                        case "permissionwaypoint": {
                            if (!p.hasPermission("waypoints.rename.permission")) {
                                getTranslations().GENERAL_NO_PERMISSION.send(p);
                                return true;
                            }
                            if (!WPConfig.getGeneralConfig().getRenamingConfig().isAllowRenamingWaypointsPermission()) {
                                getTranslations().COMMAND_RENAME_WAYPOINT_PERMISSION_DISABLED.send(p);
                                return true;
                            }
                            if (!WPConfig.getGeneralConfig().getDuplicateConfig().isAllowDuplicateNamesWaypointPermission()) {
                                if (Waypoints.getGlobalStore().getPermissionFolder().findWaypoint(wp -> newName.equalsIgnoreCase(wp.getName())).isPresent()) {
                                    getTranslations().COMMAND_RENAME_WAYPOINT_PERMISSION_NAME_DUPLICATE.send(p);
                                    return true;
                                }
                            }
                            Optional<Waypoint> waypoint = Waypoints.getGlobalStore().getPermissionFolder().findWaypoint(wp -> uuid.equals(wp.getID()));
                            if (!waypoint.isPresent()) {
                                getTranslations().GENERAL_WAYPOINT_NOT_FOUND.send(p);
                                return true;
                            }
                            waypoint.get().setName(newName);
                            getTranslations().COMMAND_RENAME_WAYPOINT_SUCCESS.send(p);
                            break;
                        }
                        case "folder": {
                            // TODO can rename check missing?
                            WPPlayerData data = WPPlayerData.getPlayerData(p.getUniqueId());
                            if (!WPConfig.getGeneralConfig().getDuplicateConfig().isAllowDuplicateNamesFolderPrivate()) {
                                if (data.findFolder(f -> f.getName().equalsIgnoreCase(newName)).isPresent()) {
                                    getTranslations().COMMAND_RENAME_FOLDER_NAME_DUPLICATE.send(p);
                                    return true;
                                }
                            }
                            Optional<Folder> folder = data.findFolder(f -> f.getID().equals(uuid));
                            if (!folder.isPresent()) {
                                getTranslations().GENERAL_FOLDER_NOT_FOUND.send(p);
                                return true;
                            }
                            folder.get().setName(newName);
                            getTranslations().COMMAND_RENAME_FOLDER_SUCCESS.send(p);
                            break;
                        }
                        default:
                            getTranslations().COMMAND_RENAME_WRONG_USAGE.send(p);
                            break;
                    }
                    break;
                }
                default:
                    getTranslations().COMMAND_NOT_FOUND.send(p);
                    break;
            }
        } else {
            getTranslations().GENERAL_NOT_A_PLAYER.send(sender);
        }
        return true;
    }

    public static boolean setPrivateWaypoint(Player p, String name) {
        WPPlayerData data = WPPlayerData.getPlayerData(p.getUniqueId());
        if (!WPConfig.getGeneralConfig().getDuplicateConfig().isAllowDuplicateNamesWaypointPrivate()) {
            if (data.findWaypoint(wp -> wp.getName().equalsIgnoreCase(name)).isPresent()) {
                getTranslations().COMMAND_SET_PRIVATE_NAME_DUPLICATE.send(p);
                return false;
            }
        }
        if (WPConfig.getGeneralConfig().getWaypointLimit() > 0) {
            if (data.countWaypoints() >= WPConfig.getGeneralConfig().getWaypointLimit()) {
                getTranslations().COMMAND_SET_PRIVATE_LIMIT_REACHED.send(p);
                return false;
            }
        }
        data.addWaypoint(new PrivateWaypoint(name, p.getLocation()));
        getTranslations().COMMAND_SET_PRIVATE_SUCCESS.send(p);
        return true;
    }

    public static boolean setPublicWaypoint(Player p, String name) {
        if (!WPConfig.getGeneralConfig().getDuplicateConfig().isAllowDuplicateNamesWaypointPublic()) {
            for (Waypoint wp : Waypoints.getGlobalStore().getPublicFolder().getWaypoints(p)) {
                if (wp.getName().equalsIgnoreCase(name)) {
                    getTranslations().COMMAND_SET_PUBLIC_NAME_DUPLICATE.send(p);
                    return false;
                }
            }
        }
        Waypoints.getGlobalStore().getPublicFolder().addWaypoint(new PublicWaypoint(name, p.getLocation()));
        getTranslations().COMMAND_SET_PUBLIC_SUCCESS.send(p);
        return true;
    }

    public static boolean setPermissionWaypoint(Player p, String permission, String name) {
        if (!WPConfig.getGeneralConfig().getDuplicateConfig().isAllowDuplicateNamesWaypointPermission()) {
            for (Waypoint wp : Waypoints.getGlobalStore().getPermissionFolder().getWaypoints(p)) {
                if (wp.getName().equalsIgnoreCase(name)) {
                    getTranslations().COMMAND_SET_PERMISSION_NAME_DUPLICATE.send(p);
                    return false;
                }
            }
        }
        Waypoints.getGlobalStore().getPermissionFolder().addWaypoint(new PermissionWaypoint(name, p.getLocation(), permission));
        getTranslations().COMMAND_SET_PERMISSION_SUCCESS.send(p);
        return true;
    }

    public static boolean createFolder(Player p, String name) {
        WPPlayerData data = WPPlayerData.getPlayerData(p.getUniqueId());
        if (!WPConfig.getGeneralConfig().getDuplicateConfig().isAllowDuplicateNamesFolderPrivate()) {
            if (data.findFolder(f -> f.getName().equalsIgnoreCase(name)).isPresent()) {
                getTranslations().COMMAND_CREATE_FOLDER_NAME_DUPLICATE.send(p);
                return false;
            }
        }
        if (WPConfig.getGeneralConfig().getFolderLimit() > 0) {
            if (data.getFolders().size() >= WPConfig.getGeneralConfig().getFolderLimit()) {
                getTranslations().COMMAND_CREATE_FOLDER_LIMIT_REACHED.send(p);
                return false;
            }
        }
        data.addFolder(name);
        getTranslations().COMMAND_CREATE_FOLDER_SUCCESS.send(p);
        return true;
    }
}
