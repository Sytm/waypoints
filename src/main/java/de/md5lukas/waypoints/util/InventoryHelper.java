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

package de.md5lukas.waypoints.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryHelper {

    public static boolean hasPlayerItem(Player player, Material material) {
        return player.getInventory().first(material) >= 0;
    }

    public static boolean hasPlayerItemInHotbar(Player player, Material material) {
        PlayerInventory inv = player.getInventory();
        for (int i = 0; i <= 8; i++) {
            ItemStack stack = inv.getItem(i);
            if (stack != null && stack.getType() == material) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasPlayerItemInHand(Player player, Material material) {
        return player.getInventory().getItemInMainHand().getType() == material ||
                player.getInventory().getItemInOffHand().getType() == material;
    }
}
