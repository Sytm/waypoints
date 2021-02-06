package de.md5lukas.waypoints.util

import org.bukkit.plugin.Plugin

fun Plugin.runTask(block: () -> Unit) {
    server.scheduler.runTask(this, block)
}

fun Plugin.runTaskAsync(block: () -> Unit) {
    server.scheduler.runTaskAsynchronously(this, block)
}
