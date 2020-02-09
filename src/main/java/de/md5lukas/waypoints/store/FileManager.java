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

package de.md5lukas.waypoints.store;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.UUID;

public class FileManager {

	private final Plugin plugin;
	private final File globalStore;

	public FileManager(Plugin plugin) {
		this.plugin = plugin;
		globalStore = new File(plugin.getDataFolder(), "globalstore.nbt");
	}

	public File getMessageFolder() {
		return new File(plugin.getDataFolder(), "lang/");
	}

	public File getGlobalStore() {
		return  globalStore;
	}

	public File getPlayerStore(UUID uuid) {
		return new File(plugin.getDataFolder(), "playerdata/" + uuid + ".nbt");
	}
}
