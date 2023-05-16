package de.md5lukas.waypoints.pointers.util

import kotlin.math.atan2
import org.bukkit.Location

internal fun normalizeAngleTo360(angle: Float): Float {
  var normalizedAngle = angle

  while (normalizedAngle < 0) normalizedAngle += 360

  while (normalizedAngle > 360) normalizedAngle -= 360

  return normalizedAngle
}

internal fun getAngleToTarget(origin: Location, target: Location): Float =
    Math.toDegrees(atan2(origin.z - target.z, origin.x - target.x)).toFloat()
