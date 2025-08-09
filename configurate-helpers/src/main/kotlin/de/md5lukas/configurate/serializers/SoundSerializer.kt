package de.md5lukas.configurate.serializers

import de.md5lukas.configurate.nonVirtualNode
import java.lang.reflect.Type
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer

internal object SoundSerializer : TypeSerializer<Sound> {

  private const val NAME = "name"
  private const val VOLUME = "volume"
  private const val PITCH = "pitch"

  override fun deserialize(type: Type, node: ConfigurationNode): Sound {
    val keyNode = node.nonVirtualNode(NAME)
    val rawKey =
        keyNode.string
            ?: throw SerializationException(keyNode, String::class.java, "name is not a string")
    return Sound.sound()
        .type(Key.key(rawKey))
        .volume(node.node(VOLUME).getFloat(1.0f))
        .pitch(node.node(PITCH).getFloat(1.0f))
        .build()
  }

  override fun serialize(type: Type, obj: Sound?, node: ConfigurationNode) {
    if (obj == null) {
      node.raw(null)
      return
    }
    node.node(NAME).set(obj.name().asMinimalString())
    if (obj.volume() == 1.0f) {
      node.node(VOLUME).raw(null)
    } else {
      node.node(VOLUME).set(obj.volume().toDouble())
    }
    if (obj.pitch() == 1.0f) {
      node.node(PITCH).raw(null)
    } else {
      node.node(PITCH).set(obj.pitch().toDouble())
    }
  }
}
