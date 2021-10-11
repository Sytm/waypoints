package de.md5lukas.waypoints.legacy.internal;

import de.md5lukas.nbt.Tag;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

class LocationTag extends Tag {

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
        return location == null ? "null" : String.format("World: %s X: %f Y: %f Z: %f", location.getWorld() == null ? "null" : location.getWorld().getName(), location.getX(),
                location.getY(), location.getZ());
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
