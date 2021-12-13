package de.md5lukas.waypoints.util

fun Array<out String>.join(offset: Int = 0): String {
    val builder = StringBuilder()
    for ((count, element) in this.withIndex()) {
        if (count > offset) {
            builder.append(' ')
        }
        if (count >= offset)
            builder.append(element)
    }
    return builder.toString()
}