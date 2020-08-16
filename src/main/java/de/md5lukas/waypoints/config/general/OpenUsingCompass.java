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

package de.md5lukas.waypoints.config.general;

import org.bukkit.event.block.Action;

import java.util.Arrays;

public enum OpenUsingCompass {

    LEFT("left"), RIGHT("right"), FALSE("false");

    private final String inConfig;

    OpenUsingCompass(String inConfig) {
        this.inConfig = inConfig;
    }

    public boolean isValidAction(Action action) {
        switch (action) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                return this == LEFT;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                return this == RIGHT;
            default:
                return false;
        }
    }

    public static OpenUsingCompass getFromConfig(String inConfig) {
        return Arrays.stream(values()).filter(variant -> variant.inConfig.equalsIgnoreCase(inConfig)).findFirst()
                .orElse(FALSE); // TODO replace with enum matcher when new md5-commons is in place
    }
}
