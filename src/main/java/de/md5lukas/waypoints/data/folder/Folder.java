package de.md5lukas.waypoints.data.folder;

import com.google.common.base.Preconditions;
import de.md5lukas.nbt.extended.UUIDTag;
import de.md5lukas.nbt.tags.CompoundTag;
import de.md5lukas.nbt.tags.ListTag;
import de.md5lukas.waypoints.data.GUISortable;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
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
		if (tag.contains("materials"))
			material = Material.valueOf(tag.getString("material"));
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
		return Math.min(64, waypoints.size());
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
		return Collections.unmodifiableList(waypoints);
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
