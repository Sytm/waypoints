package de.md5lukas.waypoints

object Environment {
  val DEV = System.getProperty("xyz.jpenilla.run-task", "false").toBooleanStrict()
}
