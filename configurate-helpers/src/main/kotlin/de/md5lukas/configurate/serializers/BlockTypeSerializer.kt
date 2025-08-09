package de.md5lukas.configurate.serializers

import java.lang.reflect.Type
import java.util.function.Predicate
import net.kyori.adventure.key.Key
import org.bukkit.Registry
import org.bukkit.block.BlockType
import org.spongepowered.configurate.serialize.ScalarSerializer
import org.spongepowered.configurate.serialize.SerializationException

internal object BlockTypeSerializer : ScalarSerializer<BlockType>(BlockType::class.java) {

  override fun deserialize(type: Type, obj: Any): BlockType {
    try {
      return Registry.BLOCK.getOrThrow(Key.key(obj.toString()))
    } catch (e: Exception) {
      throw SerializationException(BlockType::class.java, "Could not parse block type", e)
    }
  }

  override fun serialize(item: BlockType, typeSupported: Predicate<Class<*>>): Any {
    return item.key().asMinimalString()
  }
}
