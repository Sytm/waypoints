package de.md5lukas.waypoints.legacy.internal

import de.md5lukas.nbt.NbtIo
import de.md5lukas.nbt.tags.CompoundTag
import java.io.File

internal class BasicPlayerStore(private val tag: CompoundTag) {

    companion object {
        val PLAYER_DATA_FOLDER = File("plugins/Md5Lukas-Commons/playerdata/")

        fun getPlayerStore(file: File): BasicPlayerStore = BasicPlayerStore(
            if (file.exists()) NbtIo.readCompressed(file) else CompoundTag()
        )
    }

    fun getTag(pluginName: String): CompoundTag {
        return tag.getCompound(pluginName) ?: CompoundTag()
    }
}