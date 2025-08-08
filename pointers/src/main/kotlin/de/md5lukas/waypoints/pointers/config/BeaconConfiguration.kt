package de.md5lukas.waypoints.pointers.config

import de.md5lukas.configurate.Positive
import java.lang.reflect.Type
import java.util.function.Predicate
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.spongepowered.configurate.serialize.ScalarSerializer

class BeaconConfiguration : RepeatingPointerConfiguration {

  override var enabled = true
    private set

  @Positive
  override var interval = 30
    private set

  @Positive
  var minDistance: Long = 50
    get() = field * field
    private set

  @Positive
  var maxDistance: ViewDistanceLong = ViewDistanceLong(null)
    private set

  var baseBlock: BlockData = Material.IRON_BLOCK.createBlockData()
    private set

  class ViewDistanceLong(val value: Long?) {
    fun value(): Long = ((value ?: (Bukkit.getViewDistance() * 16L))).let { it * it }

    class ViewDistanceLongSerializer :
        ScalarSerializer<ViewDistanceLong>(ViewDistanceLong::class.java) {
      override fun deserialize(type: Type, obj: Any): ViewDistanceLong =
          if (obj is Number) {
            ViewDistanceLong(obj.toLong())
          } else {
            ViewDistanceLong(null)
          }

      override fun serialize(item: ViewDistanceLong, typeSupported: Predicate<Class<*>>): Any =
          item.value ?: "auto"
    }
  }
}
