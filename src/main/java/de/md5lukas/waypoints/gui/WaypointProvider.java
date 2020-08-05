/*
 *     Waypoints2, A plugin for spigot to add waypoints functionality
 *     Copyright (C) 2020  Lukas Planz
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
import de.md5lukas.commons.collections.LoopAroundList;
import de.md5lukas.commons.collections.PaginationList;
import de.md5lukas.commons.inventory.ItemBuilder;
import de.md5lukas.i18n.translations.ItemTranslation;
import de.md5lukas.i18n.translations.ItemTranslationTAR;
import de.md5lukas.i18n.translations.Translation;
import de.md5lukas.waypoints.Waypoints;
import de.md5lukas.waypoints.command.WaypointsCommand;
import de.md5lukas.waypoints.config.WPConfig;
import de.md5lukas.waypoints.config.general.TeleportWaypointConfig;
import de.md5lukas.waypoints.data.GUISortable;
import de.md5lukas.waypoints.data.WPPlayerData;
import de.md5lukas.waypoints.data.folder.Folder;
import de.md5lukas.waypoints.data.folder.PermissionFolder;
import de.md5lukas.waypoints.data.folder.PrivateFolder;
import de.md5lukas.waypoints.data.folder.PublicFolder;
import de.md5lukas.waypoints.data.waypoint.*;
import de.md5lukas.waypoints.display.BlockColor;
import de.md5lukas.waypoints.display.WaypointDisplay;
import de.md5lukas.waypoints.util.GeneralHelper;
import de.md5lukas.waypoints.util.TeleportManager;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInvsPlugin;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import fr.minuskube.inv.util.Pattern;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Consumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static de.md5lukas.waypoints.Waypoints.getITranslations;
import static de.md5lukas.waypoints.Waypoints.getTranslations;
import static de.md5lukas.waypoints.config.WPConfig.getInventoryConfig;

public class WaypointProvider implements InventoryProvider {

    private static final ClickableItem UNIQUE = ClickableItem.empty(null);
    private static final int pagedRows = 4;
    private static final int pageSize = pagedRows * 9;
    private static final ImmutableMap<WPPlayerData.SortMode, Translation> sortModeMap = ImmutableMap.of(
            WPPlayerData.SortMode.TYPE, getITranslations().CYCLE_SORT_MODE_TYPE,
            WPPlayerData.SortMode.NAME_ASC, getITranslations().CYCLE_SORT_MODE_NAME_ASC,
            WPPlayerData.SortMode.NAME_DESC, getITranslations().CYCLE_SORT_MODE_NAME_DESC,
            WPPlayerData.SortMode.CREATED_ASC, getITranslations().CYCLE_SORT_MODE_CREATED_ASC,
            WPPlayerData.SortMode.CREATED_DESC, getITranslations().CYCLE_SORT_MODE_CREATED_DESC);

    //<editor-fold defaultstate="collapsed" desc="Patterns">
    private static final Pattern<ClickableItem> overviewPattern = new Pattern<>(
            "#########",
            "#########",
            "#########",
            "#########",
            "pfs_d_tcn");
    /*
    # = none
    _ = background
    p = Previous
    f = create folder
    s = cycle sort
    d = deselect
    t = toggle global folders
    c = create waypoint
    n = next
     */
    private static final Pattern<ClickableItem> selectWaypointTypePattern = new Pattern<>(
            "_________",
            "____t____",
            "_________",
            "_u_____e_",
            "____r___b");
    /*
    _ = background
    t = title
    u = pUblic
    r = pRivate
    e = pErmission
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
            "____s___c",
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
    c = select beacon color
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
    private static final Pattern<ClickableItem> selectBeaconColorPattern = new Pattern<>(
            "_________",
            "_________",
            "p_#####_n",
            "_________",
            "________b");
    /*
    # = none
    _ = background
    b = back to waypoint
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

    private boolean initiated = false;
    private final UUID target;
    private final WPPlayerData targetData;
    private WPPlayerData viewerData;
    private boolean isOwner;
    private Player viewer;
    private InventoryContents contents;

    private Folder lastFolder = null;
    private Waypoint lastWaypoint = null;
    private int overviewPage = 0, folderPage = 0, folderListPage = 0;
    private LoopAroundList<BlockColor> beaconColorWheel;

    WaypointProvider(UUID target) {
        this.target = target;
        this.targetData = WPPlayerData.getPlayerData(target);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        this.contents = contents;
        if (initiated) {
            if (lastWaypoint != null)
                showWaypoint(lastWaypoint);
            else if (lastFolder != null)
                showFolder(lastFolder);
            else
                showOverview();
        } else {
            this.viewer = player;
            this.viewerData = WPPlayerData.getPlayerData(player.getUniqueId());
            this.isOwner = player.getUniqueId().equals(target);
            showOverview();
            initiated = true;
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Show overview/folder">
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
        lastWaypoint = null;

        ClickableItem bg = ClickableItem.empty(getITranslations().OVERVIEW_BACKGROUND.getStack(viewer));

        overviewPattern.setDefault(ClickableItem.NONE);
        overviewPattern.attach('_', bg);
        overviewPattern.attach('p', ClickableItem.from(getITranslations().GENERAL_PREVIOUS.getStack(viewer), click -> {
            overviewPage = Math.max(0, overviewPage - 1);
            updateOverview();
        }));
        overviewPattern.attach('n', ClickableItem.from(getITranslations().GENERAL_NEXT.getStack(viewer), click -> {
            overviewPage = Math.min(getOverviewPages() - 1, overviewPage + 1);
            updateOverview();
        }));
        overviewPattern
                .attach('d', ClickableItem.from(getITranslations().OVERVIEW_DESELECT.getStack(viewer), click -> WaypointDisplay.getAll().disable(viewer)));


        if (isOwner) {
            overviewPattern.attach('t', UNIQUE);
            SlotPos globalTogglePos = GeneralHelper.find(overviewPattern, ci -> ci == UNIQUE);
            final AtomicReference<Runnable> globalToggle = new AtomicReference<>();
            ClickableItem globalsShown = ClickableItem
                    .from(getITranslations().OVERVIEW_TOGGLE_GLOBALS_SHOWN.getStack(viewer), click -> globalToggle.get().run());
            ClickableItem globalsHidden = ClickableItem
                    .from(getITranslations().OVERVIEW_TOGGLE_GLOBALS_HIDDEN.getStack(viewer), click -> globalToggle.get().run());
            globalToggle.set(() -> {
                viewerData.settings().showGlobals(!viewerData.settings().showGlobals());
                if (viewerData.settings().showGlobals()) {
                    contents.set(globalTogglePos, globalsShown);
                } else {
                    contents.set(globalTogglePos, globalsHidden);
                }
                overviewPage = Math.min(getOverviewPages() - 1, overviewPage);
                updateOverview();
            });
            overviewPattern.attach('t', viewerData.settings().showGlobals() ? globalsShown : globalsHidden);
            if (viewer.hasPermission("waypoints.set.private") || viewer.hasPermission("waypoints.set.public") || viewer
                    .hasPermission("waypoints.set.permission")) {
                overviewPattern.attach('c', ClickableItem.from(getITranslations().OVERVIEW_SET_WAYPOINT.getStack(viewer), click -> {
                    if (viewer.hasPermission("waypoints.set.private") && !viewer.hasPermission("waypoints.set.public")
                            && !viewer.hasPermission("waypoints.set.permission")) {
                        closeInventory();
                        new AnvilGUI.Builder().plugin(Waypoints.instance()).text(getITranslations().ANVIL_GUI_ENTER_NAME_HERE.getAsString(viewer))
                                .onComplete((player, text) -> {
                                    WaypointsCommand.setPrivateWaypoint(viewer, text);
                                    return AnvilGUI.Response.close();
                                }).open(viewer);
                    } else {
                        showSelectWaypointType();
                    }
                }));
            } else {
                overviewPattern.attach('c', bg);
            }
            overviewPattern.attach('f', ClickableItem.from(getITranslations().OVERVIEW_CREATE_FOLDER.getStack(viewer), click -> {
                closeInventory();
                new AnvilGUI.Builder().plugin(Waypoints.instance()).text(getITranslations().ANVIL_GUI_ENTER_NAME_HERE.getAsString(viewer))
                        .onComplete((player, text) -> {
                            WaypointsCommand.createFolder(viewer, text);
                            return AnvilGUI.Response.close();
                        }).open(viewer);
            }));
        } else {
            overviewPattern.attach('t', bg);
            overviewPattern.attach('c', bg);
        }

        overviewPattern.attach('s', UNIQUE);
        SlotPos sortCyclePos = GeneralHelper.find(overviewPattern, ci -> ci == UNIQUE);
        final AtomicReference<Runnable> cycleUpdate = new AtomicReference<>();
        cycleUpdate.set(() -> contents
                .set(sortCyclePos, ClickableItem.from(getSortCycleItem(getInventoryConfig().getDefaultOverviewMenuConfig().getCycleSortItem()), click -> {
                    cycleSortMode();
                    updateOverview();
                    cycleUpdate.get().run();
                })));
        overviewPattern.attach('s', ClickableItem.from(getSortCycleItem(getInventoryConfig().getDefaultOverviewMenuConfig().getCycleSortItem()), click -> {
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
        lastWaypoint = null;
        folderPattern.setDefault(ClickableItem.NONE);
        ClickableItem bg = null;

        if (folder instanceof PrivateFolder)
            bg = ClickableItem.empty(getITranslations().FOLDER_PRIVATE_BACKGROUND.getStack(viewer));
        else if (folder instanceof PublicFolder)
            bg = ClickableItem.empty(getITranslations().FOLDER_PUBLIC_BACKGROUND.getStack(viewer));
        else if (folder instanceof PermissionFolder)
            bg = ClickableItem.empty(getITranslations().FOLDER_PERMISSION_BACKGROUND.getStack(viewer));

        folderPattern.attach('_', bg);
        folderPattern.attach('b', ClickableItem.from(getITranslations().GENERAL_BACK.getStack(viewer), click -> showOverview()));
        folderPattern.attach('p', ClickableItem.from(getITranslations().GENERAL_PREVIOUS.getStack(viewer), click -> {
            folderPage = Math.max(0, folderPage - 1);
            updateFolder(folder);
        }));
        folderPattern.attach('n', ClickableItem.from(getITranslations().GENERAL_NEXT.getStack(viewer), click -> {
            folderPage = Math.min(getFolderPages(folder) - 1, folderPage + 1);
            updateFolder(folder);
        }));

        if (isOwner && folder instanceof PrivateFolder) {
            folderPattern.attach('d', ClickableItem.from(getITranslations().FOLDER_PRIVATE_DELETE.getStack(viewer),
                    click -> showConfirm(getITranslations().CONFIRM_MENU_FOLDER_PRIVATE_DELETE_DESCRIPTION,
                            getITranslations().CONFIRM_MENU_FOLDER_PRIVATE_DELETE_YES,
                            getITranslations().CONFIRM_MENU_FOLDER_PRIVATE_DELETE_NO,
                            result -> {
                                if (result) {
                                    targetData.removeFolder(folder.getID());
                                }
                                showOverview();
                            })));
            folderPattern.attach('f', ClickableItem.from(folder.getStack(viewer), click -> {
                BaseComponent[] components = TextComponent.fromLegacyText(getTranslations().CHAT_ACTION_UPDATE_ITEM_FOLDER_PRIVATE.getAsString(viewer));
                ClickEvent ce = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/waypoints updateItem folder " + folder.getID());
                for (BaseComponent component : components) {
                    component.setClickEvent(ce);
                }
                viewer.spigot().sendMessage(components);
                viewer.closeInventory();
            }));
            folderPattern.attach('r', ClickableItem.from(getITranslations().FOLDER_PRIVATE_RENAME.getStack(viewer), click -> {
                closeInventory();
                new AnvilGUI.Builder().plugin(Waypoints.instance()).text(folder.getName())
                        .onComplete((player, text) -> {
                            folder.setName(text);
                            return AnvilGUI.Response.close();
                        }).onClose(player -> GUIManager.openGUI(viewer, target, this)).open(viewer);
            }));
        } else {
            folderPattern.attach('d', bg);
            folderPattern.attach('f', ClickableItem.empty(folder.getStack(viewer)));
            folderPattern.attach('r', bg);
        }
        contents.fillPattern(folderPattern);

        updateFolder(folder);
    }

    private void showConfirm(ItemTranslation description, ItemTranslation yes,
            ItemTranslation no, Consumer<Boolean> result) {
        confirmPattern.setDefault(ClickableItem.empty(getITranslations().CONFIRM_MENU_BACKGROUND.getStack(viewer)));
        confirmPattern.attach('t', ClickableItem
                .empty(description.getStack(viewer)));
        confirmPattern.attach('n', ClickableItem.from(no.getStack(viewer), click -> result.accept(false)));
        confirmPattern.attach('y', ClickableItem.from(yes.getStack(viewer), click -> result.accept(true)));
        contents.fillPattern(confirmPattern);
    }

    private void showSelectWaypointType() {
        ClickableItem bg = ClickableItem.empty(getITranslations().SELECT_WAYPOINT_TYPE_BACKGROUND.getStack(viewer));
        selectWaypointTypePattern.setDefault(bg);

        selectWaypointTypePattern.attach('t', ClickableItem.empty(getITranslations().SELECT_WAYPOINT_TYPE_TITLE.getStack(viewer)));

        if (viewer.hasPermission("waypoints.set.private")) {
            selectWaypointTypePattern.attach('r', ClickableItem.from(getITranslations().SELECT_WAYPOINT_TYPE_PRIVATE.getStack(viewer), click -> {
                closeInventory();
                new AnvilGUI.Builder().plugin(Waypoints.instance()).text(getITranslations().ANVIL_GUI_ENTER_NAME_HERE.getAsString(viewer))
                        .onComplete((player, text) -> {
                            WaypointsCommand.setPrivateWaypoint(viewer, text);
                            return AnvilGUI.Response.close();
                        }).open(viewer);
            }));
        } else {
            selectWaypointTypePattern.attach('r', bg);
        }

        if (viewer.hasPermission("waypoints.set.public")) {
            selectWaypointTypePattern.attach('u', ClickableItem.from(getITranslations().SELECT_WAYPOINT_TYPE_PUBLIC.getStack(viewer), click -> {
                closeInventory();
                new AnvilGUI.Builder().plugin(Waypoints.instance()).text(getITranslations().ANVIL_GUI_ENTER_NAME_HERE.getAsString(viewer))
                        .onComplete((player, text) -> {
                            WaypointsCommand.setPublicWaypoint(viewer, text);
                            return AnvilGUI.Response.close();
                        }).open(viewer);
            }));
        } else {
            selectWaypointTypePattern.attach('u', bg);
        }

        if (viewer.hasPermission("waypoints.set.permissions")) {
            selectWaypointTypePattern.attach('e', ClickableItem.from(getITranslations().SELECT_WAYPOINT_TYPE_PERMISSION.getStack(viewer), click -> {
                closeInventory();
                final AtomicReference<String> permission = new AtomicReference<>();
                new AnvilGUI.Builder().plugin(Waypoints.instance()).text(getITranslations().ANVIL_GUI_ENTER_NAME_HERE.getAsString(viewer))
                        .onComplete((player, text) -> {
                            if (permission.getAndSet(text) == null) {
                                return AnvilGUI.Response.text(getITranslations().ANVIL_GUI_ENTER_PERMISSION_HERE.getAsString(viewer));
                            }
                            WaypointsCommand.setPermissionWaypoint(viewer, permission.get(), text);
                            return AnvilGUI.Response.close();
                        }).open(viewer);
            }));
        } else {
            selectWaypointTypePattern.attach('e', bg);
        }

        selectWaypointTypePattern.attach('b', ClickableItem.from(getITranslations().GENERAL_BACK.getStack(viewer), click -> showLast()));

        contents.fillPattern(selectWaypointTypePattern);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Show waypoints">
    private void showWaypoint(Waypoint waypoint) {
        lastWaypoint = waypoint;
        contents.fill(ClickableItem.NONE);
        if (waypoint instanceof PrivateWaypoint) {
            showPrivateWaypoint(waypoint);
        } else if (waypoint instanceof PublicWaypoint) {
            showPublicWaypoint(waypoint);
        } else if (waypoint instanceof PermissionWaypoint) {
            showPermissionWaypoint(waypoint);
        } else if (waypoint instanceof DeathWaypoint) {
            showDeathWaypoint(waypoint);
        }
        contents.fillPattern(waypointPattern);
    }

    private void showPrivateWaypoint(Waypoint waypoint) {
        ClickableItem bg = ClickableItem.empty(getITranslations().WAYPOINT_PRIVATE_BACKGROUND.getStack(viewer));
        waypointPattern.setDefault(bg);
        if (isOwner) {
            waypointPattern.attach('w', ClickableItem.from(waypoint.getStack(viewer), click -> {
                BaseComponent[] components = TextComponent.fromLegacyText(getTranslations().CHAT_ACTION_UPDATE_ITEM_WAYPOINT_PRIVATE.getAsString(viewer));
                ClickEvent ce = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/waypoints updateItem waypointPrivate " + waypoint.getID());
                for (BaseComponent component : components) {
                    component.setClickEvent(ce);
                }
                viewer.spigot().sendMessage(components);
                viewer.closeInventory();
            }));
        } else {
            waypointPattern.attach('w', ClickableItem.empty(waypoint.getStack(viewer)));
        }
        waypointPattern.attach('s', ClickableItem.from(getITranslations().WAYPOINT_PRIVATE_SELECT.getStack(viewer), click -> {
            WaypointDisplay.getAll().show(viewer, waypoint);
            viewer.closeInventory();
        }));
        if (isOwner || viewer.hasPermission("waypoints.delete.other")) {
            waypointPattern.attach('d', ClickableItem.from(getITranslations().WAYPOINT_PRIVATE_DELETE.getStack(viewer),
                    click -> showConfirm(getITranslations().CONFIRM_MENU_FOLDER_PRIVATE_DELETE_DESCRIPTION,
                            getITranslations().CONFIRM_MENU_FOLDER_PRIVATE_DELETE_YES,
                            getITranslations().CONFIRM_MENU_FOLDER_PRIVATE_DELETE_NO,
                            result -> {
                                if (result) {
                                    targetData.removeWaypoint(waypoint.getID());
                                    showLast();
                                } else {
                                    showWaypoint(waypoint);
                                }
                            })));
        } else {
            waypointPattern.attach('d', bg);
        }
        if (isOwner && WPConfig.getGeneralConfig().getRenamingConfig().isAllowRenamingWaypointsPrivate()) {
            waypointPattern.attach('r', ClickableItem.from(getITranslations().WAYPOINT_PRIVATE_RENAME.getStack(viewer), click -> {
                closeInventory();
                new AnvilGUI.Builder().plugin(Waypoints.instance()).text(waypoint.getName())
                        .onComplete((player, text) -> {
                            waypoint.setName(text);
                            return AnvilGUI.Response.close();
                        }).onClose(player -> GUIManager.openGUI(viewer, target, this)).open(viewer);
            }));
        } else {
            waypointPattern.attach('r', bg);
        }
        if (isOwner && WPConfig.getDisplayConfig().getBeaconConfig().isEnabled() && WPConfig.getDisplayConfig().getBeaconConfig().isEnableSelectColor()) {
            waypointPattern.attach('c',
                    ClickableItem.from(getITranslations().WAYPOINT_PRIVATE_SELECT_BEACON_COLOR.getStack(viewer), click -> showSelectBeaconColor(waypoint)));
        } else {
            waypointPattern.attach('c', bg);
        }
        if (viewer.hasPermission("waypoints.teleport.private")) {
            waypointPattern.attach('t', ClickableItem.from(getITranslations().WAYPOINT_PRIVATE_TELEPORT.getStack(viewer), click -> {
                viewer.closeInventory();
                TeleportManager.teleportKeepOrientation(viewer, waypoint.getLocation());
            }));
        } else if (TeleportWaypointConfig.TeleportEnabled.FREE.equals(waypoint.getTeleportSettings().getEnabled()) && isOwner) {
            waypointPattern.attach('t', ClickableItem.from(getITranslations().WAYPOINT_PRIVATE_TELEPORT.getStack(viewer),
                    click -> TeleportManager.teleportPlayerToWaypoint(viewer, viewerData, waypoint)));
        } else if (TeleportWaypointConfig.TeleportEnabled.PAY.equals(waypoint.getTeleportSettings().getEnabled()) && isOwner) {
            long cost = waypoint.getTeleportSettings().calculateCost(waypoint.getTeleportations());
            ItemStack stack;
            if (TeleportWaypointConfig.TeleportPaymentMethod.XP_POINTS.equals(waypoint.getTeleportSettings().getPaymentMethod())) {
                stack = getITranslations().WAYPOINT_PRIVATE_TELEPORT_XP_POINTS
                        .getStack(viewer, new ItemTranslationTAR().setDescription("%points%", Long.toString(cost)));
            } else if (TeleportWaypointConfig.TeleportPaymentMethod.XP_LEVELS.equals(waypoint.getTeleportSettings().getPaymentMethod())) {
                stack = getITranslations().WAYPOINT_PRIVATE_TELEPORT_XP_LEVELS
                        .getStack(viewer, new ItemTranslationTAR().setDescription("%levels%", Long.toString(cost)));
            } else {
                stack = getITranslations().WAYPOINT_PRIVATE_TELEPORT_VAULT
                        .getStack(viewer, new ItemTranslationTAR().setDescription("%money%", Long.toString(cost)));
            }
            waypointPattern.attach('t', ClickableItem.from(stack, click -> TeleportManager.teleportPlayerToWaypoint(viewer, viewerData, waypoint)));
        } else {
            waypointPattern.attach('t', bg);
        }
        waypointPattern.attach('b', ClickableItem.from(getITranslations().GENERAL_BACK.getStack(viewer), click -> showLast()));
        if (isOwner) {
            waypointPattern
                    .attach('f', ClickableItem.from(getITranslations().WAYPOINT_PRIVATE_MOVE_TO_FOLDER.getStack(viewer), click -> showSelectFolder(waypoint)));
        } else {
            waypointPattern.attach('f', bg);
        }
    }

    private void showPublicWaypoint(Waypoint waypoint) {
        ClickableItem bg = ClickableItem.empty(getITranslations().WAYPOINT_PUBLIC_BACKGROUND.getStack(viewer));
        waypointPattern.setDefault(bg);
        if (viewer.hasPermission("waypoints.updateDisplayItem.public")) {
            waypointPattern.attach('w', ClickableItem.from(waypoint.getStack(viewer), click -> {
                BaseComponent[] components = TextComponent.fromLegacyText(getTranslations().CHAT_ACTION_UPDATE_ITEM_WAYPOINT_PUBLIC.getAsString(viewer));
                ClickEvent ce = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/waypoints updateItem waypointPublic " + waypoint.getID());
                for (BaseComponent component : components) {
                    component.setClickEvent(ce);
                }
                viewer.spigot().sendMessage(components);
                viewer.closeInventory();
            }));
        } else {
            waypointPattern.attach('w', ClickableItem.empty(waypoint.getStack(viewer)));
        }
        waypointPattern.attach('s', ClickableItem.from(getITranslations().WAYPOINT_PUBLIC_SELECT.getStack(viewer), click -> {
            WaypointDisplay.getAll().show(viewer, waypoint);
            viewer.closeInventory();
        }));
        if (viewer.hasPermission("waypoints.delete.public")) {
            waypointPattern.attach('d', ClickableItem.from(getITranslations().WAYPOINT_PUBLIC_DELETE.getStack(viewer),
                    click -> showConfirm(getITranslations().CONFIRM_MENU_WAYPOINT_PUBLIC_DELETE_DESCRIPTION,
                            getITranslations().CONFIRM_MENU_WAYPOINT_PUBLIC_DELETE_YES,
                            getITranslations().CONFIRM_MENU_WAYPOINT_PUBLIC_DELETE_NO,
                            result -> {
                                if (result) {
                                    Waypoints.getGlobalStore().getPublicFolder().removeWaypoint(waypoint.getID());
                                }
                                showLast();
                            })));
        } else {
            waypointPattern.attach('d', bg);
        }
        if (viewer.hasPermission("waypoints.rename.public") && WPConfig.getGeneralConfig().getRenamingConfig().isAllowRenamingWaypointsPublic()) {
            waypointPattern.attach('r', ClickableItem.from(getITranslations().WAYPOINT_PUBLIC_RENAME.getStack(viewer), click -> {
                closeInventory();
                new AnvilGUI.Builder().plugin(Waypoints.instance()).text(waypoint.getName())
                        .onComplete((player, text) -> {
                            waypoint.setName(text);
                            return AnvilGUI.Response.close();
                        }).onClose(player -> GUIManager.openGUI(viewer, target, this)).open(viewer);
            }));
        } else {
            waypointPattern.attach('r', bg);
        }
        if (viewer.hasPermission("waypoints.changeBeaconColor.public") && WPConfig.getDisplayConfig().getBeaconConfig().isEnabled() && WPConfig
                .getDisplayConfig().getBeaconConfig().isEnableSelectColor()) {
            waypointPattern.attach('c',
                    ClickableItem.from(getITranslations().WAYPOINT_PUBLIC_SELECT_BEACON_COLOR.getStack(viewer), click -> showSelectBeaconColor(waypoint)));
        } else {
            waypointPattern.attach('c', bg);
        }
        if (viewer.hasPermission("waypoints.teleport.public")) {
            waypointPattern.attach('t', ClickableItem.from(getITranslations().WAYPOINT_PUBLIC_TELEPORT.getStack(viewer), click -> {
                viewer.closeInventory();
                TeleportManager.teleportKeepOrientation(viewer, waypoint.getLocation());
            }));
        } else if (TeleportWaypointConfig.TeleportEnabled.FREE.equals(waypoint.getTeleportSettings().getEnabled()) && isOwner) {
            waypointPattern.attach('t', ClickableItem.from(getITranslations().WAYPOINT_PUBLIC_TELEPORT.getStack(viewer),
                    click -> TeleportManager.teleportPlayerToWaypoint(viewer, viewerData, waypoint)));
        } else if (TeleportWaypointConfig.TeleportEnabled.PAY.equals(waypoint.getTeleportSettings().getEnabled()) && isOwner) {
            long cost = waypoint.getTeleportSettings().calculateCost(waypoint.getTeleportations());
            ItemStack stack;
            if (TeleportWaypointConfig.TeleportPaymentMethod.XP_POINTS.equals(waypoint.getTeleportSettings().getPaymentMethod())) {
                stack = getITranslations().WAYPOINT_PUBLIC_TELEPORT_XP_POINTS
                        .getStack(viewer, new ItemTranslationTAR().setDescription("%points%", Long.toString(cost)));
            } else if (TeleportWaypointConfig.TeleportPaymentMethod.XP_LEVELS.equals(waypoint.getTeleportSettings().getPaymentMethod())) {
                stack = getITranslations().WAYPOINT_PUBLIC_TELEPORT_XP_LEVELS
                        .getStack(viewer, new ItemTranslationTAR().setDescription("%levels%", Long.toString(cost)));
            } else {
                stack = getITranslations().WAYPOINT_PUBLIC_TELEPORT_VAULT
                        .getStack(viewer, new ItemTranslationTAR().setDescription("%money%", Long.toString(cost)));
            }
            waypointPattern.attach('t', ClickableItem.from(stack, click -> TeleportManager.teleportPlayerToWaypoint(viewer, viewerData, waypoint)));
        } else {
            waypointPattern.attach('t', bg);
        }
        waypointPattern.attach('f', bg);
        waypointPattern.attach('b', ClickableItem.from(getITranslations().GENERAL_BACK.getStack(viewer), click -> showLast()));
    }

    private void showPermissionWaypoint(Waypoint waypoint) {
        ClickableItem bg = ClickableItem.empty(getITranslations().WAYPOINT_PERMISSION_BACKGROUND.getStack(viewer));
        waypointPattern.setDefault(bg);
        if (viewer.hasPermission("waypoints.updateDisplayItem.permission")) {
            waypointPattern.attach('w', ClickableItem.from(waypoint.getStack(viewer), click -> {
                BaseComponent[] components = TextComponent.fromLegacyText(getTranslations().CHAT_ACTION_UPDATE_ITEM_WAYPOINT_PERMISSION.getAsString(viewer));
                ClickEvent ce = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/waypoints updateItem waypointPermission " + waypoint.getID());
                for (BaseComponent component : components) {
                    component.setClickEvent(ce);
                }
                viewer.spigot().sendMessage(components);
                viewer.closeInventory();
            }));
        } else {
            waypointPattern.attach('w', ClickableItem.empty(waypoint.getStack(viewer)));
        }
        waypointPattern.attach('s', ClickableItem.from(getITranslations().WAYPOINT_PERMISSION_SELECT.getStack(viewer), click -> {
            WaypointDisplay.getAll().show(viewer, waypoint);
            viewer.closeInventory();
        }));
        if (viewer.hasPermission("waypoints.delete.permission")) {
            waypointPattern.attach('d', ClickableItem.from(getITranslations().WAYPOINT_PERMISSION_DELETE.getStack(viewer), click -> {
                Waypoints.getGlobalStore().getPermissionFolder().removeWaypoint(waypoint.getID());
                showConfirm(getITranslations().CONFIRM_MENU_WAYPOINT_PERMISSION_DELETE_DESCRIPTION,
                        getITranslations().CONFIRM_MENU_WAYPOINT_PERMISSION_DELETE_YES,
                        getITranslations().CONFIRM_MENU_WAYPOINT_PERMISSION_DELETE_NO,
                        result -> {
                            if (result) {
                                Waypoints.getGlobalStore().getPermissionFolder().removeWaypoint(waypoint.getID());
                            }
                            showLast();
                        });
            }));
        } else {
            waypointPattern.attach('d', bg);
        }
        if (viewer.hasPermission("waypoints.rename.permission") && WPConfig.getGeneralConfig().getRenamingConfig().isAllowRenamingWaypointsPermission()) {
            waypointPattern.attach('r', ClickableItem.from(getITranslations().WAYPOINT_PERMISSION_RENAME.getStack(viewer), click -> {
                closeInventory();
                new AnvilGUI.Builder().plugin(Waypoints.instance()).text(waypoint.getName())
                        .onComplete((player, text) -> {
                            waypoint.setName(text);
                            return AnvilGUI.Response.close();
                        }).onClose(player -> GUIManager.openGUI(viewer, target, this)).open(viewer);
            }));
        } else {
            waypointPattern.attach('r', bg);
        }
        if (viewer.hasPermission("waypoints.changeBeaconColor.permission") && WPConfig.getDisplayConfig().getBeaconConfig().isEnabled() && WPConfig
                .getDisplayConfig().getBeaconConfig().isEnableSelectColor()) {
            waypointPattern.attach('c',
                    ClickableItem.from(getITranslations().WAYPOINT_PERMISSION_SELECT_BEACON_COLOR.getStack(viewer), click -> showSelectBeaconColor(waypoint)));
        } else {
            waypointPattern.attach('c', bg);
        }
        if (viewer.hasPermission("waypoints.teleport.permission")) {
            waypointPattern.attach('t', ClickableItem.from(getITranslations().WAYPOINT_PERMISSION_TELEPORT.getStack(viewer), click -> {
                viewer.closeInventory();
                TeleportManager.teleportKeepOrientation(viewer, waypoint.getLocation());
            }));
        } else if (TeleportWaypointConfig.TeleportEnabled.FREE.equals(waypoint.getTeleportSettings().getEnabled()) && isOwner) {
            waypointPattern.attach('t', ClickableItem.from(getITranslations().WAYPOINT_PERMISSION_TELEPORT.getStack(viewer),
                    click -> TeleportManager.teleportPlayerToWaypoint(viewer, viewerData, waypoint)));
        } else if (TeleportWaypointConfig.TeleportEnabled.PAY.equals(waypoint.getTeleportSettings().getEnabled()) && isOwner) {
            long cost = waypoint.getTeleportSettings().calculateCost(waypoint.getTeleportations());
            ItemStack stack;
            if (TeleportWaypointConfig.TeleportPaymentMethod.XP_POINTS.equals(waypoint.getTeleportSettings().getPaymentMethod())) {
                stack = getITranslations().WAYPOINT_PERMISSION_TELEPORT_XP_POINTS
                        .getStack(viewer, new ItemTranslationTAR().setDescription("%points%", Long.toString(cost)));
            } else if (TeleportWaypointConfig.TeleportPaymentMethod.XP_LEVELS.equals(waypoint.getTeleportSettings().getPaymentMethod())) {
                stack = getITranslations().WAYPOINT_PERMISSION_TELEPORT_XP_LEVELS
                        .getStack(viewer, new ItemTranslationTAR().setDescription("%levels%", Long.toString(cost)));
            } else {
                stack = getITranslations().WAYPOINT_PERMISSION_TELEPORT_VAULT
                        .getStack(viewer, new ItemTranslationTAR().setDescription("%money%", Long.toString(cost)));
            }
            waypointPattern.attach('t', ClickableItem.from(stack, click -> TeleportManager.teleportPlayerToWaypoint(viewer, viewerData, waypoint)));
        } else {
            waypointPattern.attach('t', bg);
        }
        waypointPattern.attach('f', bg);
        waypointPattern.attach('b', ClickableItem.from(getITranslations().GENERAL_BACK.getStack(viewer), click -> showLast()));
    }

    private void showDeathWaypoint(Waypoint waypoint) {
        ClickableItem bg = ClickableItem.empty(getITranslations().WAYPOINT_DEATH_BACKGROUND.getStack(viewer));
        waypointPattern.setDefault(bg);
        waypointPattern.attach('w', ClickableItem.empty(waypoint.getStack(viewer)));

        waypointPattern.attach('s', ClickableItem.from(getITranslations().WAYPOINT_DEATH_SELECT.getStack(viewer), click -> {
            WaypointDisplay.getAll().show(viewer, waypoint);
            viewer.closeInventory();
        }));

        if (viewer.hasPermission("waypoints.teleport.death")) {
            waypointPattern.attach('t', ClickableItem.from(getITranslations().WAYPOINT_DEATH_TELEPORT.getStack(viewer), click -> {
                viewer.closeInventory();
                viewer.teleport(waypoint.getLocation());
            }));
        } else if (TeleportWaypointConfig.TeleportEnabled.FREE.equals(waypoint.getTeleportSettings().getEnabled()) && isOwner) {
            waypointPattern.attach('t', ClickableItem.from(getITranslations().WAYPOINT_DEATH_TELEPORT.getStack(viewer),
                    click -> TeleportManager.teleportKeepOrientation(viewer, waypoint.getLocation())));
        } else if (TeleportWaypointConfig.TeleportEnabled.PAY.equals(waypoint.getTeleportSettings().getEnabled()) && isOwner) {
            long cost = waypoint.getTeleportSettings().calculateCost(waypoint.getTeleportations());
            ItemStack stack;
            if (TeleportWaypointConfig.TeleportPaymentMethod.XP_POINTS.equals(waypoint.getTeleportSettings().getPaymentMethod())) {
                stack = getITranslations().WAYPOINT_DEATH_TELEPORT_XP_POINTS
                        .getStack(viewer, new ItemTranslationTAR().setDescription("%points%", Long.toString(cost)));
            } else if (TeleportWaypointConfig.TeleportPaymentMethod.XP_LEVELS.equals(waypoint.getTeleportSettings().getPaymentMethod())) {
                stack = getITranslations().WAYPOINT_DEATH_TELEPORT_XP_LEVELS
                        .getStack(viewer, new ItemTranslationTAR().setDescription("%levels%", Long.toString(cost)));
            } else {
                stack = getITranslations().WAYPOINT_DEATH_TELEPORT_VAULT
                        .getStack(viewer, new ItemTranslationTAR().setDescription("%money%", Long.toString(cost)));
            }
            waypointPattern.attach('t', ClickableItem.from(stack, click -> TeleportManager.teleportPlayerToWaypoint(viewer, viewerData, waypoint)));
        } else {
            waypointPattern.attach('t', bg);
        }

        waypointPattern.attach('b', ClickableItem.from(getITranslations().GENERAL_BACK.getStack(viewer), click -> showLast()));

        waypointPattern.attach('d', bg);
        waypointPattern.attach('f', bg);
        waypointPattern.attach('r', bg);
    }

    private void showSelectFolder(Waypoint waypoint) {
        folderListPage = 0;
        contents.fill(ClickableItem.NONE);

        ClickableItem bg = ClickableItem.empty(getITranslations().SELECT_FOLDER_BACKGROUND.getStack(viewer));

        selectFolderPattern.setDefault(bg);

        final Consumer<Folder> onClick = (folder) -> {
            if (folder == null) {
                targetData.moveWaypointToFolder(waypoint.getID(), null);
            } else {
                targetData.moveWaypointToFolder(waypoint.getID(), folder.getID());
            }
            showWaypoint(waypoint);
        };

        selectFolderPattern.attach('g', ClickableItem.from(getITranslations().SELECT_FOLDER_NO_FOLDER.getStack(viewer), click -> onClick.accept(null)));

        selectFolderPattern.attach('b', ClickableItem.from(getITranslations().GENERAL_BACK.getStack(viewer), click -> showWaypoint(waypoint)));

        selectFolderPattern.attach('p', ClickableItem.from(getITranslations().GENERAL_PREVIOUS.getStack(viewer), click -> {
            folderListPage = Math.max(0, folderListPage - 1);
            updateSelectFolder(onClick);
        }));
        selectFolderPattern.attach('n', ClickableItem.from(getITranslations().GENERAL_NEXT.getStack(viewer), click -> {
            folderListPage = Math.min(getSelectFolderPages(), folderListPage + 1);
            updateSelectFolder(onClick);
        }));

        contents.fillPattern(selectFolderPattern);

        updateSelectFolder(onClick);
    }

    private void showSelectBeaconColor(Waypoint waypoint) {
        if (beaconColorWheel != null)
            beaconColorWheel.setIndex(0);
        Consumer<BlockColor> bcConsumer = bc -> {
            waypoint.setBeaconColor(bc);
            WaypointDisplay.getAll().show(viewer, waypoint);
            showWaypoint(waypoint);
        };
        contents.fill(ClickableItem.NONE);
        selectBeaconColorPattern.setDefault(ClickableItem.empty(getITranslations().SELECT_BEACON_COLOR_BACKGROUND.getStack(viewer)));
        selectBeaconColorPattern.attach('#', ClickableItem.NONE);
        selectBeaconColorPattern.attach('p', ClickableItem.from(getITranslations().GENERAL_PREVIOUS.getStack(viewer), click -> {
            beaconColorWheel.next();
            updateSelectBeaconColor(bcConsumer);
        }));
        selectBeaconColorPattern.attach('n', ClickableItem.from(getITranslations().GENERAL_NEXT.getStack(viewer), click -> {
            beaconColorWheel.previous();
            updateSelectBeaconColor(bcConsumer);
        }));
        selectBeaconColorPattern.attach('b', ClickableItem.from(getITranslations().GENERAL_BACK.getStack(viewer), click -> showWaypoint(waypoint)));
        contents.fillPattern(selectBeaconColorPattern);
        updateSelectBeaconColor(bcConsumer);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Cycle Sort mode helpers">
    private ItemStack getSortCycleItem(Material material) {
        ItemBuilder builder = new ItemBuilder(getITranslations().CYCLE_SORT.getStack(viewer));

        builder.appendLore("");

        String active = getITranslations().CYCLE_SORT_ACTIVE.getAsString(viewer), inactive = getITranslations().CYCLE_SORT_INACTIVE.getAsString(viewer);
        sortModeMap.forEach((sortMode, translation) -> {
            if (viewerData.settings().sortMode().equals(sortMode)) {
                builder.appendLore(active.replace("%name%", translation.getAsString(viewer)));
            } else {
                builder.appendLore(inactive.replace("%name%", translation.getAsString(viewer)));
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

    private void updateSelectBeaconColor(Consumer<BlockColor> onClick) {
        List<ClickableItem> items = getSelectBeaconColorItems(onClick);
        for (int i = 0; i < 5; i++) {
            contents.set(2, 2 + i, items.get(i));
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Pagination helpers">
    private int getOverviewPages() {
        int items = 0;
        if (isOwner) {
            if (targetData.settings().showGlobals()) {
                if (!Waypoints.getGlobalStore().getPublicFolder().getWaypoints(viewer).isEmpty())
                    items++;
                if (!Waypoints.getGlobalStore().getPermissionFolder().getWaypoints(viewer).isEmpty())
                    items++;
            }
            if (WPConfig.getGeneralConfig().isDeathWaypointEnabled() && targetData.getDeathWaypoint() != null)
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
            if (WPConfig.getGeneralConfig().isDeathWaypointEnabled() && targetData.getDeathWaypoint() != null) {
                items.add(targetData.getDeathWaypoint());
            }
        }
        items.addAll(targetData.getFolders());
        items.addAll(targetData.getWaypoints());
        items.sort(viewerData.settings().sortMode().getComparator());
        return items
                .page(overviewPage)
                .stream()
                .map(guiSortable ->
                        ClickableItem.from(
                                guiSortable.getStack(viewer)
                                , click -> {
                                    switch (guiSortable.getType()) {
                                        case PERMISSION_FOLDER:
                                        case PUBLIC_FOLDER:
                                        case PRIVATE_FOLDER:
                                            folderPage = 0;
                                            showFolder((Folder) guiSortable);
                                            break;
                                        case DEATH_WAYPOINT:
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
        return items.page(folderPage).stream()
                .map(guiSortable -> ClickableItem.from(guiSortable.getStack(viewer), click -> showWaypoint((Waypoint) guiSortable)))
                .collect(Collectors.toList());
    }

    private List<ClickableItem> getSelectFolderPageItems(Consumer<Folder> onClick) {
        PaginationList<GUISortable> items = new PaginationList<>(pageSize);
        items.addAll(targetData.getFolders());
        items.sort(viewerData.settings().sortMode().getComparator());
        return items.page(folderListPage).stream()
                .map(guiSortable -> ClickableItem.from(guiSortable.getStack(viewer), click -> onClick.accept((Folder) guiSortable)))
                .collect(Collectors.toList());
    }

    private List<ClickableItem> getSelectBeaconColorItems(Consumer<BlockColor> onClick) {
        if (beaconColorWheel == null) {
            beaconColorWheel = new LoopAroundList<>(5);
            beaconColorWheel.addAll(Arrays.asList(BlockColor.values()));
        }
        List<ClickableItem> result = new ArrayList<>(5);
        for (BlockColor bc : beaconColorWheel.getCutOut()) {
            result.add(ClickableItem.from(bc.getItemTranslation().getStack(viewer), click -> onClick.accept(bc)));
        }
        return result;
    }
    //</editor-fold>

    private void closeInventory() {
        SmartInvsPlugin.manager().getInventory(viewer).ifPresent(inv -> inv.close(viewer));
    }

    // TODO External update view

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}
