package de.md5lukas.wp.config;

import org.bukkit.ChatColor;
import org.bukkit.Color;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MessagesParser {

	public static Map<String, String> parse(File file) {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			Map<String, String> result = new HashMap<>();
			String line;
			while ((line = reader.readLine()) != null) {
				int index = line.indexOf('=');
				if (index == -1) continue;
				result.put(line.substring(0, index),
						ChatColor.translateAlternateColorCodes('&', line.substring(index + 1).replaceAll("\\\\n", "\n")));
			}
			return result;
		} catch (FileNotFoundException e) {
			System.err.println("A translation file with the specified language does not exist! Maybe it has been renamed or moved. It should be found under plugins/Waypoints/translations/messages_[lang].cfg");
		} catch (IOException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return null;
	}
}
