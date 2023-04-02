package de.md5lukas.waypoints.util

import kotlin.math.max
import kotlin.math.min

private val minecraftUsernamePattern = Regex("^\\w{3,16}$")
private val nonWordCharacterRegex = Regex("\\W")

fun String.isMinecraftUsername() = this.matches(minecraftUsernamePattern)

fun String.loopingSubstring(size: Int, offset: Int): String {
    val modOffset = Math.floorMod(offset, length)

    val overhang = max(0, (size + modOffset) - length)

    return substring(modOffset, min(length, modOffset + size)) + substring(0, overhang)
}

fun String.containsNonWordCharacter() = nonWordCharacterRegex in this
