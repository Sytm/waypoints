package de.md5lukas.wp.config;

import de.md5lukas.wp.Main;
import org.bukkit.ChatColor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Messages {

	private static Map<String, String> messages;

	public static String get(Message message) {
		return messages.get(message.getInFilePath());
	}

	public static boolean parse(File file) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			messages = new HashMap<>();
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#")) continue;
				int index = line.indexOf('=');
				if (index == -1) continue;
				messages.put(line.substring(0, index).toLowerCase(),
						ChatColor.translateAlternateColorCodes('&', line.substring(index + 1).replace("\\n", "\n")));
			}
			if (!messages.containsKey("prefix")) {
				Main.logger().log(Level.SEVERE,"The message file does not contain every translation!");
				return false;
			}
			String prefix = messages.get("prefix");
			messages.keySet().forEach(key -> messages.put(key, messages.get(key).replace("%prefix%", prefix)));
			for (Message m : Message.values()) {
				if (!messages.containsKey(m.getInFilePath())) {
					Main.logger().log(Level.SEVERE,"The message file does not contain every translation!");
					return false;
				}
			}
			return true;
		} catch (FileNotFoundException e) {
			Main.logger().log(Level.SEVERE,"A message file with the specified language does not exist! Maybe it has been renamed or moved. It should be found under plugins/Waypoints/translations/messages_[lang].cfg");
		} catch (IndexOutOfBoundsException e) {
			Main.logger().log(Level.SEVERE,"The contents of the message file " + file.getAbsolutePath() + " is malformed and cannot be read!");
		} catch (IOException e) {
			Main.logger().log(Level.SEVERE,"An error occurred while reading the file " + file.getAbsolutePath(), e);
		}
		return false;
	}
}
