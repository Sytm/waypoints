package de.md5lukas.waypoints.gui;

import de.md5lukas.commons.UUIDUtils;
import fr.minuskube.inv.SmartInventory;
import org.bukkit.entity.Player;

import java.util.UUID;

import static de.md5lukas.waypoints.Waypoints.message;
import static de.md5lukas.waypoints.Messages.*;

public class GUIManager {

	public static void openGUI(Player player) {
		SmartInventory.builder().id(player.getUniqueId().toString())
			.provider(new WaypointProvider(player.getUniqueId())).title(message(INVENTORY_TITLE_OWN, player)).build().open(player);
	}

	public static void openGUI(Player player, UUID target) {
		SmartInventory.builder().id(player.getUniqueId() + "|" + target)
			.provider(new WaypointProvider(target)).title(message(INVENTORY_TITLE_OTHER, player).replace("%name%", UUIDUtils.getName(target))).build().open(player);
	}
}
