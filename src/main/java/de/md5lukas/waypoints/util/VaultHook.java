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

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    private static Economy economy = null;

    public static String setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return "Vault is not installed on the server";
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return "A economy plugin has not been registered in Vault";
        }
        economy = rsp.getProvider();
        return null;
    }

    public static boolean withdraw(Player player, long amount) {
        if (economy == null) {
            throw new IllegalStateException("There was an attempt to do a withdrawal from a player using vault, but vault has not been setup");
        }
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    public static long getBalance(Player player) {
        if (economy == null) {
            throw new IllegalStateException("There was an attempt to check the balance of a player using vault, but vault has not been setup");
        }
        return (long) Math.floor(economy.getBalance(player));
    }
}
