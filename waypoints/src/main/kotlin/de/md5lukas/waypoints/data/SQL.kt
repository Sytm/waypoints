package de.md5lukas.waypoints.data

import java.sql.ResultSet
import java.util.UUID

fun ResultSet.getUUID(columnLabel: String): UUID? = getString(columnLabel)?.let(UUID::fromString)
