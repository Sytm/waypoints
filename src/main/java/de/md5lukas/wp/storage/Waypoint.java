package de.md5lukas.wp.storage;

import org.bukkit.Location;

import java.util.Objects;
import java.util.UUID;

public class Waypoint {

	private UUID waypointID;
	private String name;
	private Location location;

	Waypoint(UUID waypointID, String name, Location location) {
		this.waypointID = waypointID;
		this.name = name;
		this.location = location;
	}

	public Waypoint(String name, Location location) {
		waypointID = UUID.randomUUID();
		this.name = name;
		this.location = location;
	}

	public UUID getID() {
		return waypointID;
	}

	public String getName() {
		return name;
	}

	public Location getLocation() {
		return location;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Waypoint waypoint = (Waypoint) o;
		return waypointID.equals(waypoint.waypointID) &&
				name.equals(waypoint.name) &&
				location.equals(waypoint.location);
	}

	@Override
	public int hashCode() {
		return Objects.hash(waypointID, name);
	}
}
