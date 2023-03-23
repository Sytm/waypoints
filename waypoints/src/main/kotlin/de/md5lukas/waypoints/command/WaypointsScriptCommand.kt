package de.md5lukas.waypoints.command

import de.md5lukas.commons.uuid.UUIDUtils
import de.md5lukas.waypoints.WaypointsPermissions
import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.BeaconColor
import de.md5lukas.waypoints.util.isLocationOutOfBounds
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.kotlindsl.*
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

class WaypointsScriptCommand(private val plugin: WaypointsPlugin) {

    private val translations = plugin.translations

    fun register() {
        val labelMap = Collections.singletonMap("label", "waypointsscript")

        commandTree("waypointsscript", { it.hasPermission(WaypointsPermissions.COMMAND_SCRIPTING) }) {
            anyExecutor { sender, _ ->
                translations.COMMAND_SCRIPT_HELP_HEADER.send(sender)
                translations.COMMAND_SCRIPT_HELP_DESELECT_WAYPOINT.send(sender, labelMap)
                translations.COMMAND_SCRIPT_HELP_SELECT_WAYPOINT.send(sender, labelMap)
                translations.COMMAND_SCRIPT_HELP_TEMPORARY_WAYPOINT.send(sender, labelMap)
            }
            literalArgument("deselectWaypoint") {
                playerArgument("player") {
                    anyExecutor { _, args ->
                        val player = args[0] as Player

                        plugin.api.pointerManager.disable(player)
                    }
                }
            }
            literalArgument("selectWaypoint") {
                playerArgument("player") {
                    stringArgument("waypoint-id") {
                        anyExecutor { sender, args ->
                            val player = args[0] as Player
                            val uuidString = args[1] as String

                            if (!UUIDUtils.isUUID(uuidString)) {
                                translations.COMMAND_SCRIPT_INVALID_UUID.send(sender, Collections.singletonMap("uuid", uuidString))
                                return@anyExecutor
                            }

                            val waypoint = plugin.api.getWaypointByID(UUID.fromString(uuidString))

                            if (waypoint == null) {
                                translations.COMMAND_SCRIPT_SELECT_WAYPOINT_WAYPOINT_NOT_FOUND.send(sender, Collections.singletonMap("uuid", uuidString))
                                return@anyExecutor
                            }

                            plugin.api.pointerManager.enable(player, waypoint)
                        }
                    }
                }
            }
            literalArgument("temporaryWaypoint") {
                playerArgument("player") {
                    locationArgument("target") {
                        anyExecutor { sender, args ->
                            val player = args[0] as Player
                            val target = args[1] as Location

                            if (isLocationOutOfBounds(plugin, target)) {
                                translations.WAYPOINT_CREATE_COORDINATES_OUT_OF_BOUNDS.send(sender)
                            } else {
                                plugin.api.pointerManager.let {
                                    it.enable(player, it.temporaryWaypointTrackableOf(target))
                                }
                            }
                        }
                        argument(
                            StringArgument("beaconcolor")
                                .replaceSuggestions(ArgumentSuggestions.strings(BeaconColor.values().map { it.name }.toList()))
                        ) {
                            anyExecutor { sender, args ->
                                val player = args[0] as Player
                                val target = args[1] as Location
                                val beaconColorString = args[2] as String

                                val beaconColor = BeaconColor.values().firstOrNull { it.name.equals(beaconColorString, true) }
                                if (beaconColor === null) {
                                    translations.COMMAND_SCRIPT_TEMPORARY_WAYPOINT_BEACON_COLOR_NOT_FOUND.send(sender)
                                    return@anyExecutor
                                }

                                if (isLocationOutOfBounds(plugin, target)) {
                                    translations.WAYPOINT_CREATE_COORDINATES_OUT_OF_BOUNDS.send(sender)
                                } else {
                                    plugin.api.pointerManager.let {
                                        it.enable(player, it.temporaryWaypointTrackableOf(target, beaconColor))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}