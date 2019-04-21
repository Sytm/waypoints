package de.md5lukas.wp.config;

import de.md5lukas.wp.Main;
import org.bukkit.ChatColor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
			String line, prevKey = "";
			StringBuilder prevValue = new StringBuilder();
			boolean isNextLineStart = false;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#") || line.trim().isEmpty()) continue;
				int index = line.indexOf('=');
				if (index > 0) {
					if (prevKey != null)
						if (!prevKey.isEmpty())
							messages.put(prevKey, prevValue.toString());
					prevKey = line.substring(0, index);
					prevValue.setLength(0);
					prevValue.append(line.substring(index + 1));
				} else if ((index = line.indexOf('|')) > 0) {
					if (prevKey != null)
						if (!prevKey.isEmpty())
							messages.put(prevKey, prevValue.toString());
					prevKey = line.substring(0, index);
					prevValue.setLength(0);
					isNextLineStart = true;
				} else if (index == 0) {
					prevValue.append(isNextLineStart ? "" : "\n").append(line.substring(1));
					if (isNextLineStart)
						isNextLineStart = false;
				}
			}
			if (!prevKey.isEmpty())
				messages.put(prevKey, prevValue.toString());
			Map<String, String> replacements = new HashMap<>();
			String variablePrefix = "variable.";
			messages.forEach((key, value) -> {
				if (key.startsWith(variablePrefix)) {
					replacements.put(key.substring(variablePrefix.length()), value);
				}
			});
			replacements.forEach((key, value) -> {
				messages.remove(variablePrefix + key);
				for (Map.Entry<String, String> entry : messages.entrySet()) {
					entry.setValue(entry.getValue().replace("$" + key + "$", value));
				}
			});
			for (Map.Entry<String, String> entry : messages.entrySet()) {
				entry.setValue(ChatColor.translateAlternateColorCodes('&', entry.getValue().replace("\\n", "\n")));
			}
			List<Message> missing = new ArrayList<>();
			for (Message m : Message.values()) {
				if (!messages.containsKey(m.getInFilePath())) {
					missing.add(m);
				}
			}
			if (!missing.isEmpty()) {
				Main.logger().log(Level.SEVERE, "The message file does not contain every translation!");
				for (Message m : missing) {
					Main.logger().log(Level.SEVERE, "'" + m.getInFilePath() + "' is missing in message file");
				}
				return false;
			}
			return true;
		} catch (FileNotFoundException e) {
			Main.logger().log(Level.SEVERE, "A message file with the specified language does not exist! Maybe it has been renamed or moved. It should be found under plugins/Waypoints/translations/messages_[lang].cfg");
		} catch (IndexOutOfBoundsException e) {
			Main.logger().log(Level.SEVERE, "The contents of the message file " + file.getAbsolutePath() + " is malformed and cannot be read!");
		} catch (IOException e) {
			Main.logger().log(Level.SEVERE, "An error occurred while reading the file " + file.getAbsolutePath(), e);
		}
		return false;
	}
}
