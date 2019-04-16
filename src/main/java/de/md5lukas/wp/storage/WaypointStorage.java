package de.md5lukas.wp.storage;

import de.md5lukas.wp.util.PaginationList;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WaypointStorage {

	private static final int waypointsPerPage = 27;

	private static final Charset cs = StandardCharsets.UTF_8;
	private static Map<UUID, PaginationList<Waypoint>> waypoints;

	public static void save(File folder) {
		folder.mkdirs();
		for (Map.Entry<UUID, PaginationList<Waypoint>> entry : waypoints.entrySet()) {
			File file = new File(folder, entry.getKey() + ".wp");
			if (entry.getValue().isEmpty()) {
				if (file.exists())
					file.delete();
				continue;
			}
			try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
				dos.writeInt(entry.getValue().size());
				for (Waypoint wp : entry.getValue()) {
					dos.writeLong(wp.getID().getMostSignificantBits());
					dos.writeLong(wp.getID().getLeastSignificantBits());
					dos.writeInt(wp.getName().getBytes(cs).length);
					dos.write(wp.getName().getBytes(cs));
					dos.writeInt(wp.getLocation().getWorld().getName().getBytes(cs).length);
					dos.write(wp.getLocation().getWorld().getName().getBytes(cs));
					dos.writeDouble(wp.getLocation().getX());
					dos.writeDouble(wp.getLocation().getY());
					dos.writeDouble(wp.getLocation().getZ());
				}
			} catch (IOException ioe) {
				System.err.println("Unable to save waypoint data for player with UUID " + entry.getKey());
				ioe.printStackTrace();
			}
		}
	}

	public static void load(File folder) {
		if (waypoints != null) waypoints.clear();
		waypoints = new HashMap<>();
		if (!folder.exists() || !folder.isDirectory()) return;
		for (File file : folder.listFiles()) {
			if (file.getName().endsWith(".wp")) {
				try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
					UUID uuid = UUID.fromString(file.getName().substring(0, 36));
					int amountWaypoints = dis.readInt();
					PaginationList<Waypoint> playerWaypoints = new PaginationList<>(waypointsPerPage);
					while (amountWaypoints-- > 0) {
						UUID wpID = new UUID(dis.readLong(), dis.readLong());
						byte[] stringBuffer = new byte[dis.readInt()];
						dis.read(stringBuffer);
						String name = new String(stringBuffer, cs);
						stringBuffer = new byte[dis.readInt()];
						dis.read(stringBuffer);
						Location loc = new Location(Bukkit.getWorld(new String(stringBuffer, cs)), dis.readDouble(), dis.readDouble(), dis.readDouble());
						playerWaypoints.add(new Waypoint(wpID, name, loc));
					}
					waypoints.put(uuid, playerWaypoints);
				} catch (IOException ioe) {
					System.err.println("Unable to open file " + file.getAbsolutePath());
					ioe.printStackTrace();
				} catch (IndexOutOfBoundsException | IllegalArgumentException uuidex) {
					System.err.println("Can't read file " + file.getName() + " because it has an malformed name");
				}
			}
		}
	}

	/**
	 * Using this method you can add, remove and clear the waypoints associated with a player and the changes will be
	 * saved when @{{@link #save(File)}} is used
	 *
	 * @param uuid The uuid of the player
	 * @return A modifiable list of waypoints associated with that uuid
	 */
	public static PaginationList<Waypoint> getWaypoints(UUID uuid) {
		return waypoints.computeIfAbsent(uuid, uuid1 -> new PaginationList<>(waypointsPerPage));
	}
}
