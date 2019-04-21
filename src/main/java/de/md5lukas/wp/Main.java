package de.md5lukas.wp;

import de.md5lukas.wp.command.WaypointCommand;
import de.md5lukas.wp.config.Config;
import de.md5lukas.wp.config.Messages;
import de.md5lukas.wp.storage.WaypointStorage;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends JavaPlugin {

	private boolean earlyDisable = false;

	private final File waypointFolder = new File(getDataFolder(), "data/");
	private final File config = new File(getDataFolder(), "config.yml");
	private final File translationFolder = new File(getDataFolder(), "translations/");

	private static Logger logger;

	public static Logger logger() {
		return logger;
	}

	@Override
	public void onEnable() {
		getDataFolder().mkdirs();
		logger = getLogger();
		if (!config.exists()) {
			try (FileOutputStream fos = new FileOutputStream(config)) {
				fos.getChannel().transferFrom(Channels.newChannel(getResource("config.yml")), 0, Long.MAX_VALUE);
			} catch (IOException e) {
				getLogger().log(Level.SEVERE, "Can't copy config file!", e);
			}
		}
		if (!Config.load(config)) {
			earlyDisable = true;
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if (!translationFolder.exists())
			translationFolder.mkdirs();
		File germanMessages = new File(translationFolder, "messages_de.cfg");
		File englishMessages = new File(translationFolder, "messages_en.cfg");
		File messageFileInfo = new File(translationFolder, "Message File Info.cfg");

		if (!germanMessages.exists()) {
			try (FileOutputStream fos = new FileOutputStream(germanMessages)) {
				fos.getChannel().transferFrom(Channels.newChannel(getResource("messages_de.cfg")), 0, Long.MAX_VALUE);
			} catch (IOException e) {
				getLogger().log(Level.SEVERE, "Can't copy german message file!", e);
			}
		}
		if (!englishMessages.exists()) {
			try (FileOutputStream fos = new FileOutputStream(englishMessages)) {
				fos.getChannel().transferFrom(Channels.newChannel(getResource("messages_en.cfg")), 0, Long.MAX_VALUE);
			} catch (IOException e) {
				getLogger().log(Level.SEVERE, "Can't copy english message file!", e);
			}
		}
		if (!messageFileInfo.exists()) {
			try (FileOutputStream fos = new FileOutputStream(messageFileInfo)) {
				fos.getChannel().transferFrom(Channels.newChannel(getResource("Message File Info.cfg")), 0, Long.MAX_VALUE);
			} catch (IOException e) {
				getLogger().log(Level.SEVERE, "Can't copy message file info!", e);
			}
		}

		if (!Messages.parse(new File(translationFolder, "messages_" + Config.language.toLowerCase() + ".cfg"))) {
			earlyDisable = true;
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		WaypointStorage.setupFiles(waypointFolder, new File(waypointFolder, "global.wp"), new File(waypointFolder, "permission.wp"));
		if (!WaypointStorage.load()) {
			earlyDisable = true;
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		WaypointStorage.initWaypointChecker(this);

		getCommand("waypoints").setExecutor(new WaypointCommand());
		PointerManager.activateTimer(this);
	}

	@Override
	public void onDisable() {
		if (earlyDisable) return;
		WaypointStorage.save();
	}
}
