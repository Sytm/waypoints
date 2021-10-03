package de.md5lukas.waypoints.lang

import de.md5lukas.waypoints.api.OverviewSort

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

    val COMMAND_SET_WRONG_USAGE_PRIVATE = Translation(tl, "command.set.wrongUsage.private")
    val COMMAND_SET_WRONG_USAGE_PUBLIC = Translation(tl, "command.set.wrongUsage.public")
    val COMMAND_SET_WRONG_USAGE_PERMISSION = Translation(tl, "command.set.wrongUsage.permission")

    val COMMAND_OTHER_WRONG_USAGE = Translation(tl, "command.other.wrongUsage")
    val COMMAND_OTHER_NOT_FOUND_UUID = Translation(tl, "command.other.notFound.uuid")
    val COMMAND_OTHER_NOT_FOUND_NAME = Translation(tl, "command.other.notFound.name")
    val COMMAND_OTHER_NOT_UUID_OR_NAME = Translation(tl, "command.other.notUuidOrName")
    val COMMAND_OTHER_PLAYER_NO_WAYPOINTS = Translation(tl, "command.other.playerNoWaypoints")


    val POINTERS_ACTION_BAR_WRONG_WORLD = Translation(tl, "pointers.actionBar.wrongWorld")


    val TEXT_DURATION_SECOND = Translation(tl, "text.duration.second")
    val TEXT_DURATION_SECONDS = Translation(tl, "text.duration.seconds")
    val TEXT_DURATION_MINUTE = Translation(tl, "text.duration.minute")
    val TEXT_DURATION_MINUTES = Translation(tl, "text.duration.minutes")
    val TEXT_DURATION_HOUR = Translation(tl, "text.duration.hour")
    val TEXT_DURATION_HOURS = Translation(tl, "text.duration.hours")
    val TEXT_DURATION_DAY = Translation(tl, "text.duration.day")
    val TEXT_DURATION_DAYS = Translation(tl, "text.duration.days")


    val WAYPOINT_LIMIT_REACHED_PRIVATE = Translation(tl, "message.waypoint.limitReached.private")
    val WAYPOINT_NAME_DUPLICATE_PRIVATE = Translation(tl, "message.waypoint.nameDuplicate.private")
    val WAYPOINT_SET_SUCCESS_PRIVATE = Translation(tl, "message.waypoint.setSuccess.private")

    val WAYPOINT_NAME_DUPLICATE_PUBLIC = Translation(tl, "message.waypoint.nameDuplicate.public")
    val WAYPOINT_SET_SUCCESS_PUBLIC = Translation(tl, "message.waypoint.setSuccess.public")

    val WAYPOINT_NAME_DUPLICATE_PERMISSION = Translation(tl, "message.waypoint.nameDuplicate.permission")
    val WAYPOINT_SET_SUCCESS_PERMISSION = Translation(tl, "message.waypoint.setSuccess.permission")

    val WAYPOINT_MESSAGE_GET_UUID = Translation(tl, "message.waypoint.getUuid")


    val FOLDER_LIMIT_REACHED_PRIVATE = Translation(tl, "message.folder.limitReached.private")
    val FOLDER_NAME_DUPLICATE_PRIVATE = Translation(tl, "message.folder.nameDuplicate.private")
    val FOLDER_CREATE_SUCCESS_PRIVATE = Translation(tl, "message.folder.createSuccess.private")

    val FOLDER_NAME_DUPLICATE_PUBLIC = Translation(tl, "message.folder.nameDuplicate.public")
    val FOLDER_CREATE_SUCCESS_PUBLIC = Translation(tl, "message.folder.createSuccess.public")

    val FOLDER_NAME_DUPLICATE_PERMISSION = Translation(tl, "message.folder.nameDuplicate.permission")
    val FOLDER_CREATE_SUCCESS_PERMISSION = Translation(tl, "message.folder.createSuccess.permission")

    val MESSAGE_TELEPORT_ON_COOLDOWN = Translation(tl, "message.teleport.onCooldown")
    val MESSAGE_TELEPORT_NOT_ENOUGH_XP = Translation(tl, "message.teleport.notEnoughXp")
    val MESSAGE_TELEPORT_NOT_ENOUGH_BALANCE = Translation(tl, "message.teleport.notEnoughBalance")
    val MESSAGE_TELEPORT_STAND_STILL_NOTICE = Translation(tl, "message.teleport.standStill.notice")
    val MESSAGE_TELEPORT_STAND_STILL_MOVED = Translation(tl, "message.teleport.standStill.moved")


    val INVENTORY_TITLE_SELF = Translation(tl, "inventory.title.self")
    val INVENTORY_TITLE_OTHER = Translation(tl, "inventory.title.other")

    val INVENTORY_LISTING_WAYPOINT_DISTANCE_OTHER_WORLD = Translation(tl, "inventory.listing.waypoint.distanceOtherWorld")

    val GENERAL_PREVIOUS = ItemTranslation(tl, "inventory.general.previous")
    val GENERAL_NEXT = ItemTranslation(tl, "inventory.general.next")
    val GENERAL_BACK = ItemTranslation(tl, "inventory.general.back")

    val BACKGROUND_PRIVATE = ItemTranslation(tl, "inventory.background.private")
    val BACKGROUND_PUBLIC = ItemTranslation(tl, "inventory.background.public")
    val BACKGROUND_PERMISSION = ItemTranslation(tl, "inventory.background.permission")

    val OVERVIEW_CYCLE_SORT = ItemTranslation(tl, "inventory.overview.cycleSort")
    val OVERVIEW_CYCLE_SORT_ACTIVE_COLOR = Translation(tl, "inventory.overview.cycleSort.activeColor")
    val OVERVIEW_CYCLE_SORT_INACTIVE_COLOR = Translation(tl, "inventory.overview.cycleSort.inactiveColor")
    val OVERVIEW_CYCLE_SORT_OPTIONS = OverviewSort.values().map {
        it to Translation(tl, "inventory.overview.cycleSort.options.${it.name.lowercase()}")
    }
    val OVERVIEW_TOGGLE_GLOBALS_VISIBLE = ItemTranslation(tl, "inventory.overview.toggleGlobals.visible")
    val OVERVIEW_TOGGLE_GLOBALS_HIDDEN = ItemTranslation(tl, "inventory.overview.toggleGlobals.hidden")
    val OVERVIEW_DESELECT = ItemTranslation(tl, "inventory.overview.cycleSort")
    val OVERVIEW_SET_WAYPOINT = ItemTranslation(tl, "inventory.overview.setWaypoint")
    val OVERVIEW_CREATE_FOLDER = ItemTranslation(tl, "inventory.overview.createFolder")

    val FOLDER_CONFIRM_DELETE_QUESTION = Translation(tl, "inventory.deleteFolder.question")
    val FOLDER_CONFIRM_DELETE_TRUE = Translation(tl, "inventory.deleteFolder.confirm")
    val FOLDER_CONFIRM_DELETE_FALSE = Translation(tl, "inventory.deleteFolder.cancel")

    val WAYPOINT_CONFIRM_DELETE_QUESTION = Translation(tl, "inventory.deleteWaypoint.question")
    val WAYPOINT_CONFIRM_DELETE_TRUE = Translation(tl, "inventory.deleteWaypoint.confirm")
    val WAYPOINT_CONFIRM_DELETE_FALSE = Translation(tl, "inventory.deleteWaypoint.cancel")

    val ICON_PUBLIC = ItemTranslation(tl, "inventory.listing.private")
    val ICON_PERMISSION = ItemTranslation(tl, "inventory.listing.permission")

    val WAYPOINT_ICON_DEATH = ItemTranslation(tl, "inventory.waypoint.icon.death")
    val WAYPOINT_ICON_PRIVATE = ItemTranslation(tl, "inventory.waypoint.icon.private")
    val WAYPOINT_ICON_PUBLIC = ItemTranslation(tl, "inventory.waypoint.icon.public")
    val WAYPOINT_ICON_PERMISSION = ItemTranslation(tl, "inventory.waypoint.icon.permission")

    val WAYPOINT_SELECT = ItemTranslation(tl, "inventory.waypoint.select")
    val WAYPOINT_DELETE = ItemTranslation(tl, "inventory.waypoint.delete")
    val WAYPOINT_RENAME = ItemTranslation(tl, "inventory.waypoint.rename")
    val WAYPOINT_MOVE_TO_FOLDER = ItemTranslation(tl, "inventory.waypoint.moveToFolder")
    val WAYPOINT_TELEPORT = ItemTranslation(tl, "inventory.waypoint.teleport")
    val WAYPOINT_SELECT_BEACON_COLOR = ItemTranslation(tl, "inventory.waypoint.selectBeaconColor")
    val WAYPOINT_GET_UUID = ItemTranslation(tl, "inventory.waypoint.getUuid")
    val WAYPOINT_NEW_ICON_INVALID = Translation(tl, "inventory.waypoint.newIconInvalid")

    val FOLDER_ICON_PRIVATE = ItemTranslation(tl, "inventory.folder.icon.private")
    val FOLDER_ICON_PUBLIC = ItemTranslation(tl, "inventory.folder.icon.public")
    val FOLDER_ICON_PERMISSION = ItemTranslation(tl, "inventory.folder.icon.permission")

    val FOLDER_DELETE = ItemTranslation(tl, "inventory.folder.delete")
    val FOLDER_RENAME = ItemTranslation(tl, "inventory.folder.rename")
    val FOLDER_NEW_ICON_INVALID = Translation(tl, "inventory.folder.newIconInvalid")

    val SELECT_FOLDER_NO_FOLDER = ItemTranslation(tl, "inventory.selectFolder.noFolder")

    val CONFIRM_BACKGROUND = ItemTranslation(tl, "inventory.confirm.background")
    val CONFIRM_QUESTION = ItemTranslation(tl, "inventory.confirm.question")
    val CONFIRM_TRUE = ItemTranslation(tl, "inventory.confirm.yes")
    val CONFIRM_FALSE = ItemTranslation(tl, "inventory.confirm.no")
}