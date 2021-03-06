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
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import de.md5lukas.waypoints.util.PlayerItemCheckRunner;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static de.md5lukas.waypoints.config.WPConfig.getDisplayConfig;

public final class ActionBarDisplay extends WaypointDisplay {

    private final ConcurrentHashMap<Player, Location> players = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Player, String> bars = new ConcurrentHashMap<>();

    protected ActionBarDisplay(Plugin plugin) {
        super(plugin, getDisplayConfig().getActionBarConfig().getInterval());
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            List<Player> notFound = new ArrayList<>();
            bars.forEach((player, str) -> {
                if (!players.containsKey(player))
                    notFound.add(player);
            });
            notFound.forEach(bars::remove);
            players.forEach((player, location) -> {
                if (player.getWorld().equals(location.getWorld()) && PlayerItemCheckRunner.canPlayerUseDisplays(player)) {
                    bars.put(player, generateDirectionIndicator(deltaAngleToTarget(player.getLocation(), location)));
                } else {
                    bars.remove(player);
                }
            });
        }, getDisplayConfig().getActionBarConfig().getInterval(), getDisplayConfig().getActionBarConfig().getInterval());
    }

    @Override
    public void show(Player player, Waypoint waypoint) {
        players.put(player, waypoint.getLocation());
    }

    @Override
    public void update(Player player, Waypoint waypoint) {
        if (bars.containsKey(player)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(bars.get(player)));
        }
    }

    @Override
    public void disable(Player player, Waypoint waypoint) {
        players.remove(player);
    }

    private static String generateDirectionIndicator(double angle) {
        if (angle > getDisplayConfig().getActionBarConfig().getRange()) {
            return getDisplayConfig().getActionBarConfig().getIndicatorColor() + getDisplayConfig().getActionBarConfig().getLeftArrow() + getDisplayConfig()
                    .getActionBarConfig().getNormalColor() + StringHelper
                    .repeatString(getDisplayConfig().getActionBarConfig().getSection(), getDisplayConfig().getActionBarConfig().getAmountOfSections())
                    + getDisplayConfig().getActionBarConfig().getRightArrow();
        }
        if (-angle > getDisplayConfig().getActionBarConfig().getRange()) {
            return getDisplayConfig().getActionBarConfig().getNormalColor() + getDisplayConfig().getActionBarConfig().getLeftArrow() + StringHelper
                    .repeatString(getDisplayConfig().getActionBarConfig().getSection(),
                            getDisplayConfig().getActionBarConfig().getAmountOfSections()) + getDisplayConfig().getActionBarConfig().getIndicatorColor()
                    + getDisplayConfig().getActionBarConfig().getRightArrow();
        }
        double percent = -(angle / getDisplayConfig().getActionBarConfig().getRange());
        int nthSection = (int) Math.round(((double) (getDisplayConfig().getActionBarConfig().getAmountOfSections() - 1) / 2) * percent);
        nthSection += Math.round((double) getDisplayConfig().getActionBarConfig().getAmountOfSections() / 2);
        return getDisplayConfig().getActionBarConfig().getNormalColor() + getDisplayConfig().getActionBarConfig().getLeftArrow() + StringHelper
                .repeatString(getDisplayConfig().getActionBarConfig().getSection(), nthSection - 1) + getDisplayConfig().getActionBarConfig()
                .getIndicatorColor() + getDisplayConfig().getActionBarConfig().getSection()
                + getDisplayConfig().getActionBarConfig().getNormalColor() + StringHelper.repeatString(getDisplayConfig().getActionBarConfig().getSection(),
                getDisplayConfig().getActionBarConfig().getAmountOfSections() - nthSection) + getDisplayConfig().getActionBarConfig().getRightArrow();
    }

    // Original code: https://bitbucket.org/Md5Lukas/waypoints/src/763ee8314b396fb5441c8eb5e0e7e281375ed989/src/main/java/de/md5lukas/wp/util/MathHelper.java?at=master

    /**
     * The returned values range from -180 to 180 degrees, where as negative numbers mean you look to much left and
     * positive numbers you look too much right
     *
     * @param location The location to calculate the angle from
     * @param target   The target when looked at the angle is 0
     * @return The delta angle
     */
    private double deltaAngleToTarget(Location location, Location target) {
        double playerAngle = location.getYaw() + 90;
        while (playerAngle < 0)
            playerAngle += 360;
        double angle = playerAngle - Math.toDegrees(Math.atan2(location.getZ() - target.getZ(), location.getX() - target.getX())) + 180;
        while (angle > 360)
            angle -= 360;
        if (angle > 180)
            angle = -(360 - angle);
        return angle;
    }
}
