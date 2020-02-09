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

package de.md5lukas.waypoints.store;

import de.md5lukas.nbt.Tag;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LocationTag extends Tag {

	private Location location;

	public LocationTag(String name, Location location) {
		super(name);
		this.location = location;
	}

	public LocationTag(String name) {
		super(name);
	}

	public Location value() {
		return location;
	}

	@Override
	public void write(DataOutput dos) throws IOException {
		dos.writeUTF(location.getWorld().getName());
		dos.writeDouble(location.getX());
		dos.writeDouble(location.getY());
		dos.writeDouble(location.getZ());
	}

	@Override
	public void load(DataInput dis) throws IOException {
		location = new Location(Bukkit.getWorld(dis.readUTF()), dis.readDouble(), dis.readDouble(), dis.readDouble());
	}

	@Override
	public String toString() {
		return location == null ? "null" : String.format("World: %s X: %f Y: %f Z: %f", location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
	}

	@Override
	public byte getId() {
		return 100;
	}

	@Override
	public String getTagName() {
		return "TAG_Location";
	}

	@Override
	public Tag copy() {
		return new LocationTag(getName(), location.clone());
	}
}
