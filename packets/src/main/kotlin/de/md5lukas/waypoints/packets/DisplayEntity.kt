package de.md5lukas.waypoints.packets

import com.comphenix.protocol.reflect.EquivalentConverter
import com.comphenix.protocol.wrappers.Vector3F
import com.comphenix.protocol.wrappers.WrappedDataValue
import org.bukkit.Location
import org.bukkit.entity.Display.Billboard
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.util.Vector

open class DisplayEntity(
    player: Player,
    location: Location,
    entityType: EntityType,
    private val billboard: Billboard,
    private val translation: Vector? = null,
    private val scale: Vector? = null,
) : ClientSideEntity(
    player,
    location,
    entityType,
) {

    private companion object {
        val vectorConverter: EquivalentConverter<Vector3F> = Vector3F.getConverter()

        fun Vector.toNativeVec3f() = vectorConverter.getGeneric(Vector3F(x.toFloat(), y.toFloat(), z.toFloat()))
    }

    override fun modifyMetadataValues(spawn: Boolean, dataValues: MutableList<WrappedDataValue>) {
        if (spawn) {
            // https://wiki.vg/Entity_metadata#Display
            // TODO: Minecraft expects org.joml.Vector3f but receives a MC-Client builtin Vector3f
            if (translation !== null) {
                dataValues += WrappedDataValue(10, vectorSerializer, translation.toNativeVec3f())
            }
            if (scale !== null) {
                dataValues += WrappedDataValue(11, vectorSerializer, scale.toNativeVec3f())
            }
            dataValues += WrappedDataValue(14, byteSerializer, billboard.ordinal.toByte())
            dataValues += WrappedDataValue(15, intSerializer, 15 shl 4) // Set block light level to 15
        }
    }
}