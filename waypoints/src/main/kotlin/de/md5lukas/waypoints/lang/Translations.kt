package de.md5lukas.waypoints.lang

import de.md5lukas.waypoints.api.BeaconColor
import de.md5lukas.waypoints.api.OverviewSort

@Suppress("PropertyName")
class Translations(
    tl: TranslationLoader
) {
    val COMMAND_NOT_A_PLAYER = Translation(tl, "command.notAPlayer")
    val COMMAND_NO_PERMISSION = Translation(tl, "command.noPermission")
    val COMMAND_NOT_FOUND = Translation(tl, "command.notFound")

    val COMMAND_SCRIPT_HELP_HEADER = Translation(tl, "command.script.help.header")
    val COMMAND_SCRIPT_HELP_DESELECT_WAYPOINT = Translation(tl, "command.script.help.deselectWaypoint")
    val COMMAND_SCRIPT_HELP_SELECT_WAYPOINT = Translation(tl, "command.script.help.selectWaypoint")

    val COMMAND_SCRIPT_DESELECT_WAYPOINT_WRONG_USAGE = Translation(tl, "command.script.deselectWaypoint.wrongUsage")

    val COMMAND_SCRIPT_SELECT_WAYPOINT_WRONG_USAGE = Translation(tl, "command.script.selectWaypoint.wrongUsage")
    val COMMAND_SCRIPT_SELECT_WAYPOINT_WAYPOINT_NOT_FOUND = Translation(tl, "command.script.selectWaypoint.waypointNotFound")

    val COMMAND_SCRIPT_PLAYER_NOT_FOUND = Translation(tl, "commands.script.playerNotFound")
    val COMMAND_SCRIPT_INVALID_UUID = Translation(tl, "commands.script.invalidUuid")

    val COMMAND_HELP_HEADER = Translation(tl, "command.help.header")
    val COMMAND_HELP_GUI = Translation(tl, "command.help.gui")
    val COMMAND_HELP_HELP = Translation(tl, "command.help.help")
    val COMMAND_HELP_SET_PRIVATE = Translation(tl, "command.help.set.private")
    val COMMAND_HELP_SET_PUBLIC = Translation(tl, "command.help.set.public")
    val COMMAND_HELP_SET_PERMISSION = Translation(tl, "command.help.set.permission")
    val COMMAND_HELP_OTHER = Translation(tl, "command.help.other")
    val COMMAND_HELP_STATISTICS = Translation(tl, "command.help.statistics")
    val COMMAND_HELP_IMPORT = Translation(tl, "command.help.import")

    val COMMAND_SET_WRONG_USAGE_PRIVATE = Translation(tl, "command.set.wrongUsage.private")
    val COMMAND_SET_WRONG_USAGE_PUBLIC = Translation(tl, "command.set.wrongUsage.public")
    val COMMAND_SET_WRONG_USAGE_PERMISSION = Translation(tl, "command.set.wrongUsage.permission")

    val COMMAND_OTHER_WRONG_USAGE = Translation(tl, "command.other.wrongUsage")
    val COMMAND_OTHER_NOT_FOUND_UUID = Translation(tl, "command.other.notFound.uuid")
    val COMMAND_OTHER_NOT_FOUND_NAME = Translation(tl, "command.other.notFound.name")
    val COMMAND_OTHER_NOT_UUID_OR_NAME = Translation(tl, "command.other.notUuidOrName")
    val COMMAND_OTHER_PLAYER_NO_WAYPOINTS = Translation(tl, "command.other.playerNoWaypoints")

    val COMMAND_STATISTICS_MESSAGE = Translation(tl, "command.statistics.message")

    val COMMAND_IMPORT_MUST_CONFIRM = Translation(tl, "command.import.mustConfirm")
    val COMMAND_IMPORT_STARTED = Translation(tl, "command.import.started")
    val COMMAND_IMPORT_FINISHED_SUCCESS = Translation(tl, "command.import.finished.success")
    val COMMAND_IMPORT_FINISHED_ERROR = Translation(tl, "command.import.finished.error")


    val POINTERS_ACTION_BAR_WRONG_WORLD = Translation(tl, "pointers.actionBar.wrongWorld")

    val POINTERS_ACTION_BAR_DISTANCE = Translation(tl, "pointers.actionBar.distance")


    val TEXT_DURATION_SECOND = Translation(tl, "text.duration.second")
    val TEXT_DURATION_SECONDS = Translation(tl, "text.duration.seconds")
    val TEXT_DURATION_MINUTE = Translation(tl, "text.duration.minute")
    val TEXT_DURATION_MINUTES = Translation(tl, "text.duration.minutes")
    val TEXT_DURATION_HOUR = Translation(tl, "text.duration.hour")
    val TEXT_DURATION_HOURS = Translation(tl, "text.duration.hours")
    val TEXT_DURATION_DAY = Translation(tl, "text.duration.day")
    val TEXT_DURATION_DAYS = Translation(tl, "text.duration.days")

    val TEXT_DISTANCE_OTHER_WORLD = Translation(tl, "text.distance.otherWorld")

    val TEXT_BEACON_COLORS = BeaconColor.values().map {
        it to Translation(tl, "text.beaconColors.${it.name.lowercase()}")
    }

    val MESSAGE_FEATURE_DISABLED = Translation(tl, "message.featureDisabled")

    val WAYPOINT_CREATE_COORDINATES_INVALID_FORMAT = Translation(tl, "message.waypoint.create.coordinates.invalidFormat")

    val WAYPOINT_LIMIT_REACHED_PRIVATE = Translation(tl, "message.waypoint.limitReached.private")
    val WAYPOINT_NAME_DUPLICATE_PRIVATE = Translation(tl, "message.waypoint.nameDuplicate.private")
    val WAYPOINT_SET_SUCCESS_PRIVATE = Translation(tl, "message.waypoint.setSuccess.private")

    val WAYPOINT_NAME_DUPLICATE_PUBLIC = Translation(tl, "message.waypoint.nameDuplicate.public")
    val WAYPOINT_SET_SUCCESS_PUBLIC = Translation(tl, "message.waypoint.setSuccess.public")

    val WAYPOINT_NAME_DUPLICATE_PERMISSION = Translation(tl, "message.waypoint.nameDuplicate.permission")
    val WAYPOINT_SET_SUCCESS_PERMISSION = Translation(tl, "message.waypoint.setSuccess.permission")

    val MESSAGE_WAYPOINT_GET_UUID = Translation(tl, "message.waypoint.getUuid")
    val MESSAGE_WAYPOINT_NEW_ICON_INVALID = Translation(tl, "message.waypoint.newIconInvalid")


    val FOLDER_LIMIT_REACHED_PRIVATE = Translation(tl, "message.folder.limitReached.private")
    val FOLDER_NAME_DUPLICATE_PRIVATE = Translation(tl, "message.folder.nameDuplicate.private")
    val FOLDER_CREATE_SUCCESS_PRIVATE = Translation(tl, "message.folder.createSuccess.private")
    val FOLDER_NEW_ICON_INVALID = Translation(tl, "message.folder.newIconInvalid")

    val FOLDER_NAME_DUPLICATE_PUBLIC = Translation(tl, "message.folder.nameDuplicate.public")
    val FOLDER_CREATE_SUCCESS_PUBLIC = Translation(tl, "message.folder.createSuccess.public")

    val FOLDER_NAME_DUPLICATE_PERMISSION = Translation(tl, "message.folder.nameDuplicate.permission")
    val FOLDER_CREATE_SUCCESS_PERMISSION = Translation(tl, "message.folder.createSuccess.permission")

    val MESSAGE_TELEPORT_ON_COOLDOWN = Translation(tl, "message.teleport.onCooldown")
    val MESSAGE_TELEPORT_NOT_ENOUGH_XP = Translation(tl, "message.teleport.notEnough.xp")
    val MESSAGE_TELEPORT_NOT_ENOUGH_BALANCE = Translation(tl, "message.teleport.notEnough.balance")
    val MESSAGE_TELEPORT_STAND_STILL_NOTICE = Translation(tl, "message.teleport.standStill.notice")
    val MESSAGE_TELEPORT_STAND_STILL_MOVED = Translation(tl, "message.teleport.standStill.moved")


    val INVENTORY_TITLE_SELF = Translation(tl, "inventory.title.self")
    val INVENTORY_TITLE_OTHER = Translation(tl, "inventory.title.other")


    val GENERAL_PREVIOUS = ItemTranslation(tl, "inventory.general.previous")
    val GENERAL_NEXT = ItemTranslation(tl, "inventory.general.next")
    val GENERAL_BACK = ItemTranslation(tl, "inventory.general.back")

    val BACKGROUND_PRIVATE = ItemTranslation(tl, "inventory.background.private")
    val BACKGROUND_DEATH = ItemTranslation(tl, "inventory.background.death")
    val BACKGROUND_PUBLIC = ItemTranslation(tl, "inventory.background.public")
    val BACKGROUND_PERMISSION = ItemTranslation(tl, "inventory.background.permission")

    val OVERVIEW_CYCLE_SORT = ItemTranslation(tl, "inventory.overview.cycleSort")
    val OVERVIEW_CYCLE_SORT_ACTIVE_COLOR = Translation(tl, "inventory.overview.cycleSort.activeColor")
    val OVERVIEW_CYCLE_SORT_INACTIVE_COLOR = Translation(tl, "inventory.overview.cycleSort.inactiveColor")
    val OVERVIEW_CYCLE_SORT_OPTIONS = OverviewSort.values().map {
        it to Translation(tl, "text.sortOptions.${it.name.lowercase()}")
    }
    val OVERVIEW_TOGGLE_GLOBALS_VISIBLE = ItemTranslation(tl, "inventory.overview.toggleGlobals.visible")
    val OVERVIEW_TOGGLE_GLOBALS_HIDDEN = ItemTranslation(tl, "inventory.overview.toggleGlobals.hidden")
    val OVERVIEW_DESELECT = ItemTranslation(tl, "inventory.overview.deselect")
    val OVERVIEW_SET_WAYPOINT = ItemTranslation(tl, "inventory.overview.setWaypoint")
    val OVERVIEW_CREATE_FOLDER = ItemTranslation(tl, "inventory.overview.createFolder")


    val ICON_PUBLIC = ItemTranslation(tl, "inventory.listing.public")
    val ICON_PERMISSION = ItemTranslation(tl, "inventory.listing.permission")

    val WAYPOINT_ICON_PRIVATE = ItemTranslation(tl, "inventory.waypoint.icon.private")
    val WAYPOINT_ICON_DEATH = ItemTranslation(tl, "inventory.waypoint.icon.death")
    val WAYPOINT_ICON_PUBLIC = ItemTranslation(tl, "inventory.waypoint.icon.public")
    val WAYPOINT_ICON_PERMISSION = ItemTranslation(tl, "inventory.waypoint.icon.permission")

    val WAYPOINT_SELECT = ItemTranslation(tl, "inventory.waypoint.select")
    val WAYPOINT_DELETE = ItemTranslation(tl, "inventory.waypoint.delete", true)
    val WAYPOINT_DELETE_CONFIRM_QUESTION = ItemTranslation(tl, "inventory.waypoint.delete.question")
    val WAYPOINT_DELETE_CONFIRM_TRUE = ItemTranslation(tl, "inventory.waypoint.delete.confirm")
    val WAYPOINT_DELETE_CONFIRM_FALSE = ItemTranslation(tl, "inventory.waypoint.delete.cancel")
    val WAYPOINT_RENAME = ItemTranslation(tl, "inventory.waypoint.rename")
    val WAYPOINT_MOVE_TO_FOLDER = ItemTranslation(tl, "inventory.waypoint.moveToFolder")
    val WAYPOINT_TELEPORT = ItemTranslation(tl, "inventory.waypoint.teleport")
    val WAYPOINT_TELEPORT_XP_LEVEL = Translation(tl, "inventory.waypoint.teleport.xpLevel")
    val WAYPOINT_TELEPORT_BALANCE = Translation(tl, "inventory.waypoint.teleport.balance")
    val WAYPOINT_SELECT_BEACON_COLOR = ItemTranslation(tl, "inventory.waypoint.selectBeaconColor")
    val WAYPOINT_GET_UUID = ItemTranslation(tl, "inventory.waypoint.getUuid")
    val WAYPOINT_EDIT_PERMISSION = ItemTranslation(tl, "inventory.waypoint.editPermission")
    val WAYPOINT_MAKE_PUBLIC = ItemTranslation(tl, "inventory.waypoint.make.public", true)
    val WAYPOINT_MAKE_PUBLIC_CONFIRM_QUESTION = ItemTranslation(tl, "inventory.waypoint.make.public.question")
    val WAYPOINT_MAKE_PUBLIC_CONFIRM_TRUE = ItemTranslation(tl, "inventory.waypoint.make.public.confirm")
    val WAYPOINT_MAKE_PUBLIC_CONFIRM_FALSE = ItemTranslation(tl, "inventory.waypoint.make.public.cancel")
    val WAYPOINT_MAKE_PERMISSION = ItemTranslation(tl, "inventory.waypoint.make.permission", true)
    val WAYPOINT_MAKE_PERMISSION_CONFIRM_QUESTION = ItemTranslation(tl, "inventory.waypoint.make.permission.question")
    val WAYPOINT_MAKE_PERMISSION_CONFIRM_TRUE = ItemTranslation(tl, "inventory.waypoint.make.permission.confirm")
    val WAYPOINT_MAKE_PERMISSION_CONFIRM_FALSE = ItemTranslation(tl, "inventory.waypoint.make.permission.cancel")

    val FOLDER_ICON_PRIVATE = ItemTranslation(tl, "inventory.folder.icon.private")
    val FOLDER_ICON_DEATH = ItemTranslation(tl, "inventory.folder.icon.death")
    val FOLDER_ICON_PUBLIC = ItemTranslation(tl, "inventory.folder.icon.public")
    val FOLDER_ICON_PERMISSION = ItemTranslation(tl, "inventory.folder.icon.permission")

    val FOLDER_DELETE = ItemTranslation(tl, "inventory.folder.delete", true)
    val FOLDER_DELETE_CONFIRM_QUESTION = ItemTranslation(tl, "inventory.folder.delete.question")
    val FOLDER_DELETE_CONFIRM_TRUE = ItemTranslation(tl, "inventory.folder.delete.confirm")
    val FOLDER_DELETE_CONFIRM_FALSE = ItemTranslation(tl, "inventory.folder.delete.cancel")
    val FOLDER_RENAME = ItemTranslation(tl, "inventory.folder.rename")

    val SELECT_FOLDER_NO_FOLDER = ItemTranslation(tl, "inventory.selectFolder.noFolder")

    val SELECT_BEACON_COLOR_MOVE_LEFT = ItemTranslation(tl, "inventory.selectBeaconColor.moveLeft")
    val SELECT_BEACON_COLOR_MOVE_RIGHT = ItemTranslation(tl, "inventory.selectBeaconColor.moveRight")

    val WAYPOINT_CREATE_ENTER_NAME = Translation(tl, "inventory.waypoint.create.enterName")
    val WAYPOINT_CREATE_ENTER_COORDINATES = Translation(tl, "inventory.waypoint.create.enterCoordinates")
    val WAYPOINT_CREATE_ENTER_PERMISSION = Translation(tl, "inventory.waypoint.create.enterPermission")

    val FOLDER_CREATE_ENTER_NAME = Translation(tl, "inventory.folder.create.enterName")

    val CONFIRM_BACKGROUND = ItemTranslation(tl, "inventory.confirm.background")

    val INTEGRATIONS_DYNMAP_MARKER_SET_LABEL = Translation(tl, "integrations.dynmap.markerSet.label")
}