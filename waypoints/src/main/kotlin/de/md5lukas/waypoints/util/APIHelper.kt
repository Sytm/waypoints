package de.md5lukas.waypoints.util

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.WaypointHolder
import de.md5lukas.waypoints.config.general.CustomIconFilterConfiguration
import org.bukkit.Material
import org.bukkit.entity.Player

fun checkMaterialForCustomIcon(plugin: WaypointsPlugin, material: Material): Boolean {
    val filter = plugin.waypointsConfig.general.customIconFilter
    return when (filter.type) {
        CustomIconFilterConfiguration.FilterType.WHITELIST -> material in filter.materials
        CustomIconFilterConfiguration.FilterType.BLACKLIST -> material !in filter.materials
    }
}

fun createWaypointPrivate(plugin: WaypointsPlugin, player: Player, name: String): Boolean {
    val waypointsPlayer = plugin.api.getWaypointPlayer(player.uniqueId)

    val waypointLimit = plugin.waypointsConfig.general.waypoints.limit

    if (waypointLimit > 0 && waypointsPlayer.waypointsAmount >= waypointLimit) {
        plugin.translations.WAYPOINT_LIMIT_REACHED_PRIVATE.send(player)
        return false
    }
    if (!checkWaypointName(plugin, waypointsPlayer, name)) {
        plugin.translations.WAYPOINT_NAME_DUPLICATE_PRIVATE.send(player)
        return false
    }

    waypointsPlayer.createWaypoint(name, player.location)
    plugin.translations.WAYPOINT_SET_SUCCESS_PRIVATE.send(player)

    return true
}

fun createWaypointPublic(plugin: WaypointsPlugin, player: Player, name: String): Boolean {
    if (!checkWaypointName(plugin, plugin.api.publicWaypoints, name)) {
        plugin.translations.WAYPOINT_NAME_DUPLICATE_PUBLIC.send(player)
        return false
    }

    plugin.api.publicWaypoints.createWaypoint(name, player.location)
    plugin.translations.WAYPOINT_SET_SUCCESS_PUBLIC.send(player)

    return true
}

fun createWaypointPermission(plugin: WaypointsPlugin, player: Player, name: String, permission: String): Boolean {
    if (!checkWaypointName(plugin, plugin.api.permissionWaypoints, name)) {
        plugin.translations.WAYPOINT_NAME_DUPLICATE_PERMISSION.send(player)
        return false
    }

    plugin.api.permissionWaypoints.createWaypoint(name, player.location).also {
        it.permission = permission
    }
    plugin.translations.WAYPOINT_SET_SUCCESS_PERMISSION.send(player)

    return true
}

fun createFolderPrivate(plugin: WaypointsPlugin, player: Player, name: String): Boolean {
    val waypointsPlayer = plugin.api.getWaypointPlayer(player.uniqueId)

    val folderLimit = plugin.waypointsConfig.general.folders.limit

    if (folderLimit > 0 && waypointsPlayer.foldersAmount >= folderLimit) {
        plugin.translations.FOLDER_LIMIT_REACHED_PRIVATE.send(player)
        return false
    }
    if (!checkFolderName(plugin, waypointsPlayer, name)) {
        plugin.translations.FOLDER_NAME_DUPLICATE_PRIVATE.send(player)
        return false
    }

    waypointsPlayer.createFolder(name)
    plugin.translations.FOLDER_CREATE_SUCCESS_PRIVATE.send(player)

    return true
}

fun createFolderPublic(plugin: WaypointsPlugin, player: Player, name: String): Boolean {
    if (!checkFolderName(plugin, plugin.api.publicWaypoints, name)) {
        plugin.translations.FOLDER_NAME_DUPLICATE_PUBLIC.send(player)
        return false
    }

    plugin.api.publicWaypoints.createFolder(name)
    plugin.translations.FOLDER_CREATE_SUCCESS_PUBLIC.send(player)

    return true
}

fun createFolderPermission(plugin: WaypointsPlugin, player: Player, name: String): Boolean {
    if (!checkFolderName(plugin, plugin.api.permissionWaypoints, name)) {
        plugin.translations.FOLDER_NAME_DUPLICATE_PERMISSION.send(player)
        return false
    }

    plugin.api.permissionWaypoints.createFolder(name)
    plugin.translations.FOLDER_CREATE_SUCCESS_PERMISSION.send(player)

    return true
}

fun checkWaypointName(plugin: WaypointsPlugin, holder: WaypointHolder, name: String): Boolean {
    if (when (holder.type) {
            Type.PRIVATE -> plugin.waypointsConfig.general.waypoints.allowDuplicateNamesPrivate
            Type.PUBLIC -> plugin.waypointsConfig.general.waypoints.allowDuplicateNamesPublic
            Type.PERMISSION -> plugin.waypointsConfig.general.waypoints.allowDuplicateNamesPermission
            else -> throw IllegalArgumentException("Waypoints of the type ${holder.type} have no name")
        }
    ) {
        return true
    }

    return !holder.isDuplicateWaypointName(name)
}

fun checkFolderName(plugin: WaypointsPlugin, holder: WaypointHolder, name: String): Boolean {
    if (when (holder.type) {
            Type.PRIVATE -> plugin.waypointsConfig.general.folders.allowDuplicateNamesPrivate
            Type.PUBLIC -> plugin.waypointsConfig.general.folders.allowDuplicateNamesPublic
            Type.PERMISSION -> plugin.waypointsConfig.general.folders.allowDuplicateNamesPermission
            else -> throw IllegalArgumentException("Folders of the type ${holder.type} have no name")
        }
    ) {
        return true
    }

    return !holder.isDuplicateFolderName(name)
}

