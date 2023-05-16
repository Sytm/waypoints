package de.md5lukas.waypoints.util

import java.util.*

fun <T> T.asSingletonList(): List<T> {
  return Collections.singletonList(this)
}
