package de.md5lukas.wp.inventory;

import de.md5lukas.wp.PointerManager;
import de.md5lukas.wp.config.Config;
import de.md5lukas.wp.storage.Waypoint;
import de.md5lukas.wp.storage.WaypointStorage;
import de.md5lukas.wp.util.ItemBuilder;
import de.md5lukas.wp.util.PaginationList;
import de.md5lukas.wp.util.StringHelper;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInvsPlugin;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static de.md5lukas.wp.config.Message.*;
import static de.md5lukas.wp.config.Messages.get;

public class WaypointProvider implements InventoryProvider {

	private static final int waypointsPerPage = 27;

	private InventoryContents contents;
	private Player viewer;
	private UUID target;
	private PaginationList<Waypoint> waypoints;
	private int page;
	private Waypoint selectedWaypoint;

	public WaypointProvider(UUID target) {
		this.target = target;
	}

	@Override
	public void init(Player player, InventoryContents inventoryContents) {
		viewer = player;
		contents = inventoryContents;
		waypoints = new PaginationList<>(waypointsPerPage);
		rebuildWaypointsList();
		page = 0;
		setupOverview();
		updateOverviewPage();
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {
	}

	private void rebuildWaypointsList() {
		waypoints.clear();
		if (viewer.getUniqueId().equals(target)) {
			waypoints.addAll(WaypointStorage.getPermissionWaypoints().stream()
					.filter(wp -> (viewer.hasPermission(wp.getPermission()) || viewer.hasPermission("waypoints.admin")))
					.collect(Collectors.toList()));
			waypoints.addAll(WaypointStorage.getGlobalWaypoints());
		}
		waypoints.addAll(WaypointStorage.getWaypoints(target));
	}

	private void setupOverview() {
		contents.fill(ClickableItem.empty(new ItemStack(Material.AIR)));
		contents.fillRow(3, ClickableItem.of(new ItemBuilder(Config.inventoryEmptyItem).name(get(INV_EMPTYBACKGROUND)).make(), event -> event.setCancelled(true)));
		contents.set(3, 3, ClickableItem.of(new ItemBuilder(Config.inventoryArrowItem).name(get(INV_PREV_PAGE)).make(), event -> {
			if (page > 0) {
				--page;
				updateOverviewPage();
			}
			event.setCancelled(true);
		}));
		contents.set(3, 4, ClickableItem.of(new ItemBuilder(Config.inventoryDeselectItem).name(get(INV_DISABLE_WAYPOINTS)).make(), event -> {
			event.setCancelled(true);
			PointerManager.deactivate(viewer.getUniqueId());
		}));
		contents.set(3, 5, ClickableItem.of(new ItemBuilder(Config.inventoryArrowItem).name(get(INV_NEXT_PAGE)).make(), event -> {
			if (page < (waypoints.pages() - 1)) {
				++page;
				updateOverviewPage();
			}
			event.setCancelled(true);
		}));
	}

	private void updateOverviewPage() {
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
			ItemStack stack = new ItemStack(Config.inventoryWaypointItem);
			ItemMeta meta = stack.getItemMeta();
			if (wp.isGlobal()) {
				if (wp.getPermission() == null) {
					meta.setDisplayName(get(INV_WAYPOINT_NAME_GLOBAL).replace("%name%", wp.getName()));
				} else {
					meta.setDisplayName(get(INV_WAYPOINT_NAME_PERMISSION).replace("%name%", wp.getName()));
				}
			} else {
				meta.setDisplayName(get(INV_WAYPOINT_NAME).replace("%name%", wp.getName()));
			}
			meta.setLore(Arrays.asList(get(INV_WAYPOINT_LORE)
					.replace("%world%", StringHelper.correctWorldName(wp.getLocation().getWorld()))
					.replace("%x%", wp.getLocation().getBlockX() + "")
					.replace("%y%", wp.getLocation().getBlockY() + "")
					.replace("%z%", wp.getLocation().getBlockZ() + "")
					.replace("%distance%", StringHelper.fixDistance(viewer.getLocation(), wp.getLocation()))
					.split("\\r?\\n")));
			stack.setItemMeta(meta);
			contents.set(row, column, ClickableItem.of(stack, event -> {
				event.setCancelled(true);
				selectedWaypoint = wp;
				page = 0;
				changeToSingleWaypoint();
			}));
		}
	}

	private void changeToSingleWaypoint() {
		// Clear
		contents.fill(ClickableItem.empty(new ItemStack(Material.AIR)));

		// Select
		contents.fillRect(1, 1, 2, 2, ClickableItem.of(new ItemBuilder(Config.inventorySelectWaypointItem).name(get(INV_WAYPOINT_SELECT)).make(), event -> {
			event.setCancelled(true);
			PointerManager.activate(viewer.getUniqueId(), selectedWaypoint);
			viewer.closeInventory();
		}));

		// Info
		ItemStack stack = new ItemStack(Config.inventoryWaypointItem);
		ItemMeta meta = stack.getItemMeta();
		if (selectedWaypoint.isGlobal()) {
			if (selectedWaypoint.getPermission() == null) {
				meta.setDisplayName(get(INV_WAYPOINT_NAME_GLOBAL).replace("%name%", selectedWaypoint.getName()));
			} else {
				meta.setDisplayName(get(INV_WAYPOINT_NAME_PERMISSION).replace("%name%", selectedWaypoint.getName()));
			}
		} else {
			meta.setDisplayName(get(INV_WAYPOINT_NAME).replace("%name%", selectedWaypoint.getName()));
		}
		meta.setLore(Arrays.asList(get(INV_WAYPOINT_LORE)
				.replace("%world%", StringHelper.correctWorldName(selectedWaypoint.getLocation().getWorld()))
				.replace("%x%", selectedWaypoint.getLocation().getBlockX() + "")
				.replace("%y%", selectedWaypoint.getLocation().getBlockY() + "")
				.replace("%z", selectedWaypoint.getLocation().getBlockZ() + "")
				.replace("%distance%", StringHelper.fixDistance(viewer.getLocation(), selectedWaypoint.getLocation()))
				.split("\\r?\\n")));
		stack.setItemMeta(meta);
		contents.set(1, 4, ClickableItem.of(stack, event -> event.setCancelled(true)));

		// Teleport
		if (this.viewer.hasPermission("waypoints.teleport")) {
			contents.set(3, 4, ClickableItem.of(new ItemBuilder(Config.teleportToWaypointItem).name(get(INV_WAYPOINT_TELEPORT)).make(), event -> {
				event.setCancelled(true);
				this.viewer.closeInventory();
				this.viewer.teleport(selectedWaypoint.getLocation());
			}));
		}

		// Delete
		if (!selectedWaypoint.isGlobal() || viewer.hasPermission("waypoints.admin")) {
			contents.fillRect(1, 6, 2, 7, ClickableItem.of(new ItemBuilder(Config.inventoryDeleteWaypointItem).name(get(INV_WAYPOINT_DELETE)).make(), event -> {
				event.setCancelled(true);
				if (selectedWaypoint.isGlobal()) {
					if (selectedWaypoint.getPermission() == null) {
						WaypointStorage.getGlobalWaypoints().remove(selectedWaypoint);
					} else {
						WaypointStorage.getPermissionWaypoints().remove(selectedWaypoint);
					}
				} else {
					WaypointStorage.getWaypoints(target).remove(selectedWaypoint);
				}
				final Waypoint finalSelectedWaypoint = selectedWaypoint;
				Bukkit.getOnlinePlayers().forEach(p -> {
					if (finalSelectedWaypoint.equals(PointerManager.activeWaypoint(p.getUniqueId()))) {
						PointerManager.deactivate(p.getUniqueId());
					}
					SmartInvsPlugin.manager().getInventory(p).ifPresent(sInv -> {
						if (sInv.getId().equals("waypoints")) {
							WaypointProvider other = (WaypointProvider) sInv.getProvider();
							if (other.target == target || finalSelectedWaypoint.isGlobal()) {
								other.rebuildWaypointsList();
								if (finalSelectedWaypoint.equals(other.selectedWaypoint)) {
									other.page = 0;
									other.selectedWaypoint = null;
									other.setupOverview();
									other.updateOverviewPage();
								} else if (other.selectedWaypoint == null) {
									other.page = 0;
									other.setupOverview();
									other.updateOverviewPage();
								}
							}
						}
					});
				});
				selectedWaypoint = null;
				rebuildWaypointsList();
				setupOverview();
				updateOverviewPage();
				page = 0;
			}));
		}

		// Back
		contents.set(3, 8, ClickableItem.of(new ItemBuilder(Config.inventoryBackItem).name(get(INV_BACK)).make(), event -> {
			event.setCancelled(true);
			selectedWaypoint = null;
			setupOverview();
			updateOverviewPage();
		}));
	}
}
