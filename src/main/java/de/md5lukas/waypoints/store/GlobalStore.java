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

package de.md5lukas.waypoints.store;

import de.md5lukas.nbt.NbtIo;
import de.md5lukas.nbt.tags.CompoundTag;
import de.md5lukas.waypoints.Waypoints;
import de.md5lukas.waypoints.data.folder.PermissionFolder;
import de.md5lukas.waypoints.data.folder.PublicFolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.logging.Level;

public class GlobalStore {

	private final Plugin plugin;
	private Location compassTarget;
	private PublicFolder publicFolder;
	private PermissionFolder permissionFolder;

	public GlobalStore(Plugin plugin) throws IOException {
		this.plugin = plugin;
		if (Waypoints.getFileManager().getGlobalStore().exists()) {
			CompoundTag tag = NbtIo.readCompressed(Waypoints.getFileManager().getGlobalStore());
			if (tag.contains("compassTarget"))
				compassTarget = ((LocationTag) tag.get("compassTarget")).value();

			if (tag.contains("publicWaypoints"))
				publicFolder = new PublicFolder(tag.getCompound("publicWaypoints"));

			if (tag.contains("permissionWaypoints"))
				permissionFolder = new PermissionFolder(tag.getCompound("permissionWaypoints"));
		}
		if (publicFolder == null) publicFolder = new PublicFolder();
		if (permissionFolder == null) permissionFolder = new PermissionFolder();


		// Save global store async every 5 minutes
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> save(false), 20 * 60 * 5, 20 * 60 * 5);
	}

	public PublicFolder getPublicFolder() {
		return publicFolder;
	}

	public PermissionFolder getPermissionFolder() {
		return permissionFolder;
	}

	public Location getCompassTarget() {
		return compassTarget;
	}

	public void setCompassTarget(Location compassTarget) {
		this.compassTarget = compassTarget;
	}

	public void save(boolean async) {
		Runnable save = () -> {
			CompoundTag tag = new CompoundTag("globalStore");

			if (compassTarget != null) {
				tag.put("compassTarget", new LocationTag(null, compassTarget));
			}

			try {
				NbtIo.writeCompressed(tag, Waypoints.getFileManager().getGlobalStore());
			} catch (IOException e) {
				Waypoints.logger().log(Level.SEVERE, "Couldn't save the global data store", e);
			}
		};
		if (async) {
			Bukkit.getScheduler().runTaskAsynchronously(plugin, save);
		} else {
			save.run();
		}
	}
}
