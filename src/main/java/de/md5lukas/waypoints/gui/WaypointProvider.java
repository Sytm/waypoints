/*
 *     Waypoints2, A plugin for spigot to add waypoints functionality
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

package de.md5lukas.waypoints.gui;

import com.google.common.collect.ImmutableMap;
import de.md5lukas.commons.collections.PaginationList;
import de.md5lukas.commons.inventory.ItemBuilder;
import de.md5lukas.waypoints.Messages;
import de.md5lukas.waypoints.Waypoints;
import de.md5lukas.waypoints.data.GUISortable;
import de.md5lukas.waypoints.data.WPPlayerData;
import de.md5lukas.waypoints.data.folder.Folder;
import de.md5lukas.waypoints.data.folder.PermissionFolder;
import de.md5lukas.waypoints.data.folder.PrivateFolder;
import de.md5lukas.waypoints.data.folder.PublicFolder;
import de.md5lukas.waypoints.data.waypoint.*;
import de.md5lukas.waypoints.display.WaypointDisplay;
import de.md5lukas.waypoints.store.WPConfig;
import de.md5lukas.waypoints.util.GeneralHelper;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import fr.minuskube.inv.util.Pattern;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Consumer;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static de.md5lukas.waypoints.Messages.*;
import static de.md5lukas.waypoints.Waypoints.message;
import static de.md5lukas.waypoints.store.WPConfig.inventory;

public class WaypointProvider implements InventoryProvider {

	private static final ClickableItem UNIQUE = ClickableItem.empty(null);
	private static final int pagedRows = 4;
	private static int pageSize = pagedRows * 9;
	private static ImmutableMap<WPPlayerData.SortMode, Messages> sortModeMap = ImmutableMap.of(
		WPPlayerData.SortMode.TYPE, INVENTORY_CYCLE_SORT_MODE_TYPE,
		WPPlayerData.SortMode.NAME_ASC, INVENTORY_CYCLE_SORT_MODE_NAME_ASC,
		WPPlayerData.SortMode.NAME_DESC, INVENTORY_CYCLE_SORT_MODE_NAME_DESC,
		WPPlayerData.SortMode.CREATED_ASC, INVENTORY_CYCLE_SORT_MODE_CREATED_ASC,
		WPPlayerData.SortMode.CREATED_DESC, INVENTORY_CYCLE_SORT_MODE_CREATED_DESC);

	//<editor-fold defaultstate="collapsed" desc="Patterns">
	private static final Pattern<ClickableItem> overviewPattern = new Pattern<>(
		"#########",
		"#########",
		"#########",
		"#########",
		"p_s_d_t_n");
	/*
	# = none
	_ = background
	p = Previous
	s = cycle sort
	d = deselect
	t = toggle global folders
	n = next
	 */
	private static final Pattern<ClickableItem> folderPattern = new Pattern<>(
		"#########",
		"#########",
		"#########",
		"#########",
		"p_d_f_rbn");
	/*
	# = none
	_ = background
	p = Previous
	d = delete
	f = folder icon, click -> change
	r = rename folder
	b = back
	n = next
	 */
	private static final Pattern<ClickableItem> waypointPattern = new Pattern<>(
		"____w____",
		"_________",
		"____s____",
		"_f_____r_",
		"d___t___b");
	/*
	w = WaypointItem
	s = select
	d = delete
	f = move to folder
	r = rename
	t = teleport
	b = back
	 */
	private static final Pattern<ClickableItem> selectFolderPattern = new Pattern<>(
		"#########",
		"#########",
		"#########",
		"#########",
		"p__g_b__n");
	/*
	# = none
	_ = background
	b = back to waypoint
	g = no folder
	*/
	private static final Pattern<ClickableItem> confirmPattern = new Pattern<>(
		"_________",
		"____t____",
		"_________",
		"_n_____y_",
		"_________");
	/*
	t = title
	n = no
	y = yes
	 */
	//</editor-fold>

	private UUID target;
	private WPPlayerData targetData, viewerData;
	private boolean isOwner;
	private Player viewer;
	private InventoryContents contents;

	private Folder lastFolder = null;
	private int overviewPage = 0, folderPage = 0, folderListPage = 0;

	public WaypointProvider(UUID target) {
		this.target = target;
		this.targetData = WPPlayerData.getPlayerData(target);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		this.viewer = player;
		this.viewerData = WPPlayerData.getPlayerData(player.getUniqueId());
		this.isOwner = player.getUniqueId().equals(target);
		this.contents = contents;
		showOverview();
	}

	//<editor-fold defaultstate="collapsed" desc="Show pages">
	private void showLast() {
		if (lastFolder == null) {
			showOverview();
		} else {
			showFolder(lastFolder);
		}
	}

	private void showOverview() {
		contents.fill(ClickableItem.NONE);
		overviewPage = Math.min(overviewPage, getOverviewPages());
		lastFolder = null;

		overviewPattern.setDefault(ClickableItem.NONE);
		overviewPattern.attach('_', ClickableItem.empty(ItemStacks.getOverviewBackgroundItem(viewer)));
		overviewPattern.attach('p', ClickableItem.from(ItemStacks.getPreviousItem(viewer), click -> overviewPage = Math.max(0, overviewPage - 1)));
		overviewPattern.attach('n', ClickableItem.from(ItemStacks.getNextItem(viewer), click -> overviewPage = Math.min(getOverviewPages(), overviewPage + 1)));
		overviewPattern.attach('d', ClickableItem.from(ItemStacks.getOverviewDeselectItem(viewer), click -> WaypointDisplay.getAll().disable(viewer)));


		overviewPattern.attach('t', UNIQUE);
		SlotPos globalTogglePos = GeneralHelper.find(overviewPattern, ci -> ci == UNIQUE);
		final AtomicReference<Runnable> globalToggle = new AtomicReference<>();
		ClickableItem globalsShown = ClickableItem.from(ItemStacks.getOverviewToggleGlobalsShownItem(viewer), click -> globalToggle.get().run());
		ClickableItem globalsHidden = ClickableItem.from(ItemStacks.getOverviewToggleGlobalsHiddenItem(viewer), click -> globalToggle.get().run());
		globalToggle.set(() -> {
			if (viewerData.settings().showGlobals()) {
				contents.set(globalTogglePos, globalsHidden);
			} else {
				contents.set(globalTogglePos, globalsHidden);
			}
			viewerData.settings().showGlobals(!viewerData.settings().showGlobals());
		});
		overviewPattern.attach('t', viewerData.settings().showGlobals() ? globalsShown : globalsHidden);


		overviewPattern.attach('s', UNIQUE);
		SlotPos sortCyclePos = GeneralHelper.find(overviewPattern, ci -> ci == UNIQUE);
		final AtomicReference<Runnable> cycleUpdate = new AtomicReference<>();
		cycleUpdate.set(() -> contents.set(sortCyclePos, ClickableItem.from(getSortCycleItem(inventory().getOverviewCycleSortItem()), click -> {
			cycleSortMode();
			updateOverview();
			cycleUpdate.get().run();
		})));
		overviewPattern.attach('s', ClickableItem.from(getSortCycleItem(inventory().getOverviewCycleSortItem()), click -> {
			cycleSortMode();
			updateOverview();
			cycleUpdate.get().run();
		}));
		contents.fillPattern(overviewPattern);

		updateOverview();
	}

	private void showFolder(Folder folder) {
		contents.fill(ClickableItem.NONE);
		folderPage = Math.min(folderPage, getFolderPages(folder));
		lastFolder = folder;
		folderPattern.setDefault(ClickableItem.NONE);
		ClickableItem bg = null;

		if (folder instanceof PrivateFolder)
			bg = ClickableItem.empty(ItemStacks.getFolderPrivateBackgroundItem(viewer));
		else if (folder instanceof PublicFolder)
			bg = ClickableItem.empty(ItemStacks.getFolderPublicBackgroundItem(viewer));
		else if (folder instanceof PermissionFolder)
			bg = ClickableItem.empty(ItemStacks.getFolderPermissionBackgroundItem(viewer));

		folderPattern.attach('_', bg);
		folderPattern.attach('b', ClickableItem.from(ItemStacks.getBackItem(viewer), click -> showOverview()));
		folderPattern.attach('p', ClickableItem.from(ItemStacks.getPreviousItem(viewer), click -> {
			folderPage = Math.max(0, folderPage - 1);
			updateFolder(folder);
		}));
		folderPattern.attach('n', ClickableItem.from(ItemStacks.getNextItem(viewer), click -> {
			folderPage = Math.min(getFolderPages(folder), folderPage + 1);
			updateFolder(folder);
		}));

		if (isOwner && folder instanceof PrivateFolder) {
			folderPattern.attach('d', ClickableItem.from(ItemStacks.getFolderPrivateDeleteItem(viewer),
				click -> showConfirm(INVENTORY_CONFIRM_MENU_FOLDER_PRIVATE_DELETE_DESCRIPTION_DISPLAY_NAME, INVENTORY_CONFIRM_MENU_FOLDER_PRIVATE_DELETE_DESCRIPTION_DESCRIPTION,
					INVENTORY_CONFIRM_MENU_FOLDER_PRIVATE_DELETE_YES_DISPLAY_NAME, INVENTORY_CONFIRM_MENU_FOLDER_PRIVATE_DELETE_YES_DESCRIPTION,
					INVENTORY_CONFIRM_MENU_FOLDER_PRIVATE_DELETE_NO_DISPLAY_NAME, INVENTORY_CONFIRM_MENU_FOLDER_PRIVATE_DELETE_NO_DESCRIPTION,
					result -> {
						if (result) {
							targetData.removeFolder(folder.getID());
						}
						showOverview();
					})));
			folderPattern.attach('f', ClickableItem.from(folder.getStack(viewer), click -> {
				BaseComponent[] components = TextComponent.fromLegacyText(message(CHAT_ACTION_UPDATE_ITEM_FOLDER_PRIVATE, viewer));
				ClickEvent ce = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/waypoints updateItem folder " + folder.getID());
				Arrays.stream(components).forEach(component -> component.setClickEvent(ce));
				viewer.spigot().sendMessage(components);
				viewer.closeInventory();
			}));
			folderPattern.attach('r', ClickableItem.from(ItemStacks.getFolderPrivateRenameItem(viewer), click -> {
				BaseComponent[] components = TextComponent.fromLegacyText(message(CHAT_ACTION_RENAME_FOLDER_PRIVATE, viewer));
				ClickEvent ce = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/waypoints updateItem folder " + folder.getID());
				Arrays.stream(components).forEach(component -> component.setClickEvent(ce));
				viewer.spigot().sendMessage(components);
				viewer.closeInventory();
			}));
		} else {
			folderPattern.attach('d', bg);
			folderPattern.attach('f', ClickableItem.empty(folder.getStack(viewer)));
			folderPattern.attach('r', bg);
		}
		contents.fillPattern(folderPattern);

		updateFolder(folder);
	}

	private void showWaypoint(Waypoint waypoint) {
		contents.fill(ClickableItem.NONE);
		if (waypoint instanceof PrivateWaypoint) {
			ClickableItem bg = ClickableItem.empty(ItemStacks.getWaypointPrivateBackgroundItem(viewer));
			waypointPattern.setDefault(bg);
			if (isOwner) {
				waypointPattern.attach('w', ClickableItem.from(waypoint.getStack(viewer), click -> {
					BaseComponent[] components = TextComponent.fromLegacyText(message(CHAT_ACTION_UPDATE_ITEM_FOLDER_PRIVATE, viewer));
					ClickEvent ce = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/waypoints updateItem waypoint " + waypoint.getID());
					Arrays.stream(components).forEach(component -> component.setClickEvent(ce));
					viewer.spigot().sendMessage(components);
					viewer.closeInventory();
				}));
			} else {
				waypointPattern.attach('w', ClickableItem.empty(waypoint.getStack(viewer)));
			}
			waypointPattern.attach('s', ClickableItem.from(ItemStacks.getWaypointPrivateSelectItem(viewer), click -> {
				WaypointDisplay.getAll().show(viewer, waypoint);
				viewer.closeInventory();
			}));
			if (isOwner || viewer.hasPermission("waypoints.delete.other")) {
				waypointPattern.attach('d', ClickableItem.from(ItemStacks.getWaypointPrivateDeleteItem(viewer),
					click -> showConfirm(INVENTORY_CONFIRM_MENU_WAYPOINT_PRIVATE_DELETE_DESCRIPTION_DISPLAY_NAME,
						INVENTORY_CONFIRM_MENU_WAYPOINT_PRIVATE_DELETE_DESCRIPTION_DESCRIPTION,
						INVENTORY_CONFIRM_MENU_WAYPOINT_PRIVATE_DELETE_YES_DISPLAY_NAME, INVENTORY_CONFIRM_MENU_WAYPOINT_PRIVATE_DELETE_YES_DESCRIPTION,
						INVENTORY_CONFIRM_MENU_WAYPOINT_PRIVATE_DELETE_NO_DISPLAY_NAME, INVENTORY_CONFIRM_MENU_WAYPOINT_PRIVATE_DELETE_NO_DESCRIPTION, result -> {
							if (result) {
								targetData.removeWaypoint(waypoint.getID());
							}
							showLast();
						})));
			} else {
				waypointPattern.attach('d', bg);
			}
			if (isOwner && WPConfig.allowRenamingWaypointsPrivate()) {
				waypointPattern.attach('r', ClickableItem.from(ItemStacks.getWaypointPrivateRenameItem(viewer), click -> {
					BaseComponent[] components = TextComponent.fromLegacyText(message(CHAT_ACTION_RENAME_WAYPOINT_PRIVATE, viewer));
					ClickEvent ce = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/waypoints rename privateWaypoint " + waypoint.getID());
					Arrays.stream(components).forEach(component -> component.setClickEvent(ce));
					viewer.spigot().sendMessage(components);
					viewer.closeInventory();
				}));
			} else {
				waypointPattern.attach('r', bg);
			}
			if (viewer.hasPermission("waypoints.teleport.private")) {
				waypointPattern.attach('t', ClickableItem.from(ItemStacks.getWaypointPrivateTeleportItem(viewer), click -> {
					viewer.closeInventory();
					viewer.teleport(waypoint.getLocation());
				}));
			} else {
				waypointPattern.attach('t', bg);
			}
			waypointPattern.attach('b', ClickableItem.from(ItemStacks.getBackItem(viewer), click -> showLast()));
			if (isOwner) {
				waypointPattern.attach('f', ClickableItem.from(ItemStacks.getWaypointPrivateMoveToFolderItem(viewer), click -> showSelectFolder(waypoint)));
			} else {
				waypointPattern.attach('f', bg);
			}
		} else if (waypoint instanceof PublicWaypoint) {
			ClickableItem bg = ClickableItem.empty(ItemStacks.getWaypointPublicBackgroundItem(viewer));
			waypointPattern.setDefault(bg);
			waypointPattern.attach('w', ClickableItem.empty(waypoint.getStack(viewer)));
			waypointPattern.attach('s', ClickableItem.from(ItemStacks.getWaypointPublicSelectItem(viewer), click -> {
				WaypointDisplay.getAll().show(viewer, waypoint);
				viewer.closeInventory();
			}));
			if (viewer.hasPermission("waypoints.delete.public")) {
				waypointPattern.attach('d', ClickableItem.from(ItemStacks.getWaypointPublicDeleteItem(viewer),
					click -> showConfirm(INVENTORY_CONFIRM_MENU_WAYPOINT_PUBLIC_DELETE_DESCRIPTION_DISPLAY_NAME,
						INVENTORY_CONFIRM_MENU_WAYPOINT_PUBLIC_DELETE_DESCRIPTION_DESCRIPTION,
						INVENTORY_CONFIRM_MENU_WAYPOINT_PUBLIC_DELETE_YES_DISPLAY_NAME, INVENTORY_CONFIRM_MENU_WAYPOINT_PUBLIC_DELETE_YES_DESCRIPTION,
						INVENTORY_CONFIRM_MENU_WAYPOINT_PUBLIC_DELETE_NO_DISPLAY_NAME, INVENTORY_CONFIRM_MENU_WAYPOINT_PUBLIC_DELETE_NO_DESCRIPTION, result -> {
							if (result) {
								Waypoints.getGlobalStore().getPublicFolder().removeWaypoint(waypoint.getID());
							}
							showLast();
						})));
			} else {
				waypointPattern.attach('d', bg);
			}
			if (viewer.hasPermission("waypoints.rename.public") && WPConfig.allowRenamingWaypointsPublic()) {
				waypointPattern.attach('r', ClickableItem.from(ItemStacks.getWaypointPublicRenameItem(viewer), click -> {
					BaseComponent[] components = TextComponent.fromLegacyText(message(CHAT_ACTION_RENAME_WAYPOINT_PUBLIC, viewer));
					ClickEvent ce = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/waypoints rename publicWaypoint " + waypoint.getID());
					Arrays.stream(components).forEach(component -> component.setClickEvent(ce));
					viewer.spigot().sendMessage(components);
					viewer.closeInventory();
				}));
			} else {
				waypointPattern.attach('r', bg);
			}
			if (viewer.hasPermission("waypoints.teleport.public")) {
				waypointPattern.attach('t', ClickableItem.from(ItemStacks.getWaypointPublicTeleportItem(viewer), click -> {
					viewer.closeInventory();
					viewer.teleport(waypoint.getLocation());
				}));
			} else {
				waypointPattern.attach('t', bg);
			}
			waypointPattern.attach('f', bg);
			waypointPattern.attach('b', ClickableItem.from(ItemStacks.getBackItem(viewer), click -> showLast()));
		} else if (waypoint instanceof PermissionWaypoint) {
			ClickableItem bg = ClickableItem.empty(ItemStacks.getWaypointPermissionBackgroundItem(viewer));
			waypointPattern.setDefault(bg);
			waypointPattern.attach('w', ClickableItem.empty(waypoint.getStack(viewer)));
			waypointPattern.attach('s', ClickableItem.from(ItemStacks.getWaypointPermissionSelectItem(viewer), click -> {
				WaypointDisplay.getAll().show(viewer, waypoint);
				viewer.closeInventory();
			}));
			if (viewer.hasPermission("waypoints.delete.permission")) {
				waypointPattern.attach('d', ClickableItem.from(ItemStacks.getWaypointPermissionDeleteItem(viewer), click -> {
					Waypoints.getGlobalStore().getPermissionFolder().removeWaypoint(waypoint.getID());
					showConfirm(INVENTORY_CONFIRM_MENU_WAYPOINT_PERMISSION_DELETE_DESCRIPTION_DISPLAY_NAME,
						INVENTORY_CONFIRM_MENU_WAYPOINT_PERMISSION_DELETE_DESCRIPTION_DESCRIPTION,
						INVENTORY_CONFIRM_MENU_WAYPOINT_PERMISSION_DELETE_YES_DISPLAY_NAME, INVENTORY_CONFIRM_MENU_WAYPOINT_PERMISSION_DELETE_YES_DESCRIPTION,
						INVENTORY_CONFIRM_MENU_WAYPOINT_PERMISSION_DELETE_NO_DISPLAY_NAME, INVENTORY_CONFIRM_MENU_WAYPOINT_PERMISSION_DELETE_NO_DESCRIPTION, result -> {
							if (result) {
								Waypoints.getGlobalStore().getPermissionFolder().removeWaypoint(waypoint.getID());
							}
							showLast();
						});
				}));
			} else {
				waypointPattern.attach('d', bg);
			}
			if (viewer.hasPermission("waypoints.rename.permission") && WPConfig.allowRenamingWaypointsPermission()) {
				waypointPattern.attach('r', ClickableItem.from(ItemStacks.getWaypointPermissionRenameItem(viewer), click -> {
					BaseComponent[] components = TextComponent.fromLegacyText(message(CHAT_ACTION_RENAME_WAYPOINT_PERMISSION, viewer));
					ClickEvent ce = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/waypoints rename permissionWaypoint " + waypoint.getID());
					Arrays.stream(components).forEach(component -> component.setClickEvent(ce));
					viewer.spigot().sendMessage(components);
					viewer.closeInventory();
				}));
			} else {
				waypointPattern.attach('r', bg);
			}
			if (viewer.hasPermission("waypoints.teleport.permission")) {
				waypointPattern.attach('t', ClickableItem.from(ItemStacks.getWaypointPermissionTeleportItem(viewer), click -> {
					viewer.closeInventory();
					viewer.teleport(waypoint.getLocation());
				}));
			} else {
				waypointPattern.attach('t', bg);
			}
			waypointPattern.attach('f', bg);
			waypointPattern.attach('b', ClickableItem.from(ItemStacks.getBackItem(viewer), click -> showLast()));
		} else if (waypoint instanceof DeathWaypoint) {
			ClickableItem bg = ClickableItem.empty(ItemStacks.getWaypointDeathBackgroundItem(viewer));
			waypointPattern.setDefault(bg);
			waypointPattern.attach('w', ClickableItem.empty(waypoint.getStack(viewer)));

			waypointPattern.attach('s', ClickableItem.from(ItemStacks.getWaypointDeathSelectItem(viewer), click -> {
				WaypointDisplay.getAll().show(viewer, waypoint);
				viewer.closeInventory();
			}));

			if (viewer.hasPermission("waypoints.teleport.death")) {
				viewer.closeInventory();
				viewer.teleport(waypoint.getLocation());
			} else {
				waypointPattern.attach('t', bg);
			}

			waypointPattern.attach('d', bg);
			waypointPattern.attach('f', bg);
			waypointPattern.attach('r', bg);
		}
		contents.fillPattern(waypointPattern);
	}

	private void showSelectFolder(Waypoint waypoint) {
		folderListPage = 0;
		contents.fill(ClickableItem.NONE);

		ClickableItem bg = ClickableItem.empty(ItemStacks.getSelectFolderBackgroundItem(viewer));

		selectFolderPattern.setDefault(bg);

		final Consumer<Folder> onClick = (folder) -> {
			if (folder == null) {
				targetData.moveWaypointToFolder(waypoint.getID(), null);
			} else {
				targetData.moveWaypointToFolder(waypoint.getID(), folder.getID());
			}
			showWaypoint(waypoint);
		};

		selectFolderPattern.attach('g', ClickableItem.from(ItemStacks.getSelectFolderNoFolderItem(viewer), click -> onClick.accept(null)));

		selectFolderPattern.attach('b', ClickableItem.from(ItemStacks.getBackItem(viewer), click -> showWaypoint(waypoint)));

		selectFolderPattern.attach('p', ClickableItem.from(ItemStacks.getPreviousItem(viewer), click -> {
			folderListPage = Math.max(0, folderListPage - 1);
			updateSelectFolder(onClick);
		}));
		selectFolderPattern.attach('n', ClickableItem.from(ItemStacks.getNextItem(viewer), click -> {
			folderListPage = Math.min(getSelectFolderPages(), folderListPage + 1);
			updateSelectFolder(onClick);
		}));

		contents.fillPattern(selectFolderPattern);

		updateSelectFolder(onClick);
	}

	private void showConfirm(Messages descriptionDisplayName, Messages descriptionDescription, Messages yesDisplayName, Messages yesDescription,
	                         Messages noDisplayName, Messages noDescription, Consumer<Boolean> result) {
		confirmPattern.setDefault(ClickableItem.empty(new ItemBuilder(inventory().getConfirmMenuBackgroundItem())
			.name(message(INVENTORY_CONFIRM_MENU_BACKGROUND_DISPLAY_NAME, viewer)).lore(message(INVENTORY_CONFIRM_MENU_BACKGROUND_DESCRIPTION, viewer)).make()));
		confirmPattern.attach('t', ClickableItem.empty(new ItemBuilder(inventory().getConfirmMenuDescriptionItem()).name(message(descriptionDisplayName, viewer))
			.lore(message(descriptionDescription, viewer)).make()));
		confirmPattern.attach('n', ClickableItem.from(new ItemBuilder(inventory().getConfirmMenuNoItem()).name(message(noDisplayName, viewer))
			.lore(message(noDescription, viewer)).make(), click -> result.accept(false)));
		confirmPattern.attach('y', ClickableItem.from(new ItemBuilder(inventory().getConfirmMenuYesItem()).name(message(yesDisplayName, viewer))
			.lore(message(yesDescription, viewer)).make(), click -> result.accept(true)));
		contents.fillPattern(confirmPattern);
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Cycle Sort mode helpers">
	private ItemStack getSortCycleItem(Material material) {
		ItemBuilder builder = new ItemBuilder(material).name(message(INVENTORY_CYCLE_SORT_DISPLAY_NAME, viewer))
			.lore(message(INVENTORY_CYCLE_SORT_DESCRIPTION, viewer));

		builder.appendLore("");

		String active = message(INVENTORY_CYCLE_SORT_ACTIVE, viewer), inactive = message(INVENTORY_CYCLE_SORT_INACTIVE, viewer);
		sortModeMap.forEach((sortMode, message) -> {
			if (viewerData.settings().sortMode().equals(sortMode)) {
				builder.appendLore(active.replace("%name%", message(message, viewer)));
			} else {
				builder.appendLore(inactive.replace("%name%", message(message, viewer)));
			}
		});

		return builder.make();
	}

	private void cycleSortMode() {
		int current = viewerData.settings().sortMode().ordinal();
		current += 1;
		current %= WPPlayerData.SortMode.values().length;
		viewerData.settings().sortMode(WPPlayerData.SortMode.values()[current]);
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Update items">
	private void updateOverview() {
		List<ClickableItem> items = getOverviewPageItems();
		for (int i = 0; i < pageSize; i++) {
			if (i < items.size()) {
				contents.set(i, items.get(i));
			} else {
				contents.set(i, ClickableItem.NONE);
			}
		}
	}

	private void updateFolder(Folder folder) {
		List<ClickableItem> items = getFolderPageItems(folder);
		for (int i = 0; i < pageSize; i++) {
			if (i < items.size()) {
				contents.set(i, items.get(i));
			} else {
				contents.set(i, ClickableItem.NONE);
			}
		}
	}

	private void updateSelectFolder(Consumer<Folder> onClick) {
		List<ClickableItem> items = getSelectFolderPageItems(onClick);
		for (int i = 0; i < pageSize; i++) {
			if (i < items.size()) {
				contents.set(i, items.get(i));
			} else {
				contents.set(i, ClickableItem.NONE);
			}
		}
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Pagination helpers">
	private int getOverviewPages() {
		int items = 0;
		if (targetData.settings().showGlobals() && isOwner) {
			if (!Waypoints.getGlobalStore().getPublicFolder().getWaypoints(viewer).isEmpty())
				items++;
			if (!Waypoints.getGlobalStore().getPermissionFolder().getWaypoints(viewer).isEmpty())
				items++;
		}
		items += targetData.getWaypoints().size() + targetData.getFolders().size();
		return PaginationList.pages(items, pageSize);
	}

	private int getFolderPages(Folder folder) {
		return PaginationList.pages(folder.getWaypoints(viewer).size(), pageSize);
	}

	private int getSelectFolderPages() {
		return PaginationList.pages(targetData.getFolders().size(), pageSize);
	}

	private List<ClickableItem> getOverviewPageItems() {
		PaginationList<GUISortable> items = new PaginationList<>(pageSize);
		if (isOwner) {
			if (targetData.settings().showGlobals()) {
				PermissionFolder permFolder = Waypoints.getGlobalStore().getPermissionFolder();
				if (!permFolder.getWaypoints(viewer).isEmpty())
					items.add(permFolder);

				PublicFolder pubFolder = Waypoints.getGlobalStore().getPublicFolder();
				if (!pubFolder.getWaypoints(viewer).isEmpty())
					items.add(Waypoints.getGlobalStore().getPublicFolder());
			}
			if (WPConfig.isDeathWaypointEnabled() && targetData.getDeathWaypoint() != null) {
				items.add(targetData.getDeathWaypoint());
			}
		}
		items.addAll(targetData.getFolders());
		items.addAll(targetData.getWaypoints());
		items.sort(viewerData.settings().sortMode().getComparator());
		return items.page(overviewPage).stream().map(guiSortable -> ClickableItem.from(guiSortable.getStack(viewer), click -> {
			switch (guiSortable.getType()) {
				case PERMISSION_FOLDER:
				case PUBLIC_FOLDER:
				case PRIVATE_FOLDER:
					folderPage = 0;
					showFolder((Folder) guiSortable);
					break;
				case WAYPOINT:
					showWaypoint((Waypoint) guiSortable);
					break;
			}
		})).collect(Collectors.toList());
	}

	private List<ClickableItem> getFolderPageItems(Folder folder) {
		PaginationList<GUISortable> items = new PaginationList<>(pageSize);
		items.addAll(folder.getWaypoints(viewer));
		items.sort(viewerData.settings().sortMode().getComparator());
		return items.page(folderPage).stream().map(guiSortable -> ClickableItem.from(guiSortable.getStack(viewer), click -> showWaypoint((Waypoint) guiSortable)))
			.collect(Collectors.toList());
	}

	private List<ClickableItem> getSelectFolderPageItems(Consumer<Folder> onClick) {
		PaginationList<GUISortable> items = new PaginationList<>(pageSize);
		items.addAll(targetData.getFolders());
		items.sort(viewerData.settings().sortMode().getComparator());
		return items.page(folderListPage).stream().map(guiSortable -> ClickableItem.from(guiSortable.getStack(viewer), click -> onClick.accept((Folder) guiSortable))).collect(Collectors.toList());
	}
	//</editor-fold>

	// TODO External update view

	@Override
	public void update(Player player, InventoryContents contents) {
	}
}
