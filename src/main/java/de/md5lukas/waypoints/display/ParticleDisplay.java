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

import de.md5lukas.waypoints.data.waypoint.Waypoint;
import de.md5lukas.waypoints.util.PlayerItemCheckRunner;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import static de.md5lukas.waypoints.config.WPConfig.getDisplayConfigs;

public final class ParticleDisplay extends WaypointDisplay {

    protected ParticleDisplay(Plugin plugin) {
        super(plugin, getDisplayConfigs().getParticleConfig().getInterval());
    }

    @Override
    public void show(Player player, Waypoint waypoint) {
        update(player, waypoint);
    }

    @Override
    public void update(Player player, Waypoint waypoint) {
        if (player.getWorld().equals(waypoint.getLocation().getWorld()) && PlayerItemCheckRunner.canPlayerUseDisplays(player)) {
            Location pLoc = player.getLocation();
            Vector dir = waypoint.getLocation().toVector().subtract(pLoc.toVector()).normalize()
                    .multiply(getDisplayConfigs().getParticleConfig().getDistance());
            for (int i = 0; i < getDisplayConfigs().getParticleConfig().getAmount(); i++) {
                player.spawnParticle(getDisplayConfigs().getParticleConfig().getParticle(),
                        pLoc.getX() + dir.getX() * i,
                        pLoc.getY() + getDisplayConfigs().getParticleConfig().getHeightOffset() + (getDisplayConfigs().getParticleConfig().isVerticalDirection()
                                ? dir.getY() * i : 0),
                        pLoc.getZ() + dir.getZ() * i,
                        1, 0, 0, 0, 0);
            }
        }
    }

    @Override
    public void disable(Player player, Waypoint waypoint) {
    }
}
