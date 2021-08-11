package de.md5lukas.waypoints.jdbc

import org.intellij.lang.annotations.Language
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * This function prepares a statement, inserts the values into the [PreparedStatement], executes it, calls [block] on the first row and returns the value.
 */
inline fun <T> Connection.selectFirst(@Language("SQLite") query: String, vararg values: Any?, block: ResultSet.() -> T): T? =
    prepareStatement(query).setValues(values).executeQuery().use {
        if (it.next()) {
            it.block()
        } else {
            null
        }
    }

/**
 * This function prepares a statement, inserts the values into the [PreparedStatement], executes it, calls [block] for each row and collects the returned
 * values in a list.
 */
inline fun <T> Connection.select(@Language("SQLite") query: String, vararg values: Any?, block: ResultSet.() -> T): List<T> =
    prepareStatement(query).setValues(values).executeQuery().use {
        val result = mutableListOf<T>()

        while (it.next()) {
            result.add(it.block())
        }

        result
    }

/**
 * This function prepares a statement, inserts the values into the [PreparedStatement] and executes it.
 */
fun Connection.update(@Language("SQLite") sql: String, vararg values: Any?): Int =
    prepareStatement(sql).setValues(values).executeUpdate()

/**
 * This function inserts the provided [values] into the [PreparedStatement]
 */
fun PreparedStatement.setValues(vararg values: Any?): PreparedStatement {
    values.forEachIndexed { arrayIndex, value ->
        val index = arrayIndex + 1

        this.setObject(index, value)
    }

    return this
}

