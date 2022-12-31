package de.md5lukas.waypoints.util

import org.bukkit.Location
import kotlin.math.atan2

fun normalizeAngleTo360(angle: Float): Float {
    var normalizedAngle = angle

    while (normalizedAngle < 0)
        normalizedAngle += 360

    while (normalizedAngle > 360)
        normalizedAngle -= 360

    return normalizedAngle
}

fun getAngleToTarget(origin: Location, target: Location): Float =
    Math.toDegrees(atan2(origin.z - target.z, origin.x - target.x)).toFloat()