package de.md5lukas.waypoints.jdbc

import java.io.File
import java.sql.Connection
import java.sql.DriverManager

/**
 * Helper class for getting SQLite connections.
 *
 * @constructor
 * Creates a new SQLiteHelper instance. The SQLite database will use the [file] to store the data.
 */
open class SQLiteHelper(
    private val file: File,
) {
    init {
        try {
            Class.forName("org.sqlite.JDBC")
        } catch (cnfe: ClassNotFoundException) {
            throw IllegalStateException("There is no SQLite driver present")
        }
    }

    private val connectionLazy: Lazy<Connection> = lazy {
        DriverManager.getConnection("jdbc:sqlite:${file.absolutePath}")
    }

    /**
     * Lazily connects to the SQLite database and then stores the connection for later use
     */
    val connection: Connection by connectionLazy

    /**
     * Closes the connection if it is connected to the database file
     */
    fun close() {
        if (!connectionLazy.isInitialized() && !connection.isClosed) {
            synchronized(connection) {
                connection.close()
            }
        }
    }

    /**
     * Checks whether the connection has been closed or not. Does not create a connection if it hasn't been used before
     */
    val isClosed: Boolean
        get() = !connectionLazy.isInitialized() || connection.isClosed
}