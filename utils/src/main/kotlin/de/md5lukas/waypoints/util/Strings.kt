package de.md5lukas.waypoints.util

private val minecraftUsernamePattern = Regex("^\\w{3,16}$")
private val nonWordCharacterRegex = Regex("\\W")

fun String.isMinecraftUsername() = this.matches(minecraftUsernamePattern)

fun String.containsNonWordCharacter() = nonWordCharacterRegex in this
