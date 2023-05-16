package de.md5lukas.waypoints.util

import kotlin.test.Test
import kotlin.test.assertEquals

class StringsTest {
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
