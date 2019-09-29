package de.md5lukas.waypoints.display;

import de.md5lukas.commons.StringHelper;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import de.md5lukas.waypoints.store.WPConfig;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


import static de.md5lukas.waypoints.Waypoints.message;
import static de.md5lukas.waypoints.Messages.DISPLAY_WRONG_WORLD;

public final class WrongWorldDisplay extends WaypointDisplay {

	protected WrongWorldDisplay(Plugin plugin) {
		super(plugin, WPConfig.displays().getWrongWorldInterval());
	}

	@Override
	public void show(Player player, Waypoint waypoint) {
		update(player, waypoint);
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public void update(Player player, Waypoint waypoint) {
		if (!player.getWorld().equals(waypoint.getLocation().getWorld())) {
			TextComponent component = new TextComponent(StringHelper.multiReplace(message(DISPLAY_WRONG_WORLD, player),
				"%currentworld%", WPConfig.translateWorldName(player.getWorld().getName(), player),
				"%correctworld%", WPConfig.translateWorldName(waypoint.getLocation().getWorld().getName(), player)));
			player.spigot().sendMessage(WPConfig.displays().isWrongWorldActionBar() ? ChatMessageType.ACTION_BAR : ChatMessageType.CHAT, component);
		}
	}

	@Override
	public void disable(Player player) {
	}
}
