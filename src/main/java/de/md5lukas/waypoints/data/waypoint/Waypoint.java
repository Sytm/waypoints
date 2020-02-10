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

package de.md5lukas.waypoints.data.waypoint;

import de.md5lukas.commons.MathHelper;
import de.md5lukas.commons.tags.LocationTag;
import de.md5lukas.nbt.extended.UUIDTag;
import de.md5lukas.nbt.tags.CompoundTag;
import de.md5lukas.waypoints.Messages;
import de.md5lukas.waypoints.data.GUISortable;
import de.md5lukas.waypoints.display.BlockColor;
import de.md5lukas.waypoints.gui.GUIType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class Waypoint implements GUISortable {

	protected final UUID id;
	protected String name;
	protected final long createdAt;
	protected final Location location;
	protected Material material;

	protected BlockColor beaconColor;

	public Waypoint(CompoundTag tag) {
		this.id = ((UUIDTag) tag.get("id")).value();
		this.name = tag.getString("name");
		this.createdAt = tag.getLong("createdAt");
		this.location = ((LocationTag) tag.get("location")).value();
		if (tag.contains("material")) {
			this.material = Material.valueOf(tag.getString("material"));
		}
		if (tag.contains("beaconColor")) {
			this.beaconColor = BlockColor.valueOf(tag.getString("beaconColor"));
		}
	}

	public Waypoint(String name, Location location) {
		this.id = UUID.randomUUID();
		this.name = name;
		this.createdAt = System.currentTimeMillis();
		this.location = location;
	}

	public final UUID getID() {
		return this.id;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public final String getName() {
		return this.name;
	}

	@Override
	public long createdAt() {
		return createdAt;
	}

	public final Location getLocation() {
		return this.location;
	}

	public final void setMaterial(Material material) {
		this.material = material;
	}

	@Override
	public GUIType getType() {
		return GUIType.WAYPOINT;
	}

	public void setBeaconColor(BlockColor beaconColor) {
		this.beaconColor = beaconColor;
	}

	public abstract BlockColor getBeaconColor();

	public CompoundTag toCompoundTag() {
		CompoundTag tag = new CompoundTag();
		tag.put("id", new UUIDTag(null, id));
		tag.putString("name", name);
		tag.put("location", new LocationTag(null, location));
		if (material != null)
			tag.putString("material", material.name());
		if (beaconColor != null)
			tag.putString("beaconColor", beaconColor.name());
		return tag;
	}

	protected String getDistance2D(Player player) {
		if (player.getWorld().equals(location.getWorld())) {
			return MathHelper.format(MathHelper.distance2D(player.getLocation(), location));
		}
		return Messages.INVENTORY_WAYPOINT_DISTANCE_OTHER_WORLD.getRaw(player);
	}
}
