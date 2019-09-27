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
