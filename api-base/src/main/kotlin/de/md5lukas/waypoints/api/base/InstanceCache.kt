package de.md5lukas.waypoints.api.base

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import de.md5lukas.waypoints.api.Folder
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.WaypointsPlayer
import java.util.*

class InstanceCache {

    val playerData: Cache<UUID, WaypointsPlayer> = CacheBuilder.newBuilder().weakValues().build()
    val waypoints: Cache<UUID, Waypoint> = CacheBuilder.newBuilder().weakValues().build()
    val folders: Cache<UUID, Folder> = CacheBuilder.newBuilder().weakValues().build()
}