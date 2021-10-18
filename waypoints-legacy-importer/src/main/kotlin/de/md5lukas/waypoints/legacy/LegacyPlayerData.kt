package de.md5lukas.waypoints.legacy

import de.md5lukas.nbt.tags.CompoundTag

internal class LegacyPlayerData(
    private val store: BasicPlayerStore
) {

    private val rootTag = store.getTag("Waypoints")
    val settings: LegacyPlayerSettings
    val folders: List<LegacyPrivateFolder>
    val waypoints: List<LegacyPrivateWaypoint>

    val deathWaypoint: LegacyDeathWaypoint? = if ("deathWaypoint" in rootTag) {
        LegacyDeathWaypoint(rootTag.getCompound("deathWaypoint"))
    } else null

    init {
        if ("settings" in rootTag) {
            settings = LegacyPlayerSettings(rootTag.getCompound("settings"))
            folders = rootTag.getList("folders").values().map { LegacyPrivateFolder(it as CompoundTag) }
            waypoints = rootTag.getList("waypoints").values().map { LegacyPrivateWaypoint(it as CompoundTag) }
        } else {
            settings = LegacyPlayerSettings()
            folders = emptyList()
            waypoints = emptyList()
        }
    }
}