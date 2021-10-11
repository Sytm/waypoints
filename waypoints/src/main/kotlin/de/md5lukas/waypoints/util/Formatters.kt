package de.md5lukas.waypoints.util

import de.md5lukas.commons.MathHelper
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun Double.format(): String = MathHelper.format(this)

object Formatters {
    val SHORT_DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)!!
}
