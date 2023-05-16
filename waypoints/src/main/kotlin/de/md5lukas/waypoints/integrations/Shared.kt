package de.md5lukas.waypoints.integrations

import java.io.File
import java.nio.charset.StandardCharsets
import org.bukkit.plugin.Plugin

fun extractIcons(plugin: Plugin) {
  plugin.getResource("resourceIndex")!!.bufferedReader(StandardCharsets.UTF_8).useLines { seq ->
    seq.filter { it.startsWith("icons/") }
        .forEach {
          if (!File(plugin.dataFolder, it).exists()) {
            plugin.saveResource(it, false)
          }
        }
  }
}
