package de.md5lukas.waypoints

object Environment {
  val DEV = System.getProperty("xyz.jpenilla.run-task", "false").toBooleanStrict()
  const val METRICS_PLUGIN_ID = 6864
  const val MODRINTH_PLUGIN_ID = "1c2olKOU"
}
