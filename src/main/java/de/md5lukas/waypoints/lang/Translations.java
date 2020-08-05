/*
 *     Waypoints2, A plugin for spigot to add waypoints functionality
 *     Copyright (C) 2019-2020 Lukas Planz
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.md5lukas.waypoints.lang;

import de.md5lukas.i18n.language.Language;
import de.md5lukas.i18n.language.LanguageStorage;
import de.md5lukas.i18n.translations.Translation;
import de.md5lukas.waypoints.Waypoints;
import de.md5lukas.waypoints.config.WPConfig;
import de.md5lukas.waypoints.util.ConfigHelper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Translations {

    private final LanguageStorage ls;
    private final InventoryTranslations inventoryTranslations;

    public Translations() {
        ls = new LanguageStorage(WPConfig.getGeneralConfig().getDefaultLanguage());
        inventoryTranslations = new InventoryTranslations(ls);

        // ---------------------- General ----------------------
        GENERAL_NOT_A_PLAYER = new Translation(ls, "general.notAPlayer");
        GENERAL_NO_PERMISSION = new Translation(ls, "general.noPermission");
        GENERAL_WAYPOINT_NOT_FOUND = new Translation(ls, "general.waypointNotFound");
        GENERAL_FOLDER_NOT_FOUND = new Translation(ls, "general.folderNotFound");
        GENERAL_NOT_A_VALID_UUID = new Translation(ls, "general.notAValidUuid");

        //<editor-fold defaultstate="collapsed" desc="Miscellaneous">

        DISPLAY_WRONG_WORLD = new Translation(ls, "display.wrongWorld"); // %currentworld%, %correctworld%
        CHAT_DURATION_SECOND = new Translation(ls, "chat.duration.second");
        CHAT_DURATION_SECONDS = new Translation(ls, "chat.duration.seconds");
        CHAT_DURATION_MINUTE = new Translation(ls, "chat.duration.minute");
        CHAT_DURATION_MINUTES = new Translation(ls, "chat.duration.minutes");
        CHAT_DURATION_HOUR = new Translation(ls, "chat.duration.hour");
        CHAT_DURATION_HOURS = new Translation(ls, "chat.duration.hours");
        CHAT_DURATION_DAY = new Translation(ls, "chat.duration.day");
        CHAT_DURATION_DAYS = new Translation(ls, "chat.duration.days");
        CHAT_ACTION_UPDATE_ITEM_WAYPOINT_PRIVATE = new Translation(ls, "chatAction.updateItem.waypoint.private");
        CHAT_ACTION_UPDATE_ITEM_WAYPOINT_PUBLIC = new Translation(ls, "chatAction.updateItem.waypoint.public");
        CHAT_ACTION_UPDATE_ITEM_WAYPOINT_PERMISSION = new Translation(ls, "chatAction.updateItem.waypoint.permission");
        CHAT_ACTION_UPDATE_ITEM_FOLDER_PRIVATE = new Translation(ls, "chatAction.updateItem.folder.private");
        CHAT_WARNING_WORLD_TRANSLATIONS_MISSING = new Translation(ls, "chatWarning.worldTranslationsMissing");

        CHAT_TELEPORT_ON_COOLDOWN = new Translation(ls, "chat.teleport.onCooldown"); // %remainingTime%
        CHAT_TELEPORT_NOT_ENOUGH_XP_POINTS = new Translation(ls, "chat.teleport.notEnough.xpPoints");
        CHAT_TELEPORT_NOT_ENOUGH_XP_LEVELS = new Translation(ls, "chat.teleport.notEnough.xpLevels");
        CHAT_TELEPORT_NOT_ENOUGH_BALANCE = new Translation(ls, "chat.teleport.notEnough.balance");
        CHAT_TELEPORT_STAND_STILL_NOTICE = new Translation(ls, "chat.teleport.standStillNotice"); // %timeRequired%
        CHAT_TELEPORT_CANCELLED_MOVE = new Translation(ls, "chat.teleport.cancelledMove");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Commands">
        COMMAND_HELP_TITLE = new Translation(ls, "command.help.title");
        COMMAND_HELP_HELP = new Translation(ls, "command.help.help");
        COMMAND_HELP_SET_PRIVATE = new Translation(ls, "command.help.set.private");
        COMMAND_HELP_SET_PUBLIC = new Translation(ls, "command.help.set.public");
        COMMAND_HELP_SET_PERMISSION = new Translation(ls, "command.help.set.permission");
        COMMAND_HELP_COMPASS = new Translation(ls, "command.help.compass");
        COMMAND_HELP_OTHER = new Translation(ls, "command.help.other");
        COMMAND_HELP_UPDATE_ITEM = new Translation(ls, "command.help.updateItem");
        COMMAND_COMPASS_DISABLED = new Translation(ls, "command.compass.disabled");
        COMMAND_COMPASS_LOCKED = new Translation(ls, "command.compass.locked");
        COMMAND_COMPASS_SET_SUCCESS = new Translation(ls, "command.compass.setSuccess");
        COMMAND_SET_PRIVATE_WRONG_USAGE = new Translation(ls, "command.set.private.wrongUsage");
        COMMAND_SET_PRIVATE_NAME_DUPLICATE = new Translation(ls, "command.set.private.nameDuplicate");
        COMMAND_SET_PRIVATE_LIMIT_REACHED = new Translation(ls, "command.set.private.limitReached");
        COMMAND_SET_PRIVATE_SUCCESS = new Translation(ls, "command.set.private.success");
        COMMAND_SET_PUBLIC_WRONG_USAGE = new Translation(ls, "command.set.public.wrongUsage");
        COMMAND_SET_PUBLIC_NAME_DUPLICATE = new Translation(ls, "command.set.public.nameDuplicate");
        COMMAND_SET_PUBLIC_SUCCESS = new Translation(ls, "command.set.public.success");
        COMMAND_SET_PERMISSION_WRONG_USAGE = new Translation(ls, "command.set.permission.wrongUsage");
        COMMAND_SET_PERMISSION_NAME_DUPLICATE = new Translation(ls, "command.set.permission.nameDuplicate");
        COMMAND_SET_PERMISSION_SUCCESS = new Translation(ls, "command.set.permission.success");
        COMMAND_CREATE_FOLDER_WRONG_USAGE = new Translation(ls, "command.createFolder.wrongUsage");
        COMMAND_CREATE_FOLDER_NAME_DUPLICATE = new Translation(ls, "command.createFolder.nameDuplicate");
        COMMAND_CREATE_FOLDER_LIMIT_REACHED = new Translation(ls, "command.createFolder.limitReached");
        COMMAND_CREATE_FOLDER_SUCCESS = new Translation(ls, "command.createFolder.success");
        COMMAND_OTHER_WRONG_USAGE = new Translation(ls, "command.other.wrongUsage");
        COMMAND_OTHER_NOT_A_VALID_UUID_OR_PLAYER = new Translation(ls, "command.other.notAValidUuidOrPlayerName");
        COMMAND_OTHER_UUID_NOT_FOUND = new Translation(ls, "command.other.uuidNotFound");
        COMMAND_OTHER_PLAYER_NAME_NOT_FOUND = new Translation(ls, "command.other.playerNameNotFound");
        COMMAND_UPDATE_ITEM_DISABLED = new Translation(ls, "command.updateItem.disabled");
        COMMAND_UPDATE_ITEM_NOT_A_VALID_ITEM = new Translation(ls, "command.updateItem.notAValidItem");
        COMMAND_UPDATE_ITEM_WRONG_USAGE = new Translation(ls, "command.updateItem.wrongUsage");
        COMMAND_UPDATE_ITEM_WAYPOINT_SUCCESS = new Translation(ls, "command.updateItem.waypoint.success");
        COMMAND_UPDATE_ITEM_FOLDER_SUCCESS = new Translation(ls, "command.updateItem.folder.success");
        COMMAND_RENAME_DISABLED = new Translation(ls, "command.rename.disabled");
        COMMAND_RENAME_WAYPOINT_PRIVATE_DISABLED = new Translation(ls, "command.rename.waypoint.private.disabled");
        COMMAND_RENAME_WAYPOINT_PUBLIC_DISABLED = new Translation(ls, "command.rename.waypoint.public.disabled");
        COMMAND_RENAME_WAYPOINT_PERMISSION_DISABLED = new Translation(ls, "command.rename.waypoint.permission.disabled");
        COMMAND_RENAME_FOLDER_DISABLED = new Translation(ls, "command.rename.folder.disabled");
        COMMAND_RENAME_WRONG_USAGE = new Translation(ls, "command.rename.wrongUsage");
        COMMAND_RENAME_WAYPOINT_PRIVATE_NAME_DUPLICATE = new Translation(ls, "command.rename.waypoint.private.nameDuplicate");
        COMMAND_RENAME_WAYPOINT_PUBLIC_NAME_DUPLICATE = new Translation(ls, "command.rename.waypoint.public.nameDuplicate");
        COMMAND_RENAME_WAYPOINT_PERMISSION_NAME_DUPLICATE = new Translation(ls, "command.rename.waypoint.permission.nameDuplicate");
        COMMAND_RENAME_WAYPOINT_SUCCESS = new Translation(ls, "command.rename.waypoint.success");
        COMMAND_RENAME_FOLDER_NAME_DUPLICATE = new Translation(ls, "command.rename.folder.nameDuplicate");
        COMMAND_RENAME_FOLDER_SUCCESS = new Translation(ls, "command.rename.folder.success");
        COMMAND_NOT_FOUND = new Translation(ls, "command.notFound");
        //</editor-fold>
    }

    public InventoryTranslations getInventoryTranslations() {
        return inventoryTranslations;
    }

    public void updateLanguageStore() {
        String fileExtension = ".yml";

        File[] files = Waypoints.getFileManager().getTranslationFolder().listFiles();


        if (files != null) {
            List<Language> languages = new ArrayList<>();

            for (File langFile : files) {
                String name = langFile.getName();

                if (!name.toLowerCase().endsWith(fileExtension)) {
                    continue;
                }

                // TODO(md5-commons:2.0) replace with StringHelper.removeSuffix
                name = name.substring(0, name.length() - fileExtension.length());

                FileConfiguration cfg = YamlConfiguration.loadConfiguration(langFile);

                Map<String, String> translations = ConfigHelper.fileConfigurationToMap(cfg);

                Language lang = new Language(name, translations);
            }

            ls.setLanguages(languages);
            ls.setDefaultLanguage(WPConfig.getGeneralConfig().getDefaultLanguage());
        }
    }

    public final Translation
            // ---------------------- General ----------------------
            GENERAL_NOT_A_PLAYER,
            GENERAL_NO_PERMISSION,
            GENERAL_WAYPOINT_NOT_FOUND,
            GENERAL_FOLDER_NOT_FOUND,
            GENERAL_NOT_A_VALID_UUID,

    //<editor-fold defaultstate="collapsed" desc="Miscellaneous">
    DISPLAY_WRONG_WORLD,
            CHAT_DURATION_SECOND,
            CHAT_DURATION_SECONDS,
            CHAT_DURATION_MINUTE,
            CHAT_DURATION_MINUTES,
            CHAT_DURATION_HOUR,
            CHAT_DURATION_HOURS,
            CHAT_DURATION_DAY,
            CHAT_DURATION_DAYS,
            CHAT_ACTION_UPDATE_ITEM_WAYPOINT_PRIVATE,
            CHAT_ACTION_UPDATE_ITEM_WAYPOINT_PUBLIC,
            CHAT_ACTION_UPDATE_ITEM_WAYPOINT_PERMISSION,
            CHAT_ACTION_UPDATE_ITEM_FOLDER_PRIVATE,
            CHAT_WARNING_WORLD_TRANSLATIONS_MISSING,
            CHAT_TELEPORT_ON_COOLDOWN,
            CHAT_TELEPORT_NOT_ENOUGH_XP_POINTS,
            CHAT_TELEPORT_NOT_ENOUGH_XP_LEVELS,
            CHAT_TELEPORT_NOT_ENOUGH_BALANCE,
            CHAT_TELEPORT_STAND_STILL_NOTICE,
            CHAT_TELEPORT_CANCELLED_MOVE,
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Commands">
    COMMAND_HELP_TITLE,
            COMMAND_HELP_HELP,
            COMMAND_HELP_SET_PRIVATE,
            COMMAND_HELP_SET_PUBLIC,
            COMMAND_HELP_SET_PERMISSION,
            COMMAND_HELP_COMPASS,
            COMMAND_HELP_OTHER,
            COMMAND_HELP_UPDATE_ITEM,
            COMMAND_COMPASS_DISABLED,
            COMMAND_COMPASS_LOCKED,
            COMMAND_COMPASS_SET_SUCCESS,
            COMMAND_SET_PRIVATE_WRONG_USAGE,
            COMMAND_SET_PRIVATE_NAME_DUPLICATE,
            COMMAND_SET_PRIVATE_LIMIT_REACHED,
            COMMAND_SET_PRIVATE_SUCCESS,
            COMMAND_SET_PUBLIC_WRONG_USAGE,
            COMMAND_SET_PUBLIC_NAME_DUPLICATE,
            COMMAND_SET_PUBLIC_SUCCESS,
            COMMAND_SET_PERMISSION_WRONG_USAGE,
            COMMAND_SET_PERMISSION_NAME_DUPLICATE,
            COMMAND_SET_PERMISSION_SUCCESS,
            COMMAND_CREATE_FOLDER_WRONG_USAGE,
            COMMAND_CREATE_FOLDER_NAME_DUPLICATE,
            COMMAND_CREATE_FOLDER_LIMIT_REACHED,
            COMMAND_CREATE_FOLDER_SUCCESS,
            COMMAND_OTHER_WRONG_USAGE,
            COMMAND_OTHER_NOT_A_VALID_UUID_OR_PLAYER,
            COMMAND_OTHER_UUID_NOT_FOUND,
            COMMAND_OTHER_PLAYER_NAME_NOT_FOUND,
            COMMAND_UPDATE_ITEM_DISABLED,
            COMMAND_UPDATE_ITEM_NOT_A_VALID_ITEM,
            COMMAND_UPDATE_ITEM_WRONG_USAGE,
            COMMAND_UPDATE_ITEM_WAYPOINT_SUCCESS,
            COMMAND_UPDATE_ITEM_FOLDER_SUCCESS,
            COMMAND_RENAME_DISABLED,
            COMMAND_RENAME_WAYPOINT_PRIVATE_DISABLED,
            COMMAND_RENAME_WAYPOINT_PUBLIC_DISABLED,
            COMMAND_RENAME_WAYPOINT_PERMISSION_DISABLED,
            COMMAND_RENAME_FOLDER_DISABLED,
            COMMAND_RENAME_WRONG_USAGE,
            COMMAND_RENAME_WAYPOINT_PRIVATE_NAME_DUPLICATE,
            COMMAND_RENAME_WAYPOINT_PUBLIC_NAME_DUPLICATE,
            COMMAND_RENAME_WAYPOINT_PERMISSION_NAME_DUPLICATE,
            COMMAND_RENAME_WAYPOINT_SUCCESS,
            COMMAND_RENAME_FOLDER_NAME_DUPLICATE,
            COMMAND_RENAME_FOLDER_SUCCESS,
            COMMAND_NOT_FOUND;
    //</editor-fold>
}
