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
import java.util.logging.Logger;

public class Main extends JavaPlugin {

	private boolean earlyDisable = false;

	private File waypointFolder = new File(getDataFolder(), "data/"),
			config = new File(getDataFolder(), "config.yml"),
			translationFolder = new File(getDataFolder(), "translations/");

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
				getSLF4JLogger().error("Can't copy config file!", e);
			}
		}
		if (!Config.load(config)) {
			earlyDisable = true;
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if (!translationFolder.exists()) {
			translationFolder.mkdirs();
			try (FileOutputStream fos = new FileOutputStream(new File(translationFolder, "messages_de.cfg"))) {
				fos.getChannel().transferFrom(Channels.newChannel(getResource("messages_de.cfg")), 0, Long.MAX_VALUE);
			} catch (IOException e) {
				getSLF4JLogger().error("Can't copy german message file!", e);
			}
			try (FileOutputStream fos = new FileOutputStream(new File(translationFolder, "messages_en.cfg"))) {
				fos.getChannel().transferFrom(Channels.newChannel(getResource("messages_en.cfg")), 0, Long.MAX_VALUE);
			} catch (IOException e) {
				getSLF4JLogger().error("Can't copy english message file!", e);
			}
		}
		if (!Messages.parse(new File(translationFolder, "messages_" + Config.language.toLowerCase() + ".cfg"))) {
			earlyDisable = true;
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		getCommand("waypoints").setExecutor(new WaypointCommand());
		WaypointStorage.load(waypointFolder);
		PointerManager.activateTimer(this);
	}

	@Override
	public void onDisable() {
		if (earlyDisable) return;
		WaypointStorage.save(waypointFolder);
	}
}
