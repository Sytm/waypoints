package de.md5lukas.configurate.serializers

import de.md5lukas.configurate.nonVirtualNode
import java.lang.reflect.Type
import java.time.Period
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.TypeSerializer

class PeriodSerializer : TypeSerializer<Period> {

  private companion object {
    const val YEARS = "years"
    const val MONTHS = "months"
    const val DAYS = "days"
  }

  override fun deserialize(type: Type, node: ConfigurationNode): Period {
    return Period.of(
        node.nonVirtualNode(YEARS).int,
        node.nonVirtualNode(MONTHS).int,
        node.nonVirtualNode(DAYS).int)
  }

  override fun serialize(type: Type, obj: Period?, node: ConfigurationNode) {
    val period = obj ?: Period.ZERO

    node.node(YEARS).set(period.years)
    node.node(MONTHS).set(period.months)
    node.node(DAYS).set(period.days)
  }
}
