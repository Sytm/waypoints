package de.md5lukas.waypoints.util

import org.bukkit.ChatColor
import kotlin.math.max
import kotlin.math.min

private val minecraftUsernamePattern = Regex("^\\w{3,16}$")
private val nonWordCharacterRegex = Regex("\\W")
private val aotVariablePattern = Regex("\\$\\[(\\w+)]")
private val runtimeVariablePattern = Regex("\\$\\{(\\w+)}")

fun String.translateColorCodes(): String = ChatColor.translateAlternateColorCodes('&', this)

fun String.aotReplace(map: Map<String, String>): String {
    return replace(aotVariablePattern, map)
}

fun String.runtimeReplace(map: Map<String, String>): String {
    return replace(runtimeVariablePattern, map)
}

fun String.isMinecraftUsername() = this.matches(minecraftUsernamePattern)

private fun String.replace(regex: Regex, map: Map<String, String>): String {
    return regex.replace(this) { map[it.groupValues[1]] ?: it.groupValues[0] }
}

fun splitDescriptionStringToList(description: String): List<String> {
    val rawLines = description.lines()

    val lines = ArrayList<String>()
    var lastColorCodes = ""

    rawLines.forEachIndexed { index, line ->
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

fun String.containsNonWordCharacter() = nonWordCharacterRegex in this
