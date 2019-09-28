package de.md5lukas.waypoints;

import de.md5lukas.commons.messages.MessageStore;
import de.md5lukas.nbt.Tags;
import de.md5lukas.waypoints.display.WaypointDisplay;
import de.md5lukas.waypoints.store.FileManager;
import de.md5lukas.waypoints.store.GlobalStore;
import fr.minuskube.inv.SmartInvsPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.List;
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
		try {
			extractFile(new File(fileManager.getMessageFolder(), "messages_en.msg"), "messages_en.msg");
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Couldn't extract message files", e);
			inOnEnableDisable = true;
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		messageStore = new MessageStore(fileManager.getMessageFolder(), "messages_%s.msg", Messages.class);
		try {
			globalStore = new GlobalStore(this);
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Couldn't load global store, shutting down plugin", e);
			inOnEnableDisable = true;
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		WaypointDisplay.activateDisplays();
	}

	@Override
	public void onDisable() {
		disabled = true;
		SmartInvsPlugin.deleteStaticReferences();
		if (inOnEnableDisable)
			return;
	}

	@SuppressWarnings("ConstantConditions")
	private void extractFile(File destination, String resource) throws IOException {
		if (destination.exists()) {
			try (FileOutputStream fos = new FileOutputStream(destination)) {
				fos.getChannel().transferFrom(Channels.newChannel(getResource(resource)), 0, Long.MAX_VALUE);
			}
		}
	}
}
