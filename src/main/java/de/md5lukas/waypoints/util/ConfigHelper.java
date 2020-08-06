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

package de.md5lukas.waypoints.util;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ConfigHelper {

    public static Map<String, String> getTranslationsFromConfig(FileConfiguration cfg) {
        String variablePrefix = "variables.";
        Map<String, String> variables = new HashMap<>();
        Map<String, String> translations = new HashMap<>();

        for (String path : cfg.getKeys(true)) {
            if (cfg.isString(path)) {
                if (path.startsWith(variablePrefix)) {
                    variables.put(path.substring(variablePrefix.length()), cfg.getString(path));
                } else {
                    translations.put(path, cfg.getString(path));
                }
            }
        }
        for (Map.Entry<String, String> translation : translations.entrySet()) {
            String string = translation.getValue();
            for (Map.Entry<String, String> variable : variables.entrySet()) {
                string = string.replace("$" + variable.getKey() + "$", variable.getValue());
            }
            translation.setValue(string);
        }

        return translations;
    }
}
