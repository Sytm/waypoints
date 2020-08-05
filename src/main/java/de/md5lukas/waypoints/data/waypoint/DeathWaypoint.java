/*
 *     Waypoints2, A plugin for spigot to add waypoints functionality
 *     Copyright (C) 2020  Lukas Planz
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

package de.md5lukas.waypoints.data.waypoint;

import de.md5lukas.i18n.translations.ItemTranslation;
import de.md5lukas.nbt.tags.CompoundTag;
import de.md5lukas.waypoints.Waypoints;
import de.md5lukas.waypoints.config.WPConfig;
import de.md5lukas.waypoints.display.BlockColor;
import de.md5lukas.waypoints.gui.GUIType;
import org.bukkit.Location;
import org.bukkit.Material;

public class DeathWaypoint extends Waypoint {

    public DeathWaypoint(CompoundTag tag) {
        super(tag);
    }

    public DeathWaypoint(Location location) {
        super("", location);
    }

    @Override
    public Material getMaterial() {
        return WPConfig.getInventoryConfig().getDeathWaypointMenuConfig().getItem();
    }

    @Override
    public ItemTranslation getItemTranslation() {
        return Waypoints.getITranslations().WAYPOINT_PUBLIC;
    }

    @Override
    public GUIType getType() {
        return GUIType.DEATH_WAYPOINT;
    }

    @Override
    public BlockColor getBeaconColor() {
        return beaconColor == null ? WPConfig.getDisplayConfig().getBeaconConfig().getDefaultColorDeath() : beaconColor;
    }
}
