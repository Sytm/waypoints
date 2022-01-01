package de.md5lukas.waypoints.command

import de.md5lukas.commons.uuid.UUIDUtils
import de.md5lukas.waypoints.WaypointsPermissions
import de.md5lukas.waypoints.WaypointsPlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.util.*

class WaypointsScriptCommand(private val plugin: WaypointsPlugin) : CommandExecutor {

    private val translations = plugin.translations

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission(WaypointsPermissions.COMMAND_SCRIPTING)) {
            translations.COMMAND_NO_PERMISSION.send(sender)
            return true
        }
        val labelMap = Collections.singletonMap("label", label)
        if (args.isEmpty()) {
            translations.COMMAND_SCRIPT_HELP_HEADER.send(sender)
            translations.COMMAND_SCRIPT_HELP_DESELECT_WAYPOINT.send(sender, labelMap)
            translations.COMMAND_SCRIPT_HELP_SELECT_WAYPOINT.send(sender, labelMap)
            return true
        }
        when (args[0].lowercase()) {
            "deselectwaypoint" -> when {
                args.size <= 1 -> translations.COMMAND_SCRIPT_DESELECT_WAYPOINT_WRONG_USAGE.send(sender, labelMap)
                else -> {
                    val player = plugin.server.getPlayerExact(args[1])

                    if (player == null) {
                        translations.COMMAND_SCRIPT_PLAYER_NOT_FOUND.send(sender, Collections.singletonMap("name", args[1]))
                        return true
                    }

                    plugin.api.pointerManager.disable(player)
                }
            }
            "selectwaypoint" -> when {
                args.size <= 2 -> translations.COMMAND_SCRIPT_SELECT_WAYPOINT_WRONG_USAGE.send(sender, labelMap)
                else -> {
                    val player = plugin.server.getPlayerExact(args[1])

                    if (player == null) {
                        translations.COMMAND_SCRIPT_PLAYER_NOT_FOUND.send(sender, Collections.singletonMap("name", args[1]))
                        return true
                    }

                    val uuidString = args[2]
                    if (!UUIDUtils.isUUID(uuidString)) {
                        translations.COMMAND_SCRIPT_INVALID_UUID.send(sender, Collections.singletonMap("uuid", uuidString))
                        return true
                    }

                    val waypoint = plugin.api.getWaypointByID(UUID.fromString(uuidString))

                    if (waypoint == null) {
                        translations.COMMAND_SCRIPT_SELECT_WAYPOINT_WAYPOINT_NOT_FOUND.send(sender, Collections.singletonMap("uuid", uuidString))
                        return true
                    }

                    plugin.api.pointerManager.enable(player, waypoint)
                }
            }
            else -> translations.COMMAND_NOT_FOUND.send(sender)
        }
        return true
    }
}