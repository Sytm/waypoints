package de.md5lukas.waypoints.command

import com.okkero.skedule.skedule
import de.md5lukas.commons.paper.isOutOfBounds
import de.md5lukas.commons.paper.placeholder
import de.md5lukas.waypoints.WaypointsPermissions
import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.pointers.BeaconColor
import de.md5lukas.waypoints.pointers.TemporaryWaypointTrackable
import de.md5lukas.waypoints.pointers.WaypointTrackable
import de.md5lukas.waypoints.util.labelResolver
import de.md5lukas.waypoints.util.searchWaypoints
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.arguments.LocationType
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.argument
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.locationArgument
import dev.jorel.commandapi.kotlindsl.optionalArgument
import dev.jorel.commandapi.kotlindsl.playerArgument
import dev.jorel.commandapi.kotlindsl.uuidArgument
import java.util.UUID
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Location
import org.bukkit.entity.Player

class WaypointsScriptCommand(private val plugin: WaypointsPlugin) {

  private val translations
    get() = plugin.translations

  fun register() {
    commandTree("waypointsscript") {
      withPermission(WaypointsPermissions.COMMAND_SCRIPTING)
      withAliases(*plugin.waypointsConfig.general.commands.waypointsScriptAliases.toTypedArray())
      anyExecutor { sender, args ->
        val labelResolver = args.labelResolver
        translations.COMMAND_SCRIPT_HELP_HEADER.send(sender)
        translations.COMMAND_SCRIPT_HELP_DESELECT_WAYPOINT.send(sender, labelResolver)
        translations.COMMAND_SCRIPT_HELP_SELECT_WAYPOINT.send(sender, labelResolver)
        translations.COMMAND_SCRIPT_HELP_TEMPORARY_WAYPOINT.send(sender, labelResolver)
        translations.COMMAND_SCRIPT_HELP_UUID.send(sender, labelResolver)
      }
      literalArgument("deselectWaypoint") {
        playerArgument("player") {
          anyExecutor { _, args ->
            val player = args["player"] as Player

            plugin.pointerManager.disable(player) { true }
          }
        }
      }
      literalArgument("selectWaypoint") {
        playerArgument("player") {
          uuidArgument("waypoint-id") {
            anyExecutor { sender, args ->
              val player = args["player"] as Player
              val uuid = args["waypoint-id"] as UUID

              plugin.skedule(player) {
                val waypoint = plugin.api.getWaypointByID(uuid)

                if (waypoint == null) {
                  translations.COMMAND_SCRIPT_SELECT_WAYPOINT_WAYPOINT_NOT_FOUND.send(
                      sender, "uuid" placeholder uuid.toString())
                  return@skedule
                }

                plugin.pointerManager.enable(player, WaypointTrackable(plugin, waypoint))
              }
            }
          }
        }
      }
      literalArgument("temporaryWaypoint") {
        playerArgument("player") {
          locationArgument("target", LocationType.BLOCK_POSITION) {
            optionalArgument(
                StringArgument("beacon-color")
                    .replaceSuggestions(
                        ArgumentSuggestions.strings(
                            BeaconColor.entries.map { it.name }.toList()))) {
                  anyExecutor { sender, args ->
                    val player = args["player"] as Player
                    val target = args["target"] as Location
                    val beaconColorString = args["beacon-color"] as String?

                    val beaconColor =
                        BeaconColor.entries.firstOrNull { it.name.equals(beaconColorString, true) }
                    if (beaconColorString !== null && beaconColor === null) {
                      translations.COMMAND_SCRIPT_TEMPORARY_WAYPOINT_BEACON_COLOR_NOT_FOUND.send(
                          sender)
                      return@anyExecutor
                    }

                    if (target.isOutOfBounds) {
                      translations.WAYPOINT_CREATE_COORDINATES_OUT_OF_BOUNDS.send(sender)
                    } else {
                      plugin.pointerManager.enable(
                          player, TemporaryWaypointTrackable(plugin, target, beaconColor))
                    }
                  }
                }
          }
        }
      }
      literalArgument("uuid") {
        argument(
            GreedyStringArgument("query")
                .replaceSuggestions(
                    WaypointsArgumentSuggestions(plugin, textMode = false, allowGlobals = true))) {
              anyExecutor { sender, args ->
                plugin.skedule {
                  val result = searchWaypoints(plugin, sender, args["query"] as String, true)
                  if (result.isEmpty()) {
                    translations.COMMAND_SCRIPT_UUID_NO_MATCH.send(sender)
                  } else {
                    translations.COMMAND_SCRIPT_UUID_HEADER.send(sender)
                    result.forEach {
                      sender.sendMessage(
                          translations.COMMAND_SCRIPT_UUID_RESULT.withReplacements(
                                  "name" placeholder it.name,
                                  "folder" placeholder (it.getFolder()?.name ?: "null"),
                              )
                              .clickEvent(
                                  ClickEvent.clickEvent(
                                      ClickEvent.Action.COPY_TO_CLIPBOARD, it.id.toString())))
                    }
                  }
                }
              }
            }
      }
    }
  }
}
