package de.md5lukas.waypoints.pointers.packets

import com.comphenix.protocol.wrappers.WrappedDataValue
import org.bukkit.Location
import org.bukkit.entity.Display.Billboard
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.util.Vector

internal open class DisplayEntity(
    player: Player,
    location: Location,
    entityType: EntityType,
    private val billboard: Billboard,
    private val translation: Vector? = null,
    private val scale: Vector? = null,
) :
    ClientSideEntity(
        player,
        location,
        entityType,
    ) {

  override fun modifyMetadataValues(spawn: Boolean, dataValues: MutableList<WrappedDataValue>) {
    if (spawn) {
      // https://wiki.vg/Entity_metadata#Display
      if (translation !== null) {
        dataValues += WrappedDataValue(11, vectorSerializer, translation.toVector3f())
      }
      if (scale !== null) {
        dataValues += WrappedDataValue(12, vectorSerializer, scale.toVector3f())
      }
      dataValues += WrappedDataValue(15, byteSerializer, billboard.ordinal.toByte())
      dataValues += WrappedDataValue(16, intSerializer, 15 shl 4) // Set block light level to 15
    }
  }
}
