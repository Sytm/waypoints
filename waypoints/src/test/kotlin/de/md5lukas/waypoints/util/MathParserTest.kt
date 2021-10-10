package de.md5lukas.waypoints.util

import kotlin.test.Test
import kotlin.test.assertEquals

class MathParserTest {

    @Test
    fun simpleExpression() {
        assertEquals(-1.5, MathParser.parse("(4 - 2^3 + 1) / 2").eval(emptyMap()))
    }

    @Test
    fun stackoverflowExpression() {
        assertEquals(7.5, MathParser.parse("((4 - 2^3 + 1) * -sqrt(3*3+4*4)) / 2").eval(emptyMap()))
    }

    @Test
    fun variables() {
        val expression = MathParser.parse("10 + n * 10", "n")
        assertEquals(10.0, expression.eval(mapOf("n" to 0.0)))
        assertEquals(20.0, expression.eval(mapOf("n" to 1.0)))
        assertEquals(0.0, expression.eval(mapOf("n" to -1.0)))
    }

    @Test
    fun variables2() {
        val expression = MathParser.parse("1 + n", "n")
        assertEquals(1.0, expression.eval(mapOf("n" to 0.0)))
        assertEquals(2.0, expression.eval(mapOf("n" to 1.0)))
        assertEquals(3.0, expression.eval(mapOf("n" to 2.0)))
    }

    @Test
    fun onlyVariables() {
        val expression = MathParser.parse("n", "n")
        assertEquals(10.0, expression.eval(mapOf("n" to 10.0)))
        assertEquals(20.0, expression.eval(mapOf("n" to 20.0)))
        assertEquals(0.0, expression.eval(mapOf("n" to 0.0)))
    }
}