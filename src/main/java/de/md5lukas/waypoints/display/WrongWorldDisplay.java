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

import de.md5lukas.commons.StringHelper;
import de.md5lukas.waypoints.config.WPConfig;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import de.md5lukas.waypoints.util.PlayerItemCheckRunner;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static de.md5lukas.waypoints.Waypoints.getTranslations;

public final class WrongWorldDisplay extends WaypointDisplay {

    protected WrongWorldDisplay(Plugin plugin) {
        super(plugin, WPConfig.getDisplayConfig().getWrongWorldConfig().getInterval());
    }

    @Override
    public void show(Player player, Waypoint waypoint) {
        update(player, waypoint);
    }

    @Override
    public void update(Player player, Waypoint waypoint) {
        if (waypoint != null && !player.getWorld().equals(waypoint.getLocation().getWorld()) && PlayerItemCheckRunner.canPlayerUseDisplays(player)) {
            TextComponent component = new TextComponent(StringHelper.multiReplace(getTranslations().DISPLAY_WRONG_WORLD.getAsString(player),
                    "%currentworld%", WPConfig.getGeneralConfig().translateWorldName(player.getWorld().getName(), player),
                    "%correctworld%", WPConfig.getGeneralConfig().translateWorldName(waypoint.getLocation().getWorld().getName(), player)));
            player.spigot().sendMessage(WPConfig.getDisplayConfig().getWrongWorldConfig().isActionBar() ? ChatMessageType.ACTION_BAR : ChatMessageType.CHAT,
                    component);
        }
    }

    @Override
    public void disable(Player player, Waypoint waypoint) {
    }
}
