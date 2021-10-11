package de.md5lukas.waypoints.legacy.internal

import de.md5lukas.nbt.tags.CompoundTag

internal class LegacyPlayerSettings private constructor(var showGlobals: Boolean, var sortMode: LegacySortMode) {

    constructor() : this(true, LegacySortMode.TYPE)

    constructor(tag: CompoundTag) : this(tag.getBoolean("showGlobals"), LegacySortMode.valueOf(tag.getString("sortMode")))

    fun toCompoundTag(): CompoundTag? {
        val tag = CompoundTag()
        tag.putBoolean("showGlobals", showGlobals)
        tag.putString("sortMode", sortMode.name)
        return tag
    }
}