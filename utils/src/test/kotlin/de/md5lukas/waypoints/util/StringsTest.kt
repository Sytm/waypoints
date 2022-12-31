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

    private val loopingTestString = "12345678"

    @Test
    fun `loopingSubstring() with negative offset`() {
        assertEquals("78", loopingTestString.loopingSubstring(2, -2))
        assertEquals("81", loopingTestString.loopingSubstring(2, -1))
    }

    @Test
    fun `loopingSubstring() without wrapping around`() {
        assertEquals("12", loopingTestString.loopingSubstring(2, 0))
        assertEquals("23", loopingTestString.loopingSubstring(2, 1))
        assertEquals("34", loopingTestString.loopingSubstring(2, 2))
        assertEquals("45", loopingTestString.loopingSubstring(2, 3))
        assertEquals("56", loopingTestString.loopingSubstring(2, 4))
        assertEquals("67", loopingTestString.loopingSubstring(2, 5))
        assertEquals("78", loopingTestString.loopingSubstring(2, 6))
    }

    @Test
    fun `loopingSubstring() at edge`() {
        assertEquals("81", loopingTestString.loopingSubstring(2, 7))
        assertEquals("7812", loopingTestString.loopingSubstring(4, 6))
    }

    @Test
    fun `loopingSubstring() over edge`() {
        assertEquals("12", loopingTestString.loopingSubstring(2, 8))
    }
}