package de.md5lukas.waypoints.util

import org.bukkit.ChatColor
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.max
import kotlin.math.min

private val minecraftUsernamePattern = Pattern.compile("^\\w{3,16}$")
private val aotVariablePattern = Pattern.compile("\\$\\[(\\w+)]")
private val runtimeVariablePattern = Pattern.compile("\\$\\{(\\w+)}")

fun String.translateColorCodes(): String = ChatColor.translateAlternateColorCodes('&', this)

fun String.aotReplace(map: Map<String, String>): String {
    return replace(aotVariablePattern, map)
}

fun String.runtimeReplace(map: Map<String, String>): String {
    return replace(runtimeVariablePattern, map)
}

fun String.isMinecraftUsername() = minecraftUsernamePattern.matcher(this).matches()

private fun String.replace(pattern: Pattern, map: Map<String, String>): String {
    return pattern.matcher(this).replaceAll {
        Matcher.quoteReplacement(map[it.group(1)] ?: it.group(0))
    }
}

fun splitDescriptionStringToList(description: String): List<String> {
    val rawLines = description.lines()

    val lines = ArrayList<String>()
    var lastColorCodes = ""

    description.lineSequence().forEachIndexed { index, line ->
        if (index == rawLines.size - 1 && line.trim().isEmpty()) {
            return@forEachIndexed
        }
        lines.add(lastColorCodes + line)
        lastColorCodes = ChatColor.getLastColors(line)
    }

    return lines
}

fun String.loopingSubstring(size: Int, offset: Int): String {
    val modOffset = Math.floorMod(offset, length)

    val overhang = max(0, (size + modOffset) - length)

    return substring(modOffset, min(length, modOffset + size)) + substring(0, overhang)
}