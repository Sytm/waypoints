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

package de.md5lukas.waypoints.data.folder;

import de.md5lukas.nbt.tags.CompoundTag;
import de.md5lukas.nbt.tags.ListTag;
import de.md5lukas.waypoints.data.waypoint.PrivateWaypoint;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import de.md5lukas.waypoints.gui.GUIType;
import de.md5lukas.waypoints.store.WPConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

import static de.md5lukas.waypoints.Messages.INVENTORY_FOLDER_PRIVATE_DESCRIPTION;
import static de.md5lukas.waypoints.Messages.INVENTORY_FOLDER_PRIVATE_DISPLAY_NAME;

public class PrivateFolder extends Folder {

    public PrivateFolder(CompoundTag tag) {
        super(tag);
    }

    public PrivateFolder(String name) {
        super(name);
    }

    @Override
    protected List<Waypoint> loadWaypoints(ListTag waypoints) {
        return waypoints.values().stream().map(tag -> new PrivateWaypoint((CompoundTag) tag)).collect(Collectors.toList());
    }

    @Override
    public Material getMaterial() {
        return material == null ? WPConfig.inventory().getFolderPrivateDefaultItem() : material;
    }

    @Override
    public String getDisplayName(Player player) {
        return INVENTORY_FOLDER_PRIVATE_DISPLAY_NAME.getRaw(player).replace("%name%", getName());
    }

    @Override
    public List<String> getDescription(Player player) {
        return INVENTORY_FOLDER_PRIVATE_DESCRIPTION.asList(player).replace(
                "%amount%", Integer.toString(waypoints.size()));
    }

    @Override
    protected boolean isCorrectWaypointType(Waypoint waypoint) {
        return waypoint instanceof PrivateWaypoint;
    }

    @Override
    public GUIType getType() {
        return GUIType.PRIVATE_FOLDER;
    }
}
