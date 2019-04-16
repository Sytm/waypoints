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
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

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
		contents.fillRow(3, ClickableItem.of(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name("§8-").make(), event -> event.setCancelled(true)));
		contents.set(3, 3, ClickableItem.of(new ItemBuilder(Material.ARROW).name("§6Seite zurück").make(), event -> {
			if (page > 0) {
				--page;
				updateOverviewPage(player, contents);
			}
			event.setCancelled(true);
		}));
		contents.set(3, 4, ClickableItem.of(new ItemBuilder(Material.MILK_BUCKET).name("§6Waypoints deaktivieren").make(), event -> {
			event.setCancelled(true);
			PointerManager.deactivate(player.getUniqueId());
		}));
		contents.set(3, 5, ClickableItem.of(new ItemBuilder(Material.ARROW).name("§6Seite vor").make(), event -> {
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
			meta.setDisplayName("§6" + wp.getName());
			meta.setLore(Arrays.asList(
					"§7Distanz: §e" + MathHelper.DF.format(player.getLocation().distance(wp.getLocation())) + " Blöcke",
					"§7Welt: §e" + wp.getLocation().getWorld().getName(),
					"§7X: §e" + wp.getLocation().getBlockX(),
					"§7Y: §e" + wp.getLocation().getBlockY(),
					"§7Z: §e" + wp.getLocation().getBlockZ()));
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
		contents.fillRect(1, 1, 2, 2, ClickableItem.of(new ItemBuilder(Material.BEACON).name("§6Aktivieren").make(), event -> {
			event.setCancelled(true);
			PointerManager.activate(player.getUniqueId(), selectedWaypoint);
			player.closeInventory();
		}));
		contents.fillRect(1, 6, 2, 7, ClickableItem.of(new ItemBuilder(Material.RED_WOOL).name("§4Löschen").make(), event -> {
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
		contents.set(3, 8, ClickableItem.of(new ItemBuilder(Material.BARRIER).name("§6Zurück").make(), event -> {
			event.setCancelled(true);
			selectedWaypoint = null;
			setupOverview(player, contents);
			updateOverviewPage(player, contents);
		}));
	}
}
