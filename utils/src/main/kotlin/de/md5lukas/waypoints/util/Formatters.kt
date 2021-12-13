package de.md5lukas.waypoints.util

import de.md5lukas.commons.MathHelper
import java.text.CharacterIterator
import java.text.StringCharacterIterator
import java.time.format.DateTimeFormatter
import kotlin.math.abs

fun Double.format(): String = MathHelper.format(this)

object Formatters {
    val SHORT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.uuuu kk:mm")!!
}

// Based on https://stackoverflow.com/a/3758880
fun Long.humanReadableByteCountBin(): String {
    val absoluteBytes = if (this == Long.MIN_VALUE) Long.MAX_VALUE else abs(this)
    if (absoluteBytes < 1024) {
        return "$this B"
    }
    var value = absoluteBytes
    val ci: CharacterIterator = StringCharacterIterator("KMGTPE")
    var i = 40
    while (i >= 0 && absoluteBytes > 0xfffccccccccccccL shr i) {
        value = value shr 10
        ci.next()
        i -= 10
    }
    value *= java.lang.Long.signum(this).toLong()
    return "%.1f %ciB".format(value / 1024.0, ci.current())
}