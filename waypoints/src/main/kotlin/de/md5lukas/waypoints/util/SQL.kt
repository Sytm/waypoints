package de.md5lukas.waypoints.util

import java.sql.ResultSet
import java.util.UUID

fun ResultSet.getUUID(columnLabel: String): UUID? = getString(columnLabel)?.let(UUID::fromString)
