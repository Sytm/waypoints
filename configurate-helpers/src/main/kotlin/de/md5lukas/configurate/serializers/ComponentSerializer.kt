package de.md5lukas.configurate.serializers

import java.lang.reflect.Type
import java.util.function.Predicate
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.spongepowered.configurate.serialize.ScalarSerializer

internal object ComponentSerializer : ScalarSerializer<Component>(Component::class.java) {

  override fun deserialize(type: Type, obj: Any): Component {
    return MiniMessage.miniMessage().deserialize(obj.toString())
  }

  override fun serialize(item: Component, typeSupported: Predicate<Class<*>>): Any {
    return MiniMessage.miniMessage().serialize(item)
  }
}
