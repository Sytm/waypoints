package de.md5lukas.waypoints.util

import de.md5lukas.waypoints.WaypointsPermissions
import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Folder
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.api.WaypointHolder
import de.md5lukas.waypoints.config.general.CustomIconFilterConfiguration
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player

sealed class CreateResult

object LimitReached : CreateResult()
object NameTaken : CreateResult()

class SuccessWaypoint(val waypoint: Waypoint) : CreateResult()
class SuccessFolder(val folder: Folder) : CreateResult()

fun checkMaterialForCustomIcon(plugin: WaypointsPlugin, material: Material): Boolean {
    if (material == Material.AIR) {
        return false
    }

    val filter = plugin.waypointsConfig.general.customIconFilter
    return when (filter.type) {
        CustomIconFilterConfiguration.FilterType.WHITELIST -> material in filter.materials
        CustomIconFilterConfiguration.FilterType.BLACKLIST -> material !in filter.materials
    }
}

fun createWaypointPrivate(plugin: WaypointsPlugin, player: Player, name: String, location: Location = player.location): CreateResult {
    val waypointsPlayer = plugin.api.getWaypointPlayer(player.uniqueId)

    val waypointLimit = plugin.waypointsConfig.general.waypoints.limit

    if (!player.hasPermission(WaypointsPermissions.UNLIMITED) && waypointLimit > 0 && waypointsPlayer.waypointsAmount >= waypointLimit) {
        plugin.translations.WAYPOINT_LIMIT_REACHED_PRIVATE.send(player)
        return LimitReached
    }
    if (!checkWaypointName(plugin, waypointsPlayer, name)) {
        plugin.translations.WAYPOINT_NAME_DUPLICATE_PRIVATE.send(player)
        return NameTaken
    }

    val waypoint = waypointsPlayer.createWaypoint(name, location)
    plugin.translations.WAYPOINT_SET_SUCCESS_PRIVATE.send(player)

    return SuccessWaypoint(waypoint)
}

fun createWaypointPublic(plugin: WaypointsPlugin, player: Player, name: String, location: Location = player.location): CreateResult {
    if (!checkWaypointName(plugin, plugin.api.publicWaypoints, name)) {
        plugin.translations.WAYPOINT_NAME_DUPLICATE_PUBLIC.send(player)
        return NameTaken
    }

    val waypoint = plugin.api.publicWaypoints.createWaypoint(name, location)
    plugin.translations.WAYPOINT_SET_SUCCESS_PUBLIC.send(player)

    return SuccessWaypoint(waypoint)
}

fun createWaypointPermission(plugin: WaypointsPlugin, player: Player, name: String, permission: String, location: Location = player.location): CreateResult {
    if (!checkWaypointName(plugin, plugin.api.permissionWaypoints, name)) {
        plugin.translations.WAYPOINT_NAME_DUPLICATE_PERMISSION.send(player)
        return NameTaken
    }

    val waypoint = plugin.api.permissionWaypoints.createWaypoint(name, location).also {
        it.permission = permission
    }
    plugin.translations.WAYPOINT_SET_SUCCESS_PERMISSION.send(player)

    return SuccessWaypoint(waypoint)
}

fun createFolderPrivate(plugin: WaypointsPlugin, player: Player, name: String): CreateResult {
    val waypointsPlayer = plugin.api.getWaypointPlayer(player.uniqueId)

    val folderLimit = plugin.waypointsConfig.general.folders.limit

    if (!player.hasPermission(WaypointsPermissions.UNLIMITED) && folderLimit > 0 && waypointsPlayer.foldersAmount >= folderLimit) {
        plugin.translations.FOLDER_LIMIT_REACHED_PRIVATE.send(player)
        return LimitReached
    }
    if (!checkFolderName(plugin, waypointsPlayer, name)) {
        plugin.translations.FOLDER_NAME_DUPLICATE_PRIVATE.send(player)
        return NameTaken
    }

    val folder = waypointsPlayer.createFolder(name)
    plugin.translations.FOLDER_CREATE_SUCCESS_PRIVATE.send(player)

    return SuccessFolder(folder)
}

fun createFolderPublic(plugin: WaypointsPlugin, player: Player, name: String): CreateResult {
    if (!checkFolderName(plugin, plugin.api.publicWaypoints, name)) {
        plugin.translations.FOLDER_NAME_DUPLICATE_PUBLIC.send(player)
        return NameTaken
    }

    val folder = plugin.api.publicWaypoints.createFolder(name)
    plugin.translations.FOLDER_CREATE_SUCCESS_PUBLIC.send(player)

    return SuccessFolder(folder)
}

fun createFolderPermission(plugin: WaypointsPlugin, player: Player, name: String): CreateResult {
    if (!checkFolderName(plugin, plugin.api.permissionWaypoints, name)) {
        plugin.translations.FOLDER_NAME_DUPLICATE_PERMISSION.send(player)
        return NameTaken
    }

    val folder = plugin.api.permissionWaypoints.createFolder(name)
    plugin.translations.FOLDER_CREATE_SUCCESS_PERMISSION.send(player)

    return SuccessFolder(folder)
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

fun Waypoint.copyFieldsTo(waypoint: Waypoint) {
    waypoint.description = this.description
    waypoint.material = this.material
    waypoint.beaconColor = this.beaconColor
    waypoint.description = this.description
}

