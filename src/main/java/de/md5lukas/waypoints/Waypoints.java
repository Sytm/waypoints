package de.md5lukas.waypoints;

import de.md5lukas.commons.messages.MessageStore;
import de.md5lukas.nbt.Tags;
import de.md5lukas.waypoints.store.FileManager;
import de.md5lukas.waypoints.store.GlobalStore;
import fr.minuskube.inv.SmartInvsPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
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
		Tags.registerExtendedTags();
		SmartInvsPlugin.setPlugin(this);
		fileManager = new FileManager(this);
		messageStore = new MessageStore(fileManager.getMessageFolder(), "messages_%s.cfg", Messages.class);
		try {
			globalStore = new GlobalStore(this);
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Couldn't load global store, shutting down plugin", e);
			inOnEnableDisable = true;
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
	}

	@Override
	public void onDisable() {
		disabled = true;
		SmartInvsPlugin.deleteStaticReferences();
		if (inOnEnableDisable)
			return;
	}
}
