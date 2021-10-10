package de.md5lukas.waypoints.util

import de.md5lukas.commons.MathHelper
import java.text.DateFormat
import java.util.*

private val formatter = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault())

fun Long.formatTimestampToDate(): String = formatter.format(Date(this))

fun Double.format(): String = MathHelper.format(this)