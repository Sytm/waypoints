package de.md5lukas.waypoints.pointers.packets

import com.comphenix.protocol.reflect.accessors.Accessors
import com.comphenix.protocol.utility.MinecraftReflection
import java.util.concurrent.atomic.AtomicInteger

internal object Packets {

  private val entityId =
      Accessors.getFieldAccessor(
          MinecraftReflection.getEntityClass(), AtomicInteger::class.java, true)

  val nextEntityId: Int
    get() = (entityId.get(null) as AtomicInteger).incrementAndGet()
}
