package de.md5lukas.waypoints.api

interface Statistics {

    val totalWaypoints: Int

    val privateWaypoints: Int

    val deathWaypoints: Int

    val publicWaypoints: Int

    val permissionWaypoints: Int

    val totalFolders: Int

    val privateFolders: Int

    val publicFolders: Int

    val permissionFolders: Int

    val databaseSize: Long
}