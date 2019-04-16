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
