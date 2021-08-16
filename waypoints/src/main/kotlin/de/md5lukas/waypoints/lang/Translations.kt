package de.md5lukas.waypoints.lang

@Suppress("PropertyName")
class Translations(
    tl: TranslationLoader
) {
    val COMMAND_NOT_A_PLAYER = Translation(tl, "command.notAPlayer")
    val COMMAND_NO_PERMISSION = Translation(tl, "command.noPermission")
    val COMMAND_NOT_FOUND = Translation(tl, "command.notFound")

    val COMMAND_HELP_HEADER = Translation(tl, "command.help.header")
    val COMMAND_HELP_GUI = Translation(tl, "command.help.gui")
    val COMMAND_HELP_HELP = Translation(tl, "command.help.help")
    val COMMAND_HELP_SET_PRIVATE = Translation(tl, "command.help.set.private")
    val COMMAND_HELP_SET_PUBLIC = Translation(tl, "command.help.set.public")
    val COMMAND_HELP_SET_PERMISSION = Translation(tl, "command.help.set.permission")
    val COMMAND_HELP_OTHER = Translation(tl, "command.help.other")

    val COMMAND_SET_PRIVATE_WRONG_USAGE = Translation(tl, "command.set.private.wrongUsage")
    val COMMAND_SET_PRIVATE_LIMIT_REACHED = Translation(tl, "command.set.private.limitReached")
    val COMMAND_SET_PRIVATE_NAME_DUPLICATE = Translation(tl, "command.set.private.nameDuplicate")
    val COMMAND_SET_PRIVATE_SUCCESS = Translation(tl, "command.set.private.success")

    val COMMAND_SET_PUBLIC_WRONG_USAGE = Translation(tl, "command.set.public.wrongUsage")
    val COMMAND_SET_PUBLIC_NAME_DUPLICATE = Translation(tl, "command.set.public.nameDuplicate")
    val COMMAND_SET_PUBLIC_SUCCESS = Translation(tl, "command.set.public.success")

    val COMMAND_SET_PERMISSION_WRONG_USAGE = Translation(tl, "command.set.permission.wrongUsage")
    val COMMAND_SET_PERMISSION_NAME_DUPLICATE = Translation(tl, "command.set.permission.nameDuplicate")
    val COMMAND_SET_PERMISSION_SUCCESS = Translation(tl, "command.set.permission.success")

    val COMMAND_OTHER_WRONG_USAGE = Translation(tl, "command.other.wrongUsage")
    val COMMAND_OTHER_NOT_FOUND_UUID = Translation(tl, "command.other.notFound.uuid")
    val COMMAND_OTHER_NOT_FOUND_NAME = Translation(tl, "command.other.notFound.name")
    val COMMAND_OTHER_NOT_UUID_OR_NAME = Translation(tl, "command.other.notUuidOrName")
    val COMMAND_OTHER_PLAYER_NO_WAYPOINTS = Translation(tl, "command.other.playerNoWaypoints")

    val POINTERS_ACTION_BAR_WRONG_WORLD = Translation(tl, "pointers.actionBar.wrongWorld")
}