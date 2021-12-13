package de.md5lukas.waypoints.util

import kotlin.test.Test
import kotlin.test.assertEquals

class StringsTest {

    val testString = "$[prefix] $[color] > Hello \${player}, time to meet \${other}"

    @Test
    fun `aotReplace() with no values`() {
        assertEquals(testString, testString.aotReplace(emptyMap()))
    }

    @Test
    fun `aotReplace() with values`() {
        assertEquals(
            "Waypoints green > Hello \${player}, time to meet \${other}",
            testString.aotReplace(mapOf("prefix" to "Waypoints", "color" to "green"))
        )
    }

    @Test
    fun `runtimeReplace() with no values`() {
        assertEquals(testString, testString.runtimeReplace(emptyMap()))
    }

    @Test
    fun `runtimeReplace() with values`() {
        assertEquals(
            "$[prefix] $[color] > Hello Steve, time to meet Alex",
            testString.runtimeReplace(mapOf("player" to "Steve", "other" to "Alex"))
        )
    }

    @Test
    fun `isMinecraftUsername() with too short username`() {
        assertEquals(false, "12".isMinecraftUsername())
    }

    @Test
    fun `isMinecraftUsername() with too long username`() {
        assertEquals(false, "12345678901234567".isMinecraftUsername())
    }

    @Test
    fun `isMinecraftUsername() with min length username`() {
        assertEquals(true, "123".isMinecraftUsername())
    }

    @Test
    fun `isMinecraftUsername() with max length username`() {
        assertEquals(true, "1234567890123456".isMinecraftUsername())
    }

    @Test
    fun `isMinecraftUsername() with underscores`() {
        assertEquals(true, "Hello_World".isMinecraftUsername())
    }

    @Test
    fun `isMinecraftUsername() with space`() {
        assertEquals(false, "Hello World".isMinecraftUsername())
    }


}