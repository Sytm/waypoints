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

package de.md5lukas.waypoints.data;

import de.md5lukas.commons.data.PlayerStore;
import de.md5lukas.nbt.tags.CompoundTag;
import de.md5lukas.waypoints.Waypoints;
import de.md5lukas.waypoints.config.WPConfig;
import de.md5lukas.waypoints.data.folder.Folder;
import de.md5lukas.waypoints.data.folder.PrivateFolder;
import de.md5lukas.waypoints.data.waypoint.DeathWaypoint;
import de.md5lukas.waypoints.data.waypoint.PrivateWaypoint;
import de.md5lukas.waypoints.data.waypoint.Waypoint;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WPPlayerData {

    private static final Map<UUID, WPPlayerData> cache = new HashMap<>();

    public static WPPlayerData getPlayerData(UUID uuid) {
        return cache.compute(uuid, (id, value) -> {
            // To keep the store in the cache in the commons lib
            PlayerStore playerStore = PlayerStore.getPlayerStore(id);
            if (value == null) {
                value = new WPPlayerData(playerStore);
            }
            return value;
        });
    }

    private final PlayerStore store;
    private final CompoundTag rootTag;
    private final WPPlayerSettings settings;
    private DeathWaypoint deathWaypoint;
    private final List<PrivateFolder> folders;
    private final List<PrivateWaypoint> waypoints;

    private long lastTeleportation;

    public WPPlayerData(PlayerStore store) {
        this.store = store;
        this.rootTag = store.getTag(Waypoints.instance());
        if (rootTag.contains("settings")) {
            settings = new WPPlayerSettings(rootTag.getCompound("settings"));
            folders = rootTag.getList("folders").values().stream().map(tag -> new PrivateFolder((CompoundTag) tag)).collect(Collectors.toList());
            waypoints = rootTag.getList("waypoints").values().stream().map(tag -> new PrivateWaypoint((CompoundTag) tag)).collect(Collectors.toList());
        } else {
            settings = new WPPlayerSettings();
            folders = new ArrayList<>();
            waypoints = new ArrayList<>();
        }
        if (rootTag.contains("deathWaypoint"))
            deathWaypoint = new DeathWaypoint(rootTag.getCompound("deathWaypoint"));

        if (rootTag.contains("lastTeleportation")) {
            this.lastTeleportation = rootTag.getLong("lastTeleportation");
        } else {
            this.lastTeleportation = -1;
        }

        store.onSaveSync(Waypoints.instance(), tag -> {
            tag.putCompound("settings", settings.toCompoundTag());
            tag.putListTag("folders", folders.stream().map(Folder::toCompoundTag).collect(Collectors.toList()));
            tag.putListTag("waypoints", waypoints.stream().map(Waypoint::toCompoundTag).collect(Collectors.toList()));
            if (deathWaypoint != null)
                tag.putCompound("deathWaypoint", deathWaypoint.toCompoundTag());
            tag.putLong("lastTeleportation", lastTeleportation);
            cache.remove(store.getOwner());
        });
    }

    public WPPlayerSettings settings() {
        return settings;
    }

    public List<PrivateFolder> getFolders() {
        return folders;
    }

    public List<PrivateWaypoint> getWaypoints() {
        return waypoints;
    }

    public DeathWaypoint getDeathWaypoint() {
        return deathWaypoint;
    }

    public void setDeath(Location location) {
        this.deathWaypoint = new DeathWaypoint(location);
    }

    /**
     * @param waypoint
     * @param folder   the folder, or null to put into the top level
     */
    public void moveWaypointToFolder(UUID waypoint, UUID folder) {
        if (folder == null) {
            Waypoint foundWaypoint = null;
            for (PrivateFolder f : folders) {
                for (Waypoint wp : f.getWaypoints(Bukkit.getPlayer(store.getOwner()))) {
                    if (wp.getID().equals(waypoint)) {
                        foundWaypoint = wp;
                        break;
                    }
                }
                if (foundWaypoint != null) {
                    f.removeWaypoint(waypoint);
                    break;
                }
            }
            waypoints.add((PrivateWaypoint) foundWaypoint);
        } else {
            PrivateFolder foundFolder = null;
            Waypoint foundWaypoint = null;
            for (Waypoint wp : waypoints) {
                if (wp.getID().equals(waypoint)) {
                    foundWaypoint = wp;
                    break;
                }
            }
            if (foundWaypoint == null) {
                for (PrivateFolder f : folders) {
                    for (Waypoint wp : f.getWaypoints(Bukkit.getPlayer(store.getOwner()))) {
                        if (wp.getID().equals(waypoint)) {
                            foundFolder = f;
                            foundWaypoint = wp;
                            break;
                        }
                    }
                    if (foundWaypoint != null)
                        break;
                }
            }
            if (foundWaypoint != null) {
                Optional<PrivateFolder> targetFolder = folders.stream().filter(pf -> pf.getID().equals(folder)).findFirst();
                if (targetFolder.isPresent()) {
                    if (foundFolder == null) {
                        waypoints.removeIf(wp -> wp.getID().equals(waypoint));
                    } else {
                        foundFolder.removeWaypoint(waypoint);
                    }
                    targetFolder.get().addWaypoint(foundWaypoint);
                }
            }
        }
    }

    public Optional<Waypoint> findWaypoint(Predicate<Waypoint> predicate) {
        Optional<Waypoint> found = waypoints.stream().filter(predicate).map(pw -> (Waypoint) pw).findFirst();
        if (!found.isPresent()) {
            found = folders.stream().flatMap(f -> f.getWaypoints(Bukkit.getPlayer(store.getOwner())).stream()).filter(predicate).findFirst();
        }
        return found;
    }

    public Optional<Folder> findFolder(Predicate<Folder> predicate) {
        return folders.stream().filter(predicate).map(pf -> (Folder) pf).findFirst();
    }

    public void addWaypoint(PrivateWaypoint waypoint) {
        waypoints.add(waypoint);
    }

    public void removeWaypoint(UUID waypoint) {
        if (!waypoints.removeIf(pwp -> pwp.getID().equals(waypoint))) {
            boolean found = false;
            for (PrivateFolder f : folders) {
                for (Waypoint wp : f.getWaypoints(null)) {
                    if (wp.getID().equals(waypoint)) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    f.removeWaypoint(waypoint);
                    break;
                }
            }
        }
    }

    public int countWaypoints() {
        return waypoints.size() + folders.stream().mapToInt(pf -> pf.getWaypoints(null).size()).sum();
    }

    public void addFolder(String name) {
        folders.add(new PrivateFolder(name));
    }

    public void removeFolder(UUID uuid) {
        for (PrivateFolder pf : folders) {
            if (pf.getID().equals(uuid)) {
                pf.getWaypoints(null).forEach(wp -> waypoints.add((PrivateWaypoint) wp));
                break;
            }
        }
        folders.removeIf(pf -> pf.getID().equals(uuid));
    }

    public boolean canTeleport() {
        return WPConfig.getGeneralConfig().getTeleportConfig().getCooldown() <= 0
                || lastTeleportation == -1
                || (lastTeleportation + WPConfig.getGeneralConfig().getTeleportConfig().getCooldown()) <= System.currentTimeMillis();
    }

    public void playerTeleported() {
        this.lastTeleportation = System.currentTimeMillis();
    }

    public long remainingTeleportCooldown() {
        return Math.max(0, (lastTeleportation + WPConfig.getGeneralConfig().getTeleportConfig().getCooldown()) - System.currentTimeMillis());
    }

    public CompoundTag getCustomTag(String usage) {
        return rootTag.getCompound("custom").getCompound(usage);
    }

    public static class WPPlayerSettings {

        private boolean showGlobals;
        private SortMode sortMode;

        public boolean showGlobals() {
            return showGlobals;
        }

        public void showGlobals(boolean showGlobals) {
            this.showGlobals = showGlobals;
        }

        public SortMode sortMode() {
            return sortMode;
        }

        public void sortMode(SortMode sortMode) {
            this.sortMode = sortMode;
        }

        public WPPlayerSettings() {
            showGlobals = true;
            sortMode = SortMode.TYPE;
        }

        public WPPlayerSettings(CompoundTag tag) {
            showGlobals = tag.getBoolean("showGlobals");
            sortMode = SortMode.valueOf(tag.getString("sortMode"));
        }


        public CompoundTag toCompoundTag() {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("showGlobals", showGlobals);
            tag.putString("sortMode", sortMode.name());
            return tag;
        }
    }

    public enum SortMode {

        NAME_ASC(Comparator.comparing(GUISortable::getName)),
        NAME_DESC(Comparator.comparing(GUISortable::getName).reversed()),
        CREATED_ASC(Comparator.comparingLong(GUISortable::createdAt)),
        CREATED_DESC(Comparator.comparingLong(GUISortable::createdAt).reversed()),
        TYPE(Comparator.comparing(GUISortable::getType));

        private final Comparator<GUISortable> comparator;

        SortMode(Comparator<GUISortable> comparator) {
            this.comparator = comparator;
        }

        public Comparator<GUISortable> getComparator() {
            return comparator;
        }
    }
}
