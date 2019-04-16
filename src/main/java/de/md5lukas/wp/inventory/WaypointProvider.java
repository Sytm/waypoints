package de.md5lukas.wp.inventory;

import de.md5lukas.wp.PointerManager;
import de.md5lukas.wp.storage.Waypoint;
import de.md5lukas.wp.storage.WaypointStorage;
import de.md5lukas.wp.util.ItemBuilder;
import de.md5lukas.wp.util.MathHelper;
import de.md5lukas.wp.util.PaginationList;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

import static de.md5lukas.wp.config.Message.*;
import static de.md5lukas.wp.config.Messages.get;

public class WaypointProvider implements InventoryProvider {

	private PaginationList<Waypoint> waypoints;
	private int page;
	private Waypoint selectedWaypoint;

	@Override
	public void init(Player player, InventoryContents inventoryContents) {
		waypoints = WaypointStorage.getWaypoints(player.getUniqueId());
		page = 0;
		setupOverview(player, inventoryContents);
		updateOverviewPage(player, inventoryContents);
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {
	}

	private void setupOverview(Player player, InventoryContents contents) {
		contents.fill(ClickableItem.empty(new ItemStack(Material.AIR)));
		contents.fillRow(3, ClickableItem.of(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(get(INV_EMPTYBACKGROUND)).make(), event -> event.setCancelled(true)));
		contents.set(3, 3, ClickableItem.of(new ItemBuilder(Material.ARROW).name(get(INV_PREV_PAGE)).make(), event -> {
			if (page > 0) {
				--page;
				updateOverviewPage(player, contents);
			}
			event.setCancelled(true);
		}));
		contents.set(3, 4, ClickableItem.of(new ItemBuilder(Material.MILK_BUCKET).name(get(INV_DISABLE_WAYPOINTS)).make(), event -> {
			event.setCancelled(true);
			PointerManager.deactivate(player.getUniqueId());
		}));
		contents.set(3, 5, ClickableItem.of(new ItemBuilder(Material.ARROW).name(get(INV_NEXT_PAGE)).make(), event -> {
			if (page < (waypoints.pages() - 1)) {
				++page;
				updateOverviewPage(player, contents);
			}
			event.setCancelled(true);
		}));
	}

	private void updateOverviewPage(Player player, InventoryContents contents) {
		List<Waypoint> currPageContent = waypoints.page(page);
		if (currPageContent == null) return;
		for (int i = 0; i < 27; ++i) {
			int column = i % 9, row = (i - column) / 9;
			contents.set(row, column, ClickableItem.empty(new ItemStack(Material.AIR)));
		}
		int counter = 0;
		for (Waypoint wp : currPageContent) {
			int column = counter % 9, row = (counter - column) / 9;
			++counter;
			ItemStack stack = new ItemStack(Material.SIGN);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(get(INV_WAYPOINT_NAME).replace("%name%", wp.getName()));
			meta.setLore(Arrays.asList(get(INV_WAYPOINT_LORE)
					.replace("%world%", wp.getLocation().getWorld().getName())
					.replace("%x%", wp.getLocation().getBlockX() + "")
					.replace("%y%", wp.getLocation().getBlockY() + "")
					.replace("%z%", wp.getLocation().getBlockZ() + "")
					.replace("%distance%", MathHelper.DF.format(wp.getLocation().distance(player.getLocation())))
					.split("\\r?\\n")));
			stack.setItemMeta(meta);
			contents.set(row, column, ClickableItem.of(stack, event -> {
				event.setCancelled(true);
				selectedWaypoint = wp;
				page = 0;
				changeToSingleWaypoint(player, contents);
			}));
		}
	}

	private void changeToSingleWaypoint(Player player, InventoryContents contents) {
		contents.fill(ClickableItem.empty(new ItemStack(Material.AIR)));
		contents.fillRect(1, 1, 2, 2, ClickableItem.of(new ItemBuilder(Material.BEACON).name(get(INV_WAYPOINT_SELECT)).make(), event -> {
			event.setCancelled(true);
			PointerManager.activate(player.getUniqueId(), selectedWaypoint);
			player.closeInventory();
		}));
		ItemStack stack = new ItemStack(Material.SIGN);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(get(INV_WAYPOINT_NAME).replace("%name%", selectedWaypoint.getName()));
		meta.setLore(Arrays.asList(get(INV_WAYPOINT_LORE)
				.replace("%world%", selectedWaypoint.getLocation().getWorld().getName())
				.replace("%x%", selectedWaypoint.getLocation().getBlockX() + "")
				.replace("%y%", selectedWaypoint.getLocation().getBlockY() + "")
				.replace("%z", selectedWaypoint.getLocation().getBlockZ() + "")
				.replace("%distance%", MathHelper.DF.format(selectedWaypoint.getLocation().distance(player.getLocation())))
				.split("\\r?\\n")));
		stack.setItemMeta(meta);
		contents.set(1, 4, ClickableItem.of(stack, event -> event.setCancelled(true)));
		contents.fillRect(1, 6, 2, 7, ClickableItem.of(new ItemBuilder(Material.RED_WOOL).name(get(INV_WAYPOINT_DELETE)).make(), event -> {
			event.setCancelled(true);
			Waypoint active = PointerManager.activeWaypoint(player.getUniqueId());
			if (active != null && selectedWaypoint.getID().equals(active.getID())) {
				PointerManager.deactivate(player.getUniqueId());
			}
			waypoints.remove(selectedWaypoint);
			selectedWaypoint = null;
			setupOverview(player, contents);
			updateOverviewPage(player, contents);
			page = 0;
		}));
		contents.set(3, 8, ClickableItem.of(new ItemBuilder(Material.BARRIER).name(get(INV_BACK)).make(), event -> {
			event.setCancelled(true);
			selectedWaypoint = null;
			setupOverview(player, contents);
			updateOverviewPage(player, contents);
		}));
	}
}
