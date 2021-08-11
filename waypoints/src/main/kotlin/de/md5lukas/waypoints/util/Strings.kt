package de.md5lukas.waypoints.util

import net.md_5.bungee.api.ChatColor
import java.util.regex.Pattern

private val aotVariablePattern = Pattern.compile("\\$\\[(\\w+)]")
private val runtimeVariablePattern = Pattern.compile("\\$\\{(\\w+)}")

fun String.translateColorCodes(): String = ChatColor.translateAlternateColorCodes('&', this)

fun String.aotReplace(map: Map<String, String>): String {
    return replace(aotVariablePattern, map)
}

fun String.runtimeReplace(map: Map<String, String>): String {
    return replace(runtimeVariablePattern, map)
}

private fun String.replace(pattern: Pattern, map: Map<String, String>): String {
    return pattern.matcher(this).replaceAll {
        map[it.group(1)] ?: it.group(0)
    }
}