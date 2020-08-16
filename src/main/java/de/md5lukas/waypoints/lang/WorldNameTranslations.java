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

import de.md5lukas.i18n.language.LanguageStorage;
import de.md5lukas.i18n.translations.Translation;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class WorldNameTranslations {

    private final LanguageStorage ls;
    private final Map<String, Translation> nameToTranslationMap;

    public WorldNameTranslations(LanguageStorage ls) {
        this.ls = ls;
        this.nameToTranslationMap = new HashMap<>();
    }

    public void updateNameToTranslationMap() {
        nameToTranslationMap.clear();
        for (World w : Bukkit.getWorlds()) {
            nameToTranslationMap.put(w.getName(), new Translation(ls, "worlds." + w.getName()));
        }
    }

    public String getWorldNameTranslation(CommandSender commandSender, World world) {
        if (world == null) return "null";
        return this.nameToTranslationMap.containsKey(world.getName()) ?
                this.nameToTranslationMap.get(world.getName()).getAsString(commandSender) :
                world.getName();
    }
}
