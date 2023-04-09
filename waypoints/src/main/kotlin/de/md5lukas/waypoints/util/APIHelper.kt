package de.md5lukas.waypoints.util

import de.md5lukas.waypoints.WaypointsPermissions
import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.*
import de.md5lukas.waypoints.config.general.FilterType
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

sealed class CreateResult

object LimitReached : CreateResult()
object NameTaken : CreateResult()
object LocationOutOfBounds : CreateResult()
object WorldUnavailable : CreateResult()

class SuccessWaypoint(val waypoint: Waypoint) : CreateResult()
class SuccessFolder(val folder: Folder) : CreateResult()

fun checkMaterialForCustomIcon(plugin: WaypointsPlugin, material: Material): Boolean {
    if (material == Material.AIR) {
        return false
    }

    val filter = plugin.waypointsConfig.general.customIconFilter
    return when (filter.type) {
        FilterType.WHITELIST -> material in filter.materials
        FilterType.BLACKLIST -> material !in filter.materials
    }
}

fun checkWorldAvailability(plugin: WaypointsPlugin, world: World): Boolean {
    val config = plugin.waypointsConfig.general.availableWorldsConfiguration
    return when (config.type) {
        FilterType.WHITELIST -> world.name.lowercase() in config.worlds
        FilterType.BLACKLIST -> world.name.lowercase() !in config.worlds
    }
}

fun createWaypointPrivate(plugin: WaypointsPlugin, player: Player, name: String, location: Location = player.location): CreateResult {
    creationPreChecks(plugin, player, location)?.let {
        return it
    }

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

    checkVisited(plugin, waypoint, player)

    return SuccessWaypoint(waypoint)
}

fun createWaypointPublic(plugin: WaypointsPlugin, player: Player, name: String, location: Location = player.location): CreateResult {
    creationPreChecks(plugin, player, location)?.let {
        return it
    }

    if (!checkWaypointName(plugin, plugin.api.publicWaypoints, name)) {
        plugin.translations.WAYPOINT_NAME_DUPLICATE_PUBLIC.send(player)
        return NameTaken
    }

    val waypoint = plugin.api.publicWaypoints.createWaypoint(name, location)
    plugin.translations.WAYPOINT_SET_SUCCESS_PUBLIC.send(player)

    checkVisited(plugin, waypoint, player)

    return SuccessWaypoint(waypoint)
}

fun createWaypointPermission(plugin: WaypointsPlugin, player: Player, name: String, permission: String, location: Location = player.location): CreateResult {
    creationPreChecks(plugin, player, location)?.let {
        return it
    }

    if (!checkWaypointName(plugin, plugin.api.permissionWaypoints, name)) {
        plugin.translations.WAYPOINT_NAME_DUPLICATE_PERMISSION.send(player)
        return NameTaken
    }

    val waypoint = plugin.api.permissionWaypoints.createWaypoint(name, location).also {
        it.permission = permission
    }
    plugin.translations.WAYPOINT_SET_SUCCESS_PERMISSION.send(player)

    checkVisited(plugin, waypoint, player)

    return SuccessWaypoint(waypoint)
}

private fun creationPreChecks(plugin: WaypointsPlugin, player: Player, location: Location): CreateResult? {
    if (!player.hasPermission(WaypointsPermissions.MODIFY_ANYWHERE) && !checkWorldAvailability(plugin, location.world!!)) {
        plugin.translations.WAYPOINT_CREATE_WORLD_UNAVAILABLE.send(player)
        return WorldUnavailable
    }

    if (isLocationOutOfBounds(location)) {
        plugin.translations.WAYPOINT_CREATE_COORDINATES_OUT_OF_BOUNDS.send(player)
        return LocationOutOfBounds
    }
    return null
}

private fun checkVisited(plugin: WaypointsPlugin, waypoint: Waypoint, player: Player) {
    if (player.world === waypoint.location.world
        && player.location.distanceSquared(waypoint.location) <= plugin.waypointsConfig.general.teleport.visitedRadius
    ) {
        waypoint.getWaypointMeta(player.uniqueId).visited = true
    }
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


private fun searchWaypoints0(plugin: WaypointsPlugin, sender: CommandSender, query: String, allowGlobals: Boolean): List<SearchResult<out Waypoint>> {
    val publicPrefix = plugin.translations.COMMAND_SEARCH_PREFIX_PUBLIC.rawText + "/"
    val permissionPrefix = plugin.translations.COMMAND_SEARCH_PREFIX_PERMISSION.rawText + "/"

    val (strippedQuery, holder) = if (allowGlobals && query.startsWith(publicPrefix)) {
        query.removePrefix(publicPrefix) to plugin.api.publicWaypoints
    } else if (allowGlobals && query.startsWith(permissionPrefix)) {
        query.removePrefix(permissionPrefix) to plugin.api.permissionWaypoints
    } else if (sender is Player) {
        query to plugin.api.getWaypointPlayer(sender.uniqueId)
    } else {
        return emptyList()
    }

    return holder.searchWaypoints(strippedQuery, sender)
}

fun searchWaypoints(plugin: WaypointsPlugin, sender: CommandSender, query: String, allowGlobals: Boolean): List<Waypoint> =
    searchWaypoints0(plugin, sender, query, allowGlobals).map { it.t }.toList()

fun searchWaypoint(plugin: WaypointsPlugin, sender: CommandSender, query: String, allowGlobals: Boolean): Waypoint? =
    searchWaypoints0(plugin, sender, query, allowGlobals).firstOrNull()?.t

fun Waypoint.copyFieldsTo(waypoint: Waypoint) {
    waypoint.description = this.description
    waypoint.material = this.material
    waypoint.beaconColor = this.beaconColor
    waypoint.description = this.description
}

