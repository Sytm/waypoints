package de.md5lukas.waypoints.util

import de.md5lukas.waypoints.WaypointsPlugin
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Location
import org.bukkit.entity.Player
import java.time.temporal.TemporalAccessor
import kotlin.math.roundToLong

infix fun String.placeholder(component: Component) = Placeholder.component(this, component)
infix fun String.placeholder(text: String) = Placeholder.unparsed(this, text)
infix fun String.placeholder(number: Number) = Formatter.number(this, number)
infix fun String.placeholder(temporal: TemporalAccessor) = Formatter.date(this, temporal)

fun Location.getResolvers(plugin: WaypointsPlugin, player: Player) = arrayOf(
    "world" placeholder plugin.worldTranslations.getWorldName(world!!),
    "distance" placeholder player.location.distance(this).roundToLong(),
    "x" placeholder x,
    "y" placeholder y,
    "z" placeholder z,
    "block_x" placeholder blockX,
    "block_y" placeholder blockY,
    "block_z" placeholder blockZ,
)