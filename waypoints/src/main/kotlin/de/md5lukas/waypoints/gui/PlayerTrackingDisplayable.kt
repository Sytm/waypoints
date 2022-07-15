package de.md5lukas.waypoints.gui

import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.gui.GUIDisplayable
import de.md5lukas.waypoints.api.gui.GUIType
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

object PlayerTrackingDisplayable : GUIDisplayable {
    override val type: Type = Type.PUBLIC
    override val guiType: GUIType = GUIType.PUBLIC_HOLDER
    override val name: String = guiType.name
    override val createdAt: OffsetDateTime = OffsetDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC"))
}
