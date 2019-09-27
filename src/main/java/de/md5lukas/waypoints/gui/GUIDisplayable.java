package de.md5lukas.waypoints.gui;

import de.md5lukas.commons.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface GUIDisplayable {

	Material getMaterial();
	default int getAmount(Player player) {
		return 1;
	}
	String getDisplayName(Player player);
	List<String> getDescription(Player player);
	default ItemStack getStack(Player player) {
		return new ItemBuilder(getMaterial(), getAmount(player)).name(getDisplayName(player)).lore(getDescription(player)).make();
	}
}
