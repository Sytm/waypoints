package de.md5lukas.configurate.serializers

import java.lang.reflect.Type
import java.util.function.Predicate
import net.kyori.adventure.key.Key
import org.bukkit.Registry
import org.bukkit.inventory.ItemType
import org.spongepowered.configurate.serialize.ScalarSerializer
import org.spongepowered.configurate.serialize.SerializationException

internal object ItemTypeSerializer : ScalarSerializer<ItemType>(ItemType::class.java) {

  override fun deserialize(type: Type, obj: Any): ItemType {
    try {
      return Registry.ITEM.getOrThrow(Key.key(obj.toString()))
    } catch (e: Exception) {
      throw SerializationException(ItemType::class.java, "Could not parse item type", e)
    }
  }

  override fun serialize(item: ItemType, typeSupported: Predicate<Class<*>>): Any {
    return item.key().asMinimalString()
  }
}
