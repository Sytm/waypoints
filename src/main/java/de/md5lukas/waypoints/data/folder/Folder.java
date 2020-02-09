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

package de.md5lukas.waypoints.data.folder;

import com.google.common.base.Preconditions;
import de.md5lukas.commons.MathHelper;
import de.md5lukas.nbt.extended.UUIDTag;
import de.md5lukas.nbt.tags.CompoundTag;
import de.md5lukas.nbt.tags.ListTag;
import de.md5lukas.waypoints.data.GUISortable;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class Folder implements GUISortable {

	protected final UUID id;
	protected String name;
	protected final long createdAt;
	protected Material material;
	protected List<Waypoint> waypoints;

	public Folder(CompoundTag tag) {
		id = ((UUIDTag) tag.get("id")).value();
		name = tag.getString("name");
		createdAt = tag.getLong("createdAt");
		if (tag.contains("material"))
			material = Material.getMaterial(tag.getString("material"));
		waypoints = loadWaypoints(tag.getList("waypoints"));
	}

	public Folder(String name) {
		this.id = UUID.randomUUID();
		this.name = name;
		this.createdAt = System.currentTimeMillis();
		this.waypoints = new ArrayList<>();
	}

	public final UUID getID() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public long createdAt() {
		return createdAt;
	}

	@Override
	public int getAmount(Player player) {
		return MathHelper.clamp(1, 64, waypoints.size());
	}

	public final void setMaterial(Material material) {
		this.material = material;
	}

	public void addWaypoint(Waypoint waypoint) {
		Preconditions.checkArgument(isCorrectWaypointType(waypoint), "The waypoint does not have the correct type");
		this.waypoints.add(waypoint);
	}

	public void removeWaypoint(UUID waypointId) {
		this.waypoints.removeIf(wp -> wp.getID().equals(waypointId));
	}

	public Optional<Waypoint> findWaypoint(Predicate<Waypoint> predicate) {
		return waypoints.stream().filter(predicate).findFirst();
	}

	public List<Waypoint> getWaypoints(Player player) {
		return waypoints;
	}

	protected abstract List<Waypoint> loadWaypoints(ListTag waypoints);

	protected abstract boolean isCorrectWaypointType(Waypoint waypoint);

	public CompoundTag toCompoundTag() {
		CompoundTag tag = new CompoundTag();
		tag.put("id", new UUIDTag(null, id));
		tag.putString("name", name);
		tag.putLong("createdAt", createdAt);
		if (material != null)
			tag.putString("material", material.name());
		tag.putListTag("waypoints", waypoints.stream().map(Waypoint::toCompoundTag).collect(Collectors.toList()));
		return tag;
	}
}
