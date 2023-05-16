package de.md5lukas.waypoints.util

import kotlin.math.*

/*
 * Based on this StackOverflow answer: https://stackoverflow.com/a/26227947
 */
class MathParser private constructor(private val str: String, private val variables: List<String>) {

  companion object {
    fun parse(expression: String, vararg variables: String): Expression =
        MathParser(expression, listOf(*variables)).parse()
  }

  private var pos = -1
  private var ch: Char = Char.MIN_VALUE

  private fun nextChar() {
    ch = if (++pos < str.length) str[pos] else Char.MIN_VALUE
  }

  private fun eat(charToEat: Char): Boolean {
    while (ch == ' ') nextChar()
    if (ch == charToEat) {
      nextChar()
      return true
    }
    return false
  }

  fun parse(): Expression {
    nextChar()
    val x = parseExpression()
    if (pos < str.length) throw RuntimeException("Unexpected: '$ch'")
    return x
  }

  // Grammar:
  // expression = term | expression `+` term | expression `-` term
  // term = factor | term `*` factor | term `/` factor
  // factor = `+` factor | `-` factor | `(` expression `)`
  //        | number | functionName factor | factor `^` factor
  private fun parseExpression(): Expression {
    var x = parseTerm()
    while (true) {
      when {
        eat('+') -> {
          val a = x
          val b = parseTerm()
          x = Expression { a.eval(it) + b.eval(it) }
        }
        eat('-') -> {
          val a = x
          val b = parseTerm()
          x = Expression { a.eval(it) - b.eval(it) }
        }
        else -> return x
      }
    }
  }

  private fun parseTerm(): Expression {
    var x = parseFactor()
    while (true) {
      when {
        eat('*') -> {
          val a = x
          val b = parseFactor()
          x = Expression { a.eval(it) * b.eval(it) }
        }
        eat('/') -> {
          val a = x
          val b = parseFactor()
          x = Expression { a.eval(it) / b.eval(it) }
        }
        else -> return x
      }
    }
  }

  private fun parseFactor(): Expression {
    if (eat('+')) return parseFactor() // unary plus
    if (eat('-')) {
      val a = parseFactor()
      return Expression { -a.eval(it) }
    }
    var x: Expression
    val startPos = pos
    when {
      eat('(') -> { // parentheses
        x = parseExpression()
        eat(')')
      }
      ch in '0'..'9' || ch == '.' -> { // numbers
        while (ch in '0'..'9' || ch == '.') nextChar()
        val parsedX = str.substring(startPos, pos).toDouble()
        x = Expression { parsedX }
      }
      ch in 'a'..'z' -> { // functions
        while (ch in 'a'..'z') nextChar()
        val name = str.substring(startPos, pos)
        if (name in variables) {
          x = Expression {
            it[name] ?: throw IllegalArgumentException("Missing variable with the name $name")
          }
        } else {
          val a = parseFactor()
          x =
              when (name) {
                "sqrt" -> Expression { sqrt(a.eval(it)) }
                "sin" -> Expression { sin(a.eval(it)) }
                "cos" -> Expression { cos(a.eval(it)) }
                "tan" -> Expression { tan(a.eval(it)) }
                else -> throw RuntimeException("Unknown function: $name")
              }
        }
      }
      else -> {
        throw RuntimeException("Unexpected: '$ch'")
      }
    }

    if (eat('^')) {
      val a = x
      val b = parseFactor()
      x = Expression { a.eval(it).pow(b.eval(it)) }
    }
    return x
  }
}

fun interface Expression {
  fun eval(variables: Map<String, Double>): Double
}
