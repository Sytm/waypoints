package de.md5lukas.configurate

import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException

fun ConfigurationNode.nonVirtualNode(vararg path: Any): ConfigurationNode {
  if (!hasChild(*path)) {
    throw SerializationException("Required field ${path.contentToString()} was not present in node")
  }
  return node(*path)
}
