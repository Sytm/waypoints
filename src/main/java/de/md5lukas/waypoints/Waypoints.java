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

package de.md5lukas.waypoints;

import de.md5lukas.commons.messages.MessageStore;
import de.md5lukas.nbt.Tags;
import de.md5lukas.waypoints.command.WaypointsCommand;
import de.md5lukas.waypoints.display.WaypointDisplay;
import de.md5lukas.waypoints.listener.WaypointsListener;
import de.md5lukas.waypoints.store.*;
import fr.minuskube.inv.SmartInvsPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Waypoints extends JavaPlugin {

	private boolean inOnEnableDisable = false;

	private static Waypoints instance;
	private MessageStore messageStore;
	private FileManager fileManager;
	private GlobalStore globalStore;
	private boolean disabled = false;

	public static Plugin instance() {
		return instance;
	}

	public static Logger logger() {
		return instance.getLogger();
	}

	public static MessageStore messageStore() {
		return instance.messageStore;
	}

	public static String message(Enum<?> message, CommandSender sender) {
		return instance.messageStore.getMessage(message, sender);
	}

	public static FileManager getFileManager() {
		return instance.fileManager;
	}

	public static GlobalStore getGlobalStore() {
		return instance.globalStore;
	}

	public static boolean isDisabled() {
		return instance.disabled;
	}

	@Override
	public void onEnable() {
		instance = this;

		if (detectLegacy()) {
			File config = new File(getDataFolder(), "config.yml"), data = new File(getDataFolder(), "data");
			try {
				Files.move(config.toPath(), new File(getDataFolder(), "config.old.yml").toPath());
				Files.move(data.toPath(), new File(getDataFolder(), "data.old").toPath());
			} catch (IOException e) {
				getLogger().log(Level.SEVERE, "Couldn't move old plugin data", e);
				inOnEnableDisable = true;
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
		}

		setupExternalDependencies();
		loadConfig();

		fileManager = new FileManager(this);

		if (!extractMessages())
			return;
		if (!loadMessages())
			return;
		if (!loadGlobalStore())
			return;

		LegacyImporter.registerLoadedLegacyData();

		WaypointDisplay.activateDisplays();

		getServer().getPluginManager().registerEvents(new WaypointsListener(), this);
		getCommand("waypoints").setExecutor(new WaypointsCommand());
	}

	//<editor-fold defaultstate="collapsed" desc="onEnabled helpers">
	private void setupExternalDependencies() {
		Tags.registerTag(LocationTag::new);
		SmartInvsPlugin.setPlugin(this);
	}

	private boolean detectLegacy() {
		if (new File(getDataFolder(), "data").exists()) {
			getLogger().log(Level.INFO, "Loading legacy stores");
			LegacyImporter.importLegacy(getDataFolder());
			getLogger().log(Level.INFO, "Done!");
			return true;
		}
		return false;
	}

	private void loadConfig() {
		saveDefaultConfig();
		WPConfig.loadConfig(getConfig());
	}

	private boolean extractMessages() {
		saveResource("lang/messages_en.msg", false);
		return true;
	}

	private boolean loadMessages() {
		try {
			messageStore = new MessageStore(fileManager.getMessageFolder(), "messages_%s.msg", Messages.class);
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Couldn't load message files", e);
			inOnEnableDisable = true;
			getServer().getPluginManager().disablePlugin(this);
			return false;
		}
		return true;
	}

	private boolean loadGlobalStore() {
		try {
			globalStore = new GlobalStore(this);
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Couldn't load global store, shutting down plugin", e);
			inOnEnableDisable = true;
			getServer().getPluginManager().disablePlugin(this);
			return false;
		}
		return true;
	}
	//</editor-fold>

	@Override
	public void onDisable() {
		disabled = true;
		SmartInvsPlugin.deleteStaticReferences();
		if (inOnEnableDisable)
			return;
		globalStore.save(false);
	}
}
