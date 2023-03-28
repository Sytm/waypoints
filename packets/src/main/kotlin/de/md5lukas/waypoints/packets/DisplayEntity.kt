package de.md5lukas.waypoints.packets

import com.comphenix.protocol.wrappers.WrappedDataValue
import org.bukkit.Location
import org.bukkit.entity.Display.Billboard
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

open class DisplayEntity(
    player: Player,
    location: Location,
    entityType: EntityType,
    private val billboard: Billboard,
) : ClientSideEntity(
    player,
    location,
    entityType,
) {
    override fun modifyMetadataValues(spawn: Boolean, dataValues: MutableList<WrappedDataValue>) {
        if (spawn) {
            // https://wiki.vg/Entity_metadata#Display
            dataValues += WrappedDataValue(14, byteSerializer, billboard.ordinal.toByte())
            dataValues += WrappedDataValue(15, intSerializer, 15 shl 4) // Set block light level to 15
        }
    }
}