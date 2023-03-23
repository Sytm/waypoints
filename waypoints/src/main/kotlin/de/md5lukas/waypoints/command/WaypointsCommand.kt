package de.md5lukas.waypoints.command

import de.md5lukas.waypoints.WaypointsPermissions
import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.gui.WaypointsGUI
import de.md5lukas.waypoints.util.createWaypointPermission
import de.md5lukas.waypoints.util.createWaypointPrivate
import de.md5lukas.waypoints.util.createWaypointPublic
import de.md5lukas.waypoints.util.humanReadableByteCountBin
import dev.jorel.commandapi.arguments.LiteralArgument.of
import dev.jorel.commandapi.kotlindsl.*
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

class WaypointsCommand(private val plugin: WaypointsPlugin) {

    private val translations = plugin.translations

    fun register() {
        val labelMap = Collections.singletonMap("label", "waypoints")

        commandTree("waypoints") {
            playerExecutor { player, _ ->
                WaypointsGUI(plugin, player, player.uniqueId)
            }
            anyExecutor { sender, _ ->
                translations.COMMAND_NOT_A_PLAYER.send(sender)
            }
            literalArgument("help") {
                anyExecutor { sender, _ ->
                    translations.COMMAND_HELP_HEADER.send(sender)

                    if (sender is Player)
                        translations.COMMAND_HELP_GUI.send(sender, labelMap)

                    translations.COMMAND_HELP_HELP.send(sender, labelMap)

                    if (sender is Player) {
                        if (sender.hasPermission(WaypointsPermissions.MODIFY_PRIVATE)) {
                            translations.COMMAND_HELP_SET_PRIVATE.send(sender, labelMap)
                        }
                        if (plugin.waypointsConfig.general.features.globalWaypoints) {
                            if (sender.hasPermission(WaypointsPermissions.MODIFY_PUBLIC)) {
                                translations.COMMAND_HELP_SET_PUBLIC.send(sender, labelMap)
                            }
                            if (sender.hasPermission(WaypointsPermissions.MODIFY_PERMISSION)) {
                                translations.COMMAND_HELP_SET_PERMISSION.send(sender, labelMap)
                            }
                        }
                        if (sender.hasPermission(WaypointsPermissions.TEMPORARY_WAYPOINT)) {
                            translations.COMMAND_HELP_SET_TEMPORARY.send(sender, labelMap)
                        }
                        if (sender.hasPermission(WaypointsPermissions.COMMAND_OTHER)) {
                            translations.COMMAND_HELP_OTHER.send(sender, labelMap)
                        }
                    }
                    if (sender.hasPermission(WaypointsPermissions.COMMAND_STATISTICS)) {
                        translations.COMMAND_HELP_STATISTICS.send(sender, labelMap)
                    }
                    if (sender.hasPermission(WaypointsPermissions.COMMAND_RELOAD)) {
                        translations.COMMAND_HELP_RELOAD.send(sender, labelMap)
                    }
                }
            }
            requirement(of("set"), { it.hasPermission(WaypointsPermissions.MODIFY_PRIVATE) }) {
                greedyStringArgument("name") {
                    playerExecutor { player, args ->
                        val name = args[0] as String

                        createWaypointPrivate(plugin, player, name)
                    }
                    anyExecutor { sender, _ ->
                        translations.COMMAND_NOT_A_PLAYER.send(sender)
                    }
                }
            }
            if (plugin.waypointsConfig.general.features.globalWaypoints) {
                requirement(of("setPublic"), { it.hasPermission(WaypointsPermissions.MODIFY_PUBLIC) }) {
                    greedyStringArgument("name") {
                        playerExecutor { player, args ->
                            val name = args[0] as String

                            createWaypointPublic(plugin, player, name)
                        }
                        anyExecutor { sender, _ ->
                            translations.COMMAND_NOT_A_PLAYER.send(sender)
                        }
                    }
                }
                requirement(of("setPermission"), { it.hasPermission(WaypointsPermissions.MODIFY_PERMISSION) }) {
                    stringArgument("permission") {
                        greedyStringArgument("name") {
                            playerExecutor { player, args ->
                                val permission = args[0] as String
                                val name = args[1] as String

                                createWaypointPermission(plugin, player, name, permission)
                            }
                            anyExecutor { sender, _ ->
                                translations.COMMAND_NOT_A_PLAYER.send(sender)
                            }
                        }
                    }
                }
            }
            requirement(of("setTemporary"), { it.hasPermission(WaypointsPermissions.TEMPORARY_WAYPOINT) }) {
                locationArgument("target") {
                    playerExecutor { player, args ->
                        val location = args[0] as Location

                        plugin.api.pointerManager.let {
                            it.enable(player, it.temporaryWaypointTrackableOf(location))
                        }
                    }
                    anyExecutor { sender, _ ->
                        translations.COMMAND_NOT_A_PLAYER.send(sender)
                    }
                }
            }
            requirement(of("other"), { it.hasPermission(WaypointsPermissions.COMMAND_OTHER) }) {
                offlinePlayerArgument("target") {
                    playerExecutor { player, args ->
                        val otherUUID = (args[0] as OfflinePlayer).uniqueId

                        if (!plugin.api.waypointsPlayerExists(otherUUID)) {
                            translations.COMMAND_OTHER_PLAYER_NO_WAYPOINTS.send(player)
                        } else {
                            WaypointsGUI(plugin, player, otherUUID)
                        }
                    }
                    anyExecutor { sender, _ ->
                        translations.COMMAND_NOT_A_PLAYER.send(sender)
                    }
                }
            }
            requirement(of("statistics"), { it.hasPermission(WaypointsPermissions.COMMAND_STATISTICS) }) {
                anyExecutor { sender, _ ->
                    with(plugin.api.statistics) {
                        translations.COMMAND_STATISTICS_MESSAGE.send(
                            sender,
                            mapOf(
                                "dbFileSize" to databaseSize.humanReadableByteCountBin(),

                                "totalWaypoints" to totalWaypoints.toString(),
                                "privateWaypoints" to privateWaypoints.toString(),
                                "deathWaypoints" to deathWaypoints.toString(),
                                "publicWaypoints" to publicWaypoints.toString(),
                                "permissionWaypoints" to permissionWaypoints.toString(),

                                "totalFolders" to totalFolders.toString(),
                                "privateFolders" to privateFolders.toString(),
                                "publicFolders" to publicFolders.toString(),
                                "permissionFolders" to permissionFolders.toString(),
                            )
                        )
                    }
                }
            }
            requirement(of("reload"), { it.hasPermission(WaypointsPermissions.COMMAND_RELOAD) }) {
                anyExecutor { sender, _ ->
                    plugin.reloadConfiguration()
                    translations.COMMAND_RELOAD_FINISHED.send(sender)
                }
            }
        }
    }
}