package de.md5lukas.waypoints.command

import de.md5lukas.waypoints.WaypointsPermissions
import de.md5lukas.waypoints.WaypointsPlugin
import dev.jorel.commandapi.arguments.LiteralArgument.of
import dev.jorel.commandapi.kotlindsl.*
import org.bukkit.Location
import org.bukkit.OfflinePlayer

class NewWaypointsCommand(plugin: WaypointsPlugin) {

    fun register() {
        commandTree("waypoints2") {
            playerExecutor { player, _ ->
                println("GUI")
            }
            literalArgument("help") {
                anyExecutor { commandSender, _ ->
                    println("Help")
                }
            }
            requirement(of("set"), { it.hasPermission(WaypointsPermissions.MODIFY_PRIVATE) }) {
                greedyStringArgument("name") {
                    playerExecutor { player, args ->
                        val name = args[0] as String

                        println("Private set $name")
                    }
                }
            }
            requirement(of("setPublic"), { it.hasPermission(WaypointsPermissions.MODIFY_PUBLIC) }) {
                greedyStringArgument("name") {
                    playerExecutor { player, args ->
                        val name = args[0] as String

                        println("Public set $name")
                    }
                }
            }
            requirement(of("setPermission"), { it.hasPermission(WaypointsPermissions.MODIFY_PERMISSION) }) {
                greedyStringArgument("name") {
                    playerExecutor { player, args ->
                        val name = args[0] as String

                        println("Permissions set $name")
                    }
                }
            }
            requirement(of("setTemporary"), { it.hasPermission(WaypointsPermissions.TEMPORARY_WAYPOINT) }) {
                locationArgument("target") {
                    playerExecutor { player, args ->
                        val location = args[0] as Location

                        println("Temporary set $location")
                    }
                }
            }
            requirement(of("other"), { it.hasPermission(WaypointsPermissions.COMMAND_OTHER) }) {
                offlinePlayerArgument("target") {
                    playerExecutor { player, args ->
                        val otherPlayer = args[0] as OfflinePlayer

                        println("Temporary set ${otherPlayer.uniqueId}")
                    }
                }
            }
            requirement(of("statistics"), { it.hasPermission(WaypointsPermissions.COMMAND_STATISTICS) }) {
                anyExecutor { commandSender, anies ->
                    println("Statistics")
                }
            }
            requirement(of("reload"), { it.hasPermission(WaypointsPermissions.COMMAND_RELOAD) }) {
                anyExecutor { commandSender, anies ->
                    println("Reload")
                }
            }
        }
    }
}