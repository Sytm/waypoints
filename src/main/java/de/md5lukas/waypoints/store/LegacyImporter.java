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

package de.md5lukas.waypoints.store;

import de.md5lukas.commons.UUIDUtils;
import de.md5lukas.waypoints.Waypoints;
import de.md5lukas.waypoints.data.WPPlayerData;
import de.md5lukas.waypoints.data.waypoint.PermissionWaypoint;
import de.md5lukas.waypoints.data.waypoint.PrivateWaypoint;
import de.md5lukas.waypoints.data.waypoint.PublicWaypoint;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

public class LegacyImporter {

	private static List<PublicWaypoint> publicWaypoints;
	private static List<PermissionWaypoint> permissionWaypoints;
	private static Map<UUID, List<PrivateWaypoint>> playerWaypoints;

	@SuppressWarnings("ConstantConditions")
	public static void importLegacy(File pluginFolder) {
		publicWaypoints = new ArrayList<>();
		permissionWaypoints = new ArrayList<>();
		playerWaypoints = new HashMap<>();
		File legacyDataFolder = new File(pluginFolder, "data");
		loadPublicWaypoints(new File(legacyDataFolder, "global.wp"));
		loadPermissionWaypoints(new File(legacyDataFolder, "permission.wp"));
		Arrays.stream(legacyDataFolder.listFiles()).forEach(file -> {
			String[] parts = file.getName().split("\\.");
			if (parts.length == 2 && "wp".equalsIgnoreCase(parts[1]) && UUIDUtils.isUUID(parts[0])) {
				loadPlayerWaypoints(UUID.fromString(parts[0]), file);
			}
		});
	}

	public static void registerLoadedLegacyData() {
		if (publicWaypoints == null)
			return;
		publicWaypoints.forEach(Waypoints.getGlobalStore().getPublicFolder()::addWaypoint);
		permissionWaypoints.forEach(Waypoints.getGlobalStore().getPermissionFolder()::addWaypoint);
		playerWaypoints.forEach((uuid, privateWaypoints) -> {
			WPPlayerData data = WPPlayerData.getPlayerData(uuid);
			privateWaypoints.forEach(data::addWaypoint);
		});
	}

	private static void loadPublicWaypoints(File file) {
		if (file.exists()) {
			try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
				int amount = dis.readInt();
				while (amount-- > 0) {
					readUUID(dis);
					String name = readString(dis);
					Location location = readLocation(dis);
					publicWaypoints.add(new PublicWaypoint(name, location));
				}
			} catch (IOException e) {
				Waypoints.logger().log(Level.SEVERE, "Couldn't load legacy global waypoints", e);
			}
		}
	}

	private static void loadPermissionWaypoints(File file) {
		if (file.exists()) {
			try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
				int amount = dis.readInt();
				while (amount-- > 0) {
					readUUID(dis);
					String name = readString(dis);
					String permission = readString(dis);
					Location location = readLocation(dis);
					permissionWaypoints.add(new PermissionWaypoint(name, location, permission));
				}
			} catch (IOException e) {
				Waypoints.logger().log(Level.SEVERE, "Couldn't load legacy permission waypoints", e);
			}
		}
	}

	private static void loadPlayerWaypoints(UUID uuid, File file) {
		if (file.exists()) {
			WPPlayerData data = WPPlayerData.getPlayerData(uuid);
			try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
				int amount = dis.readInt();
				while (amount-- > 0) {
					readUUID(dis);
					String name = readString(dis);
					Location location = readLocation(dis);
					playerWaypoints.computeIfAbsent(uuid, key -> new ArrayList<>()).add(new PrivateWaypoint(name, location));
				}
			} catch (IOException e) {
				Waypoints.logger().log(Level.SEVERE, "Couldn't load legacy player waypoints with the uuid " + uuid, e);
			}
		}
	}

	private static String readString(DataInputStream dis) throws IOException {
		byte[] buffer = new byte[dis.readInt()];
		dis.read(buffer);
		return new String(buffer, StandardCharsets.UTF_8);
	}

	private static UUID readUUID(DataInputStream dis) throws IOException {
		return new UUID(dis.readLong(), dis.readLong());
	}

	private static Location readLocation(DataInputStream dis) throws IOException {
		return new Location(Bukkit.getWorld(readString(dis)), dis.readDouble(), dis.readDouble(), dis.readDouble());
	}
}
