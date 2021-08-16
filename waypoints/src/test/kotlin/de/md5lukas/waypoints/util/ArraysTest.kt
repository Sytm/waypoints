package de.md5lukas.waypoints.util

import kotlin.test.Test
import kotlin.test.assertEquals

class ArraysTest {

    private val testString = "Hello world. This is a test"
    private val testArray = testString.split(' ').toTypedArray()

    @Test
    fun `join() with offset 0`() {
        assertEquals(testString, testArray.join())
    }

    @Test
    fun `join() with offset 1`() {
        assertEquals("world. This is a test", testArray.join(1))
    }

    @Test
    fun `join() with offset 5`() {
        assertEquals("test", testArray.join(5))
    }

    @Test
    fun `join() with offset 6`() {
        assertEquals("", testArray.join(6))
    }
}