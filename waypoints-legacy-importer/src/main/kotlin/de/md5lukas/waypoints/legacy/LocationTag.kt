package de.md5lukas.waypoints.legacy

import de.md5lukas.nbt.Tag
import org.bukkit.Bukkit
import org.bukkit.Location
import java.io.DataInput
import java.io.DataOutput

internal class LocationTag(
    name: String,
    private var location: Location? = null
) : Tag(name) {

    fun value(): Location {
        return location!!
    }

    override fun write(dos: DataOutput) {
        location!!.let {
            dos.writeUTF(it.world!!.name)
            dos.writeDouble(it.x)
            dos.writeDouble(it.y)
            dos.writeDouble(it.z)
        }
    }

    override fun load(dis: DataInput) {
        location = Location(Bukkit.getWorld(dis.readUTF()), dis.readDouble(), dis.readDouble(), dis.readDouble())
    }

    override fun toString(): String {
        return location?.let { loc ->
            "World: ${loc.world?.name ?: "null"} X: ${loc.x} Y: ${loc.y} Z: ${loc.z}"
        } ?: "null"
    }

    override fun getId(): Byte {
        return 100
    }

    override fun getTagName(): String {
        return "TAG_Location"
    }

    override fun copy(): Tag {
        return LocationTag(name, location!!.clone())
    }
}