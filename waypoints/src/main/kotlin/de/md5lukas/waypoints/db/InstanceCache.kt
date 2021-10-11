package de.md5lukas.waypoints.db

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import de.md5lukas.waypoints.api.Folder
import de.md5lukas.waypoints.api.Waypoint
import java.util.*

class InstanceCache {

    val waypoints: Cache<UUID, Waypoint> = CacheBuilder.newBuilder().weakValues().build()
    val folders: Cache<UUID, Folder> = CacheBuilder.newBuilder().weakValues().build()
}