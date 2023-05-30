package de.md5lukas.waypoints.util

import de.md5lukas.commons.paper.placeholder
import de.md5lukas.waypoints.WaypointsPlugin
import kotlin.math.roundToLong
import org.bukkit.Location
import org.bukkit.entity.Player

fun Location.getResolvers(plugin: WaypointsPlugin, player: Player) =
    arrayOf(
        "world" placeholder plugin.worldTranslations.getWorldName(world!!),
        "distance" placeholder player.location.distance(this).roundToLong(),
        "x" placeholder x,
        "y" placeholder y,
        "z" placeholder z,
        "block_x" placeholder blockX,
        "block_y" placeholder blockY,
        "block_z" placeholder blockZ,
    )
