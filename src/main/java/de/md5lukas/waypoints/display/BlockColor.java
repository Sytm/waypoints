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

import de.md5lukas.i18n.translations.ItemTranslation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public enum BlockColor {

    CLEAR(Material.GLASS),
    LIGHT_GRAY(Material.LIGHT_GRAY_STAINED_GLASS),
    GRAY(Material.GRAY_STAINED_GLASS),
    PINK(Material.PINK_STAINED_GLASS),
    LIME(Material.LIME_STAINED_GLASS),
    YELLOW(Material.YELLOW_STAINED_GLASS),
    LIGHT_BLUE(Material.LIGHT_BLUE_STAINED_GLASS),
    MAGENTA(Material.MAGENTA_STAINED_GLASS),
    ORANGE(Material.ORANGE_STAINED_GLASS),
    WHITE(Material.WHITE_STAINED_GLASS),
    BLACK(Material.BLACK_STAINED_GLASS),
    RED(Material.RED_STAINED_GLASS),
    GREEN(Material.GREEN_STAINED_GLASS),
    BROWN(Material.BROWN_STAINED_GLASS),
    BLUE(Material.BLUE_STAINED_GLASS),
    CYAN(Material.CYAN_STAINED_GLASS),
    PURPLE(Material.PURPLE_STAINED_GLASS),
    ;

    private final Material material;
    private final BlockData blockData;
    private ItemTranslation itemTranslation;

    BlockColor(Material material) {
        this.material = material;
        this.blockData = Bukkit.createBlockData(material);
        this.itemTranslation = null;
    }

    public Material getMaterial() {
        return material;
    }

    public BlockData getBlockData() {
        return blockData;
    }

    public void setItemTranslation(ItemTranslation itemTranslation) {
        if (this.itemTranslation == null)
            this.itemTranslation = itemTranslation;
    }

    public ItemTranslation getItemTranslation() {
        return itemTranslation;
    }
}
