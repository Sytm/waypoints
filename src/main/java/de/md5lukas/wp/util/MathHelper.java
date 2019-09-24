/*
 *     Waypoints
 *     Copyright (C) 2019  Lukas Planz
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

package de.md5lukas.wp.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class MathHelper {

	public static final DecimalFormat DF = new DecimalFormat("#.##");

	/**
	 * The returned values range from -180 to 180 degrees, where as negative numbers mean you look to much left and
	 * positive numbers you look too much right
	 * @param player The player to calculate the angle from
	 * @param target The target when looked at the angle is 0
	 * @return The delta angle
	 * @throws IllegalArgumentException When the player and target are not in the same world
	 */
	public static double deltaAngleToTarget(Player player, Location target) {
		if (player.getWorld() != target.getWorld())
			throw new IllegalArgumentException("Player and target location not in same world!");
		double playerAngle = player.getLocation().getYaw() + 90;
		while (playerAngle < 0)
			playerAngle += 360;
		double angle = playerAngle - Math.toDegrees(Math.atan2(player.getLocation().getZ() - target.getZ(), player.getLocation().getX() - target.getX())) + 180;
		while (angle > 360)
			angle -= 360;
		if (angle > 180)
			angle = -(360 - angle);
		return angle;
	}
}
