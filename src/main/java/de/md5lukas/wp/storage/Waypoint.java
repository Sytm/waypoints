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

import org.bukkit.Location;

import java.util.Objects;
import java.util.UUID;

public class Waypoint implements Cloneable {

	private final UUID waypointID;
	private final String name;
	private final String permission;
	@SuppressWarnings("CanBeFinal")
	private Location location;
	private final boolean isGlobal;

	Waypoint(UUID waypointID, String name, Location location) {
		this(waypointID, name, null, location);
	}

	Waypoint(UUID waypointID, String name, String permission, Location location) {
		this(waypointID, name, permission, location, false);
	}

	Waypoint(UUID waypointID, String name, Location location, boolean isGlobal) {
		this(waypointID, name, null, location, isGlobal);
	}

	Waypoint(UUID waypointID, String name, String permission, Location location, boolean isGlobal) {
		this.waypointID = waypointID;
		this.name = name;
		this.permission = permission;
		this.location = location;
		this.isGlobal = isGlobal;
	}

	public Waypoint(String name, Location location) {
		this(name, null, location);
	}

	public Waypoint(String name, Location location, boolean isGlobal) {
		this(name, null, location, isGlobal);
	}

	public Waypoint(String name, String permission, Location location) {
		this(name, permission, location, false);
	}

	public Waypoint(String name, String permission, Location location, boolean isGlobal) {
		waypointID = UUID.randomUUID();
		this.name = name;
		this.permission = permission;
		this.location = location;
		this.isGlobal = isGlobal;
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

	public String getPermission() {
		return permission;
	}

	public boolean isGlobal() {
		return isGlobal;
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
	public String toString() {
		return "Waypoint{" +
				"waypointID=" + waypointID +
				", name='" + name + '\'' +
				", permission='" + permission + '\'' +
				", location=" + location +
				", isGlobal=" + isGlobal +
				'}';
	}

	public Waypoint clone() {
		return new Waypoint(waypointID, name, permission, location.clone(), isGlobal);
	}

	@Override
	public int hashCode() {
		return Objects.hash(waypointID, name);
	}
}
