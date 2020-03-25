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

import de.md5lukas.commons.MathHelper;
import de.md5lukas.nbt.tags.CompoundTag;
import de.md5lukas.waypoints.display.BlockColor;
import de.md5lukas.waypoints.store.WPConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

import static de.md5lukas.waypoints.Messages.INVENTORY_WAYPOINT_PERMISSION_DESCRIPTION;
import static de.md5lukas.waypoints.Messages.INVENTORY_WAYPOINT_PERMISSION_DISPLAY_NAME;

public class PermissionWaypoint extends Waypoint {

    private String permission;

    public PermissionWaypoint(CompoundTag tag) {
        super(tag);
        permission = tag.getString("permission");
    }

    public PermissionWaypoint(String name, Location location, String permission) {
        super(name, location);
        this.permission = permission;
    }

    @Override
    public Material getMaterial() {
        return material == null ? WPConfig.inventory().getWaypointPermissionItem() : material;
    }

    @Override
    public String getDisplayName(Player player) {
        return INVENTORY_WAYPOINT_PERMISSION_DISPLAY_NAME.getRaw(player).replace("%name%", name);
    }

    @Override
    public List<String> getDescription(Player player) {
        return INVENTORY_WAYPOINT_PERMISSION_DESCRIPTION.asList(player).replace(
                "%world%", WPConfig.translateWorldName(location.getWorld().getName(), player),
                "%x%", MathHelper.format(location.getX()),
                "%y%", MathHelper.format(location.getY()),
                "%z%", MathHelper.format(location.getZ()),
                "%blockX%", Integer.toString(location.getBlockX()),
                "%blockY%", Integer.toString(location.getBlockY()),
                "%blockZ%", Integer.toString(location.getBlockZ()),
                "%distance%", getDistance2D(player));
    }

    @Override
    public BlockColor getBeaconColor() {
        return beaconColor == null ? WPConfig.displays().getBeaconDefaultColorPermission() : beaconColor;
    }

    public String getPermission() {
        return permission;
    }

    @Override
    public CompoundTag toCompoundTag() {
        CompoundTag tag = super.toCompoundTag();
        tag.putString("permission", permission);
        return tag;
    }
}
