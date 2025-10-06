package de.md5lukas.configurate.serializers

import java.lang.reflect.Type
import java.util.function.Predicate
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.MiniMessage
import org.spongepowered.configurate.serialize.ScalarSerializer

internal object StyleSerializer : ScalarSerializer<Style>(Style::class.java) {

  override fun deserialize(type: Type, obj: Any): Style {
    return MiniMessage.miniMessage().deserialize(obj.toString()).style()
  }

  override fun serialize(item: Style, typeSupported: Predicate<Class<*>>): Any {
    return MiniMessage.miniMessage().serialize(Component.text("", item))
  }
}
