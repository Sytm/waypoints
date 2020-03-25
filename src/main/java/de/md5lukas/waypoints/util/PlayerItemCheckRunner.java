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

import de.md5lukas.waypoints.store.WPConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.function.BiConsumer;

public class PlayerItemCheckRunner implements Runnable {

    private static Map<UUID, Boolean> canPlayerUseDisplays = new HashMap<>();
    private static List<BiConsumer<Player, Boolean>> updateHooks = new ArrayList<>();

    public static boolean canPlayerUseDisplays(Player player) {
        if (WPConfig.getDisplaysActiveWhen() == WPConfig.DisplaysActiveWhen.FALSE) {
            return true;
        }
        return canPlayerUseDisplays.getOrDefault(player.getUniqueId(), false);
    }

    public static void start(Plugin plugin) {
        if (WPConfig.getDisplaysActiveWhen() != WPConfig.DisplaysActiveWhen.FALSE)
            Bukkit.getScheduler().runTaskTimer(plugin, new PlayerItemCheckRunner(), 10L * 20L, 20L);
    }

    public static void playerLeft(Player player) {
        canPlayerUseDisplays.remove(player.getUniqueId());
    }

    public static void registerUpdateHook(BiConsumer<Player, Boolean> hook) {
        updateHooks.add(hook);
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            boolean oldState = canPlayerUseDisplays.getOrDefault(player.getUniqueId(), false);
            boolean newState = WPConfig.getDisplaysActiveWhen().testPlayer(player);
            canPlayerUseDisplays.put(player.getUniqueId(), newState);
            if (oldState != newState) {
                updateHooks.forEach(playerConsumer -> playerConsumer.accept(player, newState));
            }
        });
    }
}
