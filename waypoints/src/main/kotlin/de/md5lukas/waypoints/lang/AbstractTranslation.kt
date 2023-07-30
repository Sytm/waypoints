package de.md5lukas.waypoints.lang

import org.jetbrains.annotations.VisibleForTesting

interface AbstractTranslation {
  fun reset()

  @VisibleForTesting fun getKeys(): Array<String>
}
