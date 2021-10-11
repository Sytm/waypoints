package de.md5lukas.waypoints.legacy.internal

import de.md5lukas.nbt.Tags

internal fun setupNBT() {
    Tags.registerExtendedTags()
    Tags.registerTag(::LocationTag)
}