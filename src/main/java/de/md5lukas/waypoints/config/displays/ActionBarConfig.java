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

package de.md5lukas.waypoints.config.displays;

import org.bukkit.configuration.ConfigurationSection;

public class ActionBarConfig {

    private boolean enabled;
    private int interval;
    private String indicatorColor;
    private String normalColor;
    private String section;
    private String leftArrow;
    private String rightArrow;
    private int amountOfSections;
    private int range;

    public void load(ConfigurationSection cfg) {
        enabled = cfg.getBoolean("enabled");
        interval = cfg.getInt("interval");
        indicatorColor = cfg.getString("indicatorColor");
        normalColor = cfg.getString("normalColor");
        section = cfg.getString("section");
        leftArrow = cfg.getString("arrow.left");
        rightArrow = cfg.getString("arrow.right");
        amountOfSections = cfg.getInt("amountOfSections");
        range = cfg.getInt("range");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getInterval() {
        return interval;
    }

    public String getIndicatorColor() {
        return indicatorColor;
    }

    public String getNormalColor() {
        return normalColor;
    }

    public String getSection() {
        return section;
    }

    public String getLeftArrow() {
        return leftArrow;
    }

    public String getRightArrow() {
        return rightArrow;
    }

    public int getAmountOfSections() {
        return amountOfSections;
    }

    public int getRange() {
        return range;
    }
}
