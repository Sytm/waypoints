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

package de.md5lukas.waypoints.display;

import de.md5lukas.commons.inventory.ItemBuilder;
import de.md5lukas.waypoints.Messages;
import de.md5lukas.waypoints.store.WPConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static de.md5lukas.waypoints.Messages.*;

public enum BlockColor {

    CLEAR(null, INVENTORY_SELECT_BEACON_COLOR_CLEAR_DISPLAY_NAME, INVENTORY_SELECT_BEACON_COLOR_CLEAR_DESCRIPTION),
    LIGHT_GRAY(Material.LIGHT_GRAY_STAINED_GLASS, INVENTORY_SELECT_BEACON_COLOR_LIGHT_GRAY_DISPLAY_NAME, INVENTORY_SELECT_BEACON_COLOR_LIGHT_GRAY_DESCRIPTION),
    GRAY(Material.GRAY_STAINED_GLASS, INVENTORY_SELECT_BEACON_COLOR_GRAY_DISPLAY_NAME, INVENTORY_SELECT_BEACON_COLOR_GRAY_DESCRIPTION),
    PINK(Material.PINK_STAINED_GLASS, INVENTORY_SELECT_BEACON_COLOR_PINK_DISPLAY_NAME, INVENTORY_SELECT_BEACON_COLOR_PINK_DESCRIPTION),
    LIME(Material.LIME_STAINED_GLASS, INVENTORY_SELECT_BEACON_COLOR_LIME_DISPLAY_NAME, INVENTORY_SELECT_BEACON_COLOR_LIME_DESCRIPTION),
    YELLOW(Material.YELLOW_STAINED_GLASS, INVENTORY_SELECT_BEACON_COLOR_YELLOW_DISPLAY_NAME, INVENTORY_SELECT_BEACON_COLOR_YELLOW_DESCRIPTION),
    LIGHT_BLUE(Material.LIGHT_BLUE_STAINED_GLASS, INVENTORY_SELECT_BEACON_COLOR_LIGHT_BLUE_DISPLAY_NAME, INVENTORY_SELECT_BEACON_COLOR_LIGHT_BLUE_DESCRIPTION),
    MAGENTA(Material.MAGENTA_STAINED_GLASS, INVENTORY_SELECT_BEACON_COLOR_MAGENTA_DISPLAY_NAME, INVENTORY_SELECT_BEACON_COLOR_MAGENTA_DESCRIPTION),
    ORANGE(Material.ORANGE_STAINED_GLASS, INVENTORY_SELECT_BEACON_COLOR_ORANGE_DISPLAY_NAME, INVENTORY_SELECT_BEACON_COLOR_ORANGE_DESCRIPTION),
    WHITE(Material.WHITE_STAINED_GLASS, INVENTORY_SELECT_BEACON_COLOR_WHITE_DISPLAY_NAME, INVENTORY_SELECT_BEACON_COLOR_WHITE_DESCRIPTION),
    BLACK(Material.BLACK_STAINED_GLASS, INVENTORY_SELECT_BEACON_COLOR_BLACK_DISPLAY_NAME, INVENTORY_SELECT_BEACON_COLOR_BLACK_DESCRIPTION),
    RED(Material.RED_STAINED_GLASS, INVENTORY_SELECT_BEACON_COLOR_RED_DISPLAY_NAME, INVENTORY_SELECT_BEACON_COLOR_RED_DESCRIPTION),
    GREEN(Material.GREEN_STAINED_GLASS, INVENTORY_SELECT_BEACON_COLOR_GREEN_DISPLAY_NAME, INVENTORY_SELECT_BEACON_COLOR_GREEN_DESCRIPTION),
    BROWN(Material.BROWN_STAINED_GLASS, INVENTORY_SELECT_BEACON_COLOR_BROWN_DISPLAY_NAME, INVENTORY_SELECT_BEACON_COLOR_BROWN_DESCRIPTION),
    BLUE(Material.BLUE_STAINED_GLASS, INVENTORY_SELECT_BEACON_COLOR_BLUE_DISPLAY_NAME, INVENTORY_SELECT_BEACON_COLOR_BLUE_DESCRIPTION),
    CYAN(Material.CYAN_STAINED_GLASS, INVENTORY_SELECT_BEACON_COLOR_CYAN_DISPLAY_NAME, INVENTORY_SELECT_BEACON_COLOR_CYAN_DESCRIPTION),
    PURPLE(Material.PURPLE_STAINED_GLASS, INVENTORY_SELECT_BEACON_COLOR_PURPLE_DISPLAY_NAME, INVENTORY_SELECT_BEACON_COLOR_PURPLE_DESCRIPTION),
    ;

    private String config;
    private Material material;
    private Messages displayName, description;
    private BlockData blockData;

    BlockColor(Material material, Messages displayName, Messages description) {
        this.material = material;
        this.displayName = displayName;
        this.description = description;
        if (material != null)
            this.blockData = Bukkit.createBlockData(material);
    }

    public Material getMaterial() {
        return material;
    }

    public ItemStack asInventoryItem(Player p) {
        Material itemMat = material;
        if (this == CLEAR) {
            itemMat = Material.GLASS;
        }
        return new ItemBuilder(itemMat).name(displayName.getRaw(p)).lore(description.getRaw(p), WPConfig.inventory().getMaxDescriptionLineLength()).make();
    }

    public BlockData getBlockData() {
        return blockData;
    }
}
