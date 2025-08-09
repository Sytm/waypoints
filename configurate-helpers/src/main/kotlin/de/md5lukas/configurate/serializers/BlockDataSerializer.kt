package de.md5lukas.configurate.serializers

import java.lang.reflect.Type
import java.util.function.Predicate
import net.kyori.adventure.key.Key
import org.bukkit.Registry
import org.bukkit.block.data.BlockData
import org.spongepowered.configurate.serialize.ScalarSerializer
import org.spongepowered.configurate.serialize.SerializationException

internal object BlockDataSerializer : ScalarSerializer<BlockData>(BlockData::class.java) {

  override fun deserialize(type: Type, obj: Any): BlockData? {
    val block = Registry.BLOCK.get(Key.key(obj.toString()))
    if (block == null) {
      throw SerializationException(BlockData::class.java, "$obj is not a valid block type")
    }
    return block.createBlockData()
  }

  override fun serialize(item: BlockData, typeSupported: Predicate<Class<*>>): Any {
    return item.material.key().asMinimalString()
  }
}
