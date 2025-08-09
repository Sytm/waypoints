package de.md5lukas.configurate.serializers

import java.lang.reflect.Type
import java.time.Duration
import java.util.function.Predicate
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration
import org.spongepowered.configurate.serialize.ScalarSerializer

internal object DurationSerializer : ScalarSerializer<Duration>(Duration::class.java) {

  override fun deserialize(type: Type, obj: Any): Duration {
    return kotlin.time.Duration.parse(obj.toString()).toJavaDuration()
  }

  override fun serialize(item: Duration, typeSupported: Predicate<Class<*>>): Any {
    return item.toKotlinDuration().toString()
  }
}
