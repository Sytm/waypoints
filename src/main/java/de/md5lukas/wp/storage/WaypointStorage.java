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

package de.md5lukas.wp.storage;

import de.md5lukas.wp.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

import static de.md5lukas.wp.util.DataStreamHelper.*;

public class WaypointStorage {

	private static File playerDataFolder, globalWPFile, permissionWPFile;
	private static List<Waypoint> globalWaypoints, permissionWaypoints;
	private static Map<UUID, List<Waypoint>> waypoints;

	public static void setupFiles(File playerDataFolder, File globalWPFile, File permissionWPFile) {
		WaypointStorage.playerDataFolder = playerDataFolder;
		WaypointStorage.globalWPFile = globalWPFile;
		WaypointStorage.permissionWPFile = permissionWPFile;
	}

	public static void save() {
		playerDataFolder.mkdirs();
		waypoints.forEach((key, value) -> {
			File file = new File(playerDataFolder, key + ".wp");
			if (value.isEmpty()) {
				if (file.exists())
					file.delete();
				return;
			}
			try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
				dos.writeInt(value.size());
				for (Waypoint wp : value) {
					writeUUID(dos, wp.getID());
					writeString(dos, wp.getName());
					writeString(dos, wp.getLocation().getWorld().getName());
					dos.writeDouble(wp.getLocation().getX());
					dos.writeDouble(wp.getLocation().getY());
					dos.writeDouble(wp.getLocation().getZ());
				}
			} catch (IOException e) {
				Main.logger().log(Level.SEVERE, "Unable to save waypoint data for player with UUID " + key);
				e.printStackTrace();
			}
		});
		if (globalWaypoints.isEmpty()) {
			if (globalWPFile.exists())
				globalWPFile.delete();
		} else {
			try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(globalWPFile))) {
				dos.writeInt(globalWaypoints.size());
				for (Waypoint wp : globalWaypoints) {
					writeUUID(dos, wp.getID());
					writeString(dos, wp.getName());
					writeString(dos, wp.getLocation().getWorld().getName());
					dos.writeDouble(wp.getLocation().getX());
					dos.writeDouble(wp.getLocation().getY());
					dos.writeDouble(wp.getLocation().getZ());
				}
			} catch (IOException e) {
				Main.logger().log(Level.SEVERE, "An error occurred while saving the global waypoints", e);
			}
		}
		if (permissionWaypoints.isEmpty()) {
			if (permissionWPFile.exists())
				permissionWPFile.delete();
		} else {
			try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(permissionWPFile))) {
				dos.writeInt(permissionWaypoints.size());
				for (Waypoint wp : permissionWaypoints) {
					writeUUID(dos, wp.getID());
					writeString(dos, wp.getName());
					writeString(dos, wp.getPermission());
					writeString(dos, wp.getLocation().getWorld().getName());
					dos.writeDouble(wp.getLocation().getX());
					dos.writeDouble(wp.getLocation().getY());
					dos.writeDouble(wp.getLocation().getZ());
				}
			} catch (IOException e) {
				Main.logger().log(Level.SEVERE, "An error occurred while saving the permission waypoints", e);
			}
		}
	}

	public static boolean load() {
		waypoints = new HashMap<>();
		globalWaypoints = new ArrayList<>();
		permissionWaypoints = new ArrayList<>();
		if (globalWPFile.exists()) {
			try (DataInputStream dis = new DataInputStream(new FileInputStream(globalWPFile))) {
				int amountWaypoints = dis.readInt();
				while (amountWaypoints-- > 0) {
					UUID id = readUUID(dis);
					String name = readString(dis);
					globalWaypoints.add(new Waypoint(id, name, new Location(Bukkit.getWorld(readString(dis)), dis.readDouble(), dis.readDouble(), dis.readDouble()), true));
				}
			} catch (IOException e) {
				Main.logger().log(Level.SEVERE, "An error occurred while loading the global waypoints", e);
				return false;
			}
		}
		if (permissionWPFile.exists()) {
			try (DataInputStream dis = new DataInputStream(new FileInputStream(permissionWPFile))) {
				int amountWaypoints = dis.readInt();
				while (amountWaypoints-- > 0) {
					UUID id = readUUID(dis);
					String name = readString(dis);
					String permission = readString(dis);
					permissionWaypoints.add(new Waypoint(id, name, permission, new Location(Bukkit.getWorld(readString(dis)), dis.readDouble(), dis.readDouble(), dis.readDouble()), true));
				}
			} catch (IOException e) {
				Main.logger().log(Level.SEVERE, "An error occurred while loading the permission waypoints", e);
				return false;
			}
		}
		return true;
	}

	private static List<Waypoint> loadPlayer(UUID uuid) {
		List<Waypoint> list = new ArrayList<>();
		File file = new File(playerDataFolder, uuid + ".wp");
		if (file.exists()) {
			try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
				int amountWaypoints = dis.readInt();
				while (amountWaypoints-- > 0) {
					UUID id = readUUID(dis);
					String name = readString(dis);
					list.add(new Waypoint(id, name, new Location(Bukkit.getWorld(readString(dis)), dis.readDouble(), dis.readDouble(), dis.readDouble())));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		waypoints.put(uuid, list);
		return list;
	}

	private static void savePlayer(UUID uuid) {
		File file = new File(playerDataFolder, uuid + ".wp");
		List<Waypoint> wps = waypoints.get(uuid);
		if (wps.isEmpty()) {
			if (file.exists())
				file.delete();
			return;
		}
		try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
			dos.writeInt(wps.size());
			for (Waypoint wp : wps) {
				writeUUID(dos, wp.getID());
				writeString(dos, wp.getName());
				writeString(dos, wp.getLocation().getWorld().getName());
				dos.writeDouble(wp.getLocation().getX());
				dos.writeDouble(wp.getLocation().getY());
				dos.writeDouble(wp.getLocation().getZ());
			}
		} catch (IOException ioe) {
			Main.logger().log(Level.SEVERE, "Unable to save waypoint data for player with UUID " + uuid);
			ioe.printStackTrace();
		}
	}

	private static final List<UUID> markedToRemove = new ArrayList<>();

	public static void initWaypointChecker(Plugin main) {
		Bukkit.getScheduler().runTaskTimer(main, () -> {
			markedToRemove.forEach(uuid -> {
				if (Bukkit.getPlayer(uuid) == null) {
					savePlayer(uuid);
					waypoints.remove(uuid);
				}
			});
			markedToRemove.clear();
			waypoints.keySet().forEach(uuid -> {
				if (Bukkit.getPlayer(uuid) == null)
					markedToRemove.add(uuid);
			});
		}, 3000/*2.5 Min in ticks*/, 3000/*2.5 Min in ticks*/);
		Bukkit.getScheduler().runTaskTimer(main, WaypointStorage::save, 8400 /*7 Min in ticks*/, 6000/*5 Min in ticks*/);
	}

	/**
	 * Using this method you can add, remove and clear the waypoints associated with a player and the changes will be
	 * saved when @{{@link #save()}} is used
	 *
	 * @param uuid The uuid of the player
	 * @return A modifiable list of waypoints associated with that uuid
	 */
	public static List<Waypoint> getWaypoints(UUID uuid) {
		if (waypoints.containsKey(uuid))
			return waypoints.get(uuid);
		return loadPlayer(uuid);
	}

	public static List<Waypoint> getGlobalWaypoints() {
		return globalWaypoints;
	}

	public static List<Waypoint> getPermissionWaypoints() {
		return permissionWaypoints;
	}
}
