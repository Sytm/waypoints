package de.md5lukas.wp;

import de.md5lukas.wp.command.WaypointCommand;
import de.md5lukas.wp.storage.WaypointStorage;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

	private static File waypointFolder = new File("plugins/Waypoints/data/");
	private static Main instance;

	public static Main instance() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;
		PluginManager pm = getServer().getPluginManager();
		getCommand("waypoints").setExecutor(new WaypointCommand());
		WaypointStorage.load(waypointFolder);
		PointerManager.activateTimer(this);
	}

	@Override
	public void onDisable() {
		WaypointStorage.save(waypointFolder);
	}
}
