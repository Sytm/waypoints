package de.md5lukas.waypoints.data.waypoint;

import de.md5lukas.nbt.extended.UUIDTag;
import de.md5lukas.nbt.tags.CompoundTag;
import de.md5lukas.waypoints.data.GUISortable;
import de.md5lukas.waypoints.gui.GUIType;
import de.md5lukas.waypoints.store.LocationTag;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.UUID;

public abstract class Waypoint implements GUISortable {

	protected final UUID id;
	protected String name;
	protected final long createdAt;
	protected final Location location;
	protected Material material;

	public Waypoint(CompoundTag tag) {
		this.id = ((UUIDTag) tag.get("id")).value();
		this.name = tag.getString("name");
		this.createdAt = tag.getLong("createdAt");
		this.location = ((LocationTag) tag.get("location")).value();
		if (tag.contains("material")) {
			this.material = Material.valueOf(tag.getString("material"));
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

	public CompoundTag toCompoundTag() {
		CompoundTag tag = new CompoundTag();
		tag.put("id", new UUIDTag(null, id));
		tag.putString("name", name);
		tag.put("location", new LocationTag(null, location));
		if (material != null)
			tag.putString("material", material.name());
		return tag;
	}
}
