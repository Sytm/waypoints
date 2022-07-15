package de.md5lukas.waypoints.command

import de.md5lukas.commons.uuid.UUIDUtils
import de.md5lukas.waypoints.WaypointsPermissions
import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.gui.WaypointsGUI
import de.md5lukas.waypoints.legacy.LegacyImporter
import de.md5lukas.waypoints.util.*
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import java.util.*
import java.util.logging.Level

class WaypointsCommand(private val plugin: WaypointsPlugin) : CommandExecutor, TabCompleter {

    private val translations = plugin.translations

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            translations.COMMAND_NOT_A_PLAYER.send(sender)
            return true
        }
        if (!sender.hasPermission(WaypointsPermissions.COMMAND_PERMISSION)) {
            translations.COMMAND_NO_PERMISSION.send(sender)
            return true
        }
        if (args.isEmpty()) {
            WaypointsGUI(plugin, sender, sender.uniqueId)
            return true
        }
        val labelMap = Collections.singletonMap("label", label)
        when (args[0].lowercase()) {
            "help" -> {
                translations.COMMAND_HELP_HEADER.send(sender)
                translations.COMMAND_HELP_GUI.send(sender, labelMap)
                translations.COMMAND_HELP_HELP.send(sender, labelMap)

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
                if (sender.hasPermission(WaypointsPermissions.COMMAND_STATISTICS)) {
                    translations.COMMAND_HELP_STATISTICS.send(sender, labelMap)
                }
                if (sender.hasPermission(WaypointsPermissions.COMMAND_IMPORT)) {
                    translations.COMMAND_HELP_IMPORT.send(sender, labelMap)
                }
            }
            "set" -> when {
                !sender.hasPermission(WaypointsPermissions.MODIFY_PRIVATE) -> translations.COMMAND_NO_PERMISSION.send(sender)
                args.size <= 1 -> translations.COMMAND_SET_WRONG_USAGE_PRIVATE.send(sender, labelMap)
                else -> {
                    val name = args.join(1)

                    createWaypointPrivate(plugin, sender, name)
                }
            }
            "setpublic" -> when {
                !plugin.waypointsConfig.general.features.globalWaypoints -> translations.MESSAGE_FEATURE_DISABLED.send(sender)
                !sender.hasPermission(WaypointsPermissions.MODIFY_PUBLIC) -> translations.COMMAND_NO_PERMISSION.send(sender)
                args.size <= 1 -> translations.COMMAND_SET_WRONG_USAGE_PUBLIC.send(sender, labelMap)
                else -> {
                    val name = args.join(1)

                    createWaypointPublic(plugin, sender, name)
                }
            }
            "setpermission" -> when {
                !plugin.waypointsConfig.general.features.globalWaypoints -> translations.MESSAGE_FEATURE_DISABLED.send(sender)
                !sender.hasPermission(WaypointsPermissions.MODIFY_PERMISSION) -> translations.COMMAND_NO_PERMISSION.send(sender)
                args.size <= 2 -> translations.COMMAND_SET_WRONG_USAGE_PERMISSION.send(sender, labelMap)
                else -> {
                    val permission = args[1]
                    val name = args.join(2)

                    createWaypointPermission(plugin, sender, name, permission)
                }
            }
            "settemporary" -> when {
                !sender.hasPermission(WaypointsPermissions.TEMPORARY_WAYPOINT) -> translations.COMMAND_NO_PERMISSION.send(sender)
                args.size <= 3 -> translations.COMMAND_SET_WRONG_USAGE_TEMPORARY.send(sender, labelMap)
                else -> {
                    try {
                        val location = Location(sender.world, args[1].toDouble(), args[2].toDouble(), args[3].toDouble())
                        plugin.api.pointerManager.let {
                            it.enable(sender, it.temporaryWaypointTrackableOf(location))
                        }
                    } catch (_: NumberFormatException) {
                        translations.COMMAND_OTHER_NOT_A_NUMBER.send(sender)
                    }
                }
            }
            "other" -> when {
                !sender.hasPermission(WaypointsPermissions.COMMAND_OTHER) -> translations.COMMAND_NO_PERMISSION.send(sender)
                args.size <= 1 -> translations.COMMAND_OTHER_WRONG_USAGE.send(sender, labelMap)
                else -> {
                    val other = args[1]
                    val otherUUID = if (UUIDUtils.isUUID(other)) {
                        val uuid = UUID.fromString(other)
                        if (plugin.uuidUtils.getName(uuid).isEmpty) {
                            translations.COMMAND_OTHER_NOT_FOUND_UUID.send(sender, Collections.singletonMap("uuid", other))
                            return true
                        }
                        uuid
                    } else if (other.isMinecraftUsername()) {
                        val uuid = plugin.uuidUtils.getUUID(other)
                        if (uuid.isEmpty) {
                            translations.COMMAND_OTHER_NOT_FOUND_NAME.send(sender, Collections.singletonMap("name", other))
                            return true
                        }
                        uuid.get()
                    } else {
                        translations.COMMAND_OTHER_NOT_UUID_OR_NAME.send(sender)
                        return true
                    }
                    if (!plugin.api.waypointsPlayerExists(otherUUID)) {
                        translations.COMMAND_OTHER_PLAYER_NO_WAYPOINTS.send(sender)
                        return true
                    }
                    WaypointsGUI(plugin, sender, otherUUID)
                }
            }
            "statistics" -> when {
                !sender.hasPermission(WaypointsPermissions.COMMAND_STATISTICS) -> translations.COMMAND_NO_PERMISSION.send(sender)
                else -> {
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
            "import" -> when {
                !sender.hasPermission(WaypointsPermissions.COMMAND_IMPORT) -> translations.COMMAND_NO_PERMISSION.send(sender)
                args.size <= 1 || !args[1].equals("confirm", true) -> translations.COMMAND_IMPORT_MUST_CONFIRM.send(sender, labelMap)
                else -> {
                    translations.COMMAND_IMPORT_STARTED.send(sender)
                    try {
                        LegacyImporter(plugin.logger, plugin.api).performImport()
                        translations.COMMAND_IMPORT_FINISHED_SUCCESS.send(sender)
                    } catch (e: Throwable) {
                        translations.COMMAND_IMPORT_FINISHED_ERROR.send(sender)
                        plugin.logger.log(Level.SEVERE, "An error occurred while importing old waypoint data", e)
                    }
                }
            }
            else -> translations.COMMAND_NOT_FOUND.send(sender)
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        if (sender !is Player) {
            return mutableListOf()
        }

        if (args.size <= 1) {
            val options = mutableListOf<String>()

            options.add("help")

            if (sender.hasPermission(WaypointsPermissions.MODIFY_PRIVATE)) {
                options.add("set")
            }
            if (plugin.waypointsConfig.general.features.globalWaypoints) {
                if (sender.hasPermission(WaypointsPermissions.MODIFY_PUBLIC)) {
                    options.add("setPublic")
                }
                if (sender.hasPermission(WaypointsPermissions.MODIFY_PERMISSION)) {
                    options.add("setPermission")
                }
            }
            if (sender.hasPermission(WaypointsPermissions.TEMPORARY_WAYPOINT)) {
                options.add("setTemporary")
            }
            if (sender.hasPermission(WaypointsPermissions.COMMAND_OTHER)) {
                options.add("other")
            }
            if (sender.hasPermission(WaypointsPermissions.COMMAND_STATISTICS)) {
                options.add("statistics")
            }
            if (sender.hasPermission(WaypointsPermissions.COMMAND_IMPORT)) {
                options.add("import")
            }

            return if (args.isEmpty()) {
                options
            } else {
                StringUtil.copyPartialMatches(args[0], options, mutableListOf())
            }
        } else {
            return mutableListOf()
        }
    }
}