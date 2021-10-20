package de.md5lukas.waypoints.legacy

import de.md5lukas.nbt.NbtIo
import de.md5lukas.nbt.tags.CompoundTag
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger

internal class LegacyGlobalStore(
    private val logger: Logger
) {

    private val globalStoreFile = File("plugins/Waypoints/globalstore.nbt")

    var publicFolder: LegacyPublicFolder? = null
        private set
    var permissionFolder: LegacyPermissionFolder? = null
        private set

    fun load() = if (globalStoreFile.exists() && globalStoreFile.length() > 0L) {
        try {
            val tag: CompoundTag = NbtIo.readCompressed(globalStoreFile)

            if ("publicWaypoints" in tag) {
                publicFolder = LegacyPublicFolder(tag.getCompound("publicWaypoints"))
            }
            if ("permissionWaypoints" in tag) {
                permissionFolder = LegacyPermissionFolder(tag.getCompound("permissionWaypoints"))
            }
            true
        } catch (t: Throwable) {
            logger.log(Level.WARNING, "Could not load legacy global store file", t)
            false
        }
    } else {
        logger.log(Level.INFO, "Could not find globalstore at ${globalStoreFile.absolutePath}")
        false
    }
}