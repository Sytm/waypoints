package de.md5lukas.waypoints.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import java.time.temporal.TemporalAccessor

infix fun String.placeholder(component: Component) = Placeholder.component(this, component)
infix fun String.placeholder(text: String) = Placeholder.unparsed(this, text)
infix fun String.placeholder(number: Number) = Formatter.number(this, number)
infix fun String.placeholder(temporal: TemporalAccessor) = Formatter.date(this, temporal)
