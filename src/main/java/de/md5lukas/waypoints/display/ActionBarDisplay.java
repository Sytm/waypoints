package de.md5lukas.waypoints.display;

import de.md5lukas.commons.StringHelper;
import de.md5lukas.waypoints.Waypoints;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static de.md5lukas.waypoints.store.WPConfig.displays;

public final class ActionBarDisplay extends WaypointDisplay {

	private ConcurrentHashMap<Player, Location> players = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Player, String> bars = new ConcurrentHashMap<>();

	protected ActionBarDisplay(Plugin plugin) {
		super(plugin, displays().getActionBarInterval());
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			while (!Waypoints.isDisabled()) {
				List<Player> notFound = new ArrayList<>();
				bars.forEach((player, str) -> {
					if (!players.containsKey(player))
						notFound.add(player);
				});
				notFound.forEach(bars::remove);
				players.forEach((player, location) -> {
					bars.put(player, generateDirectionIndicator(deltaAngleToTarget(player.getLocation(), location)));
				});
			}
		});
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
	public void disable(Player player) {
		players.remove(player);
	}

	// Original code: https://bitbucket.org/Md5Lukas/waypoints/src/763ee8314b396fb5441c8eb5e0e7e281375ed989/src/main/java/de/md5lukas/wp/util/MathHelper.java?at=master
	public static String generateDirectionIndicator(double angle) {
		if (angle > displays().getActionBarRange()) {
			return displays().getActionBarIndicatorColor() + displays().getActionBarLeftArrow() + displays().getActionBarNormalColor() + StringHelper.repeatString(displays().getActionBarSection(), displays().getActionBarAmountOfSections()) + displays().getActionBarRightArrow();
		}
		if (-angle > displays().getActionBarRange()) {
			return displays().getActionBarNormalColor() + displays().getActionBarLeftArrow() + StringHelper.repeatString(displays().getActionBarSection(),
				displays().getActionBarAmountOfSections()) + displays().getActionBarIndicatorColor() + displays().getActionBarRightArrow();
		}
		double percent = -(angle / displays().getActionBarRange());
		int nthSection = (int) Math.round(((double) (displays().getActionBarAmountOfSections() - 1) / 2) * percent);
		nthSection += Math.round((double) displays().getActionBarAmountOfSections() / 2);
		return displays().getActionBarNormalColor() + displays().getActionBarLeftArrow() + StringHelper.repeatString(displays().getActionBarSection(), nthSection - 1) + displays().getActionBarIndicatorColor() + displays().getActionBarSection() + displays().getActionBarNormalColor() + StringHelper.repeatString(displays().getActionBarSection(),
			displays().getActionBarAmountOfSections() - nthSection) + displays().getActionBarRightArrow();
	}

	/**
	 * The returned values range from -180 to 180 degrees, where as negative numbers mean you look to much left and
	 * positive numbers you look too much right
	 *
	 * @param location The location to calculate the angle from
	 * @param target The target when looked at the angle is 0
	 *
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
