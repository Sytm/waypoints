package de.md5lukas.waypoints.api.gui

import de.md5lukas.waypoints.api.Type
import java.time.OffsetDateTime

interface GUIDisplayable {

    val type: Type
    val guiType: GUIType
    val name: String
    val createdAt: OffsetDateTime
}